package workerServer;

import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.UUID;
import java.util.NoSuchElementException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;


import edu.harvard.cs262.ComputeServer.ComputeServer;
import edu.harvard.cs262.ComputeServer.WorkQueue;
import edu.harvard.cs262.ComputeServer.WorkTask;

public class QueuedServer implements ComputeServer, WorkQueue {
        private Lock lock = new ReentrantLock();
        private Condition notEmpty = lock.newCondition();
        private Hashtable<UUID, ComputeServer> workers;
        private LinkedList<UUID> freeWorkers, busyWorkers;
        private int numFree = 0;
	

	private QueuedServer(){
		super();
		workers = new Hashtable<UUID, ComputeServer>();
		freeWorkers = new LinkedList<UUID>();
		busyWorkers = new LinkedList<UUID>();
	}
	
	/**
	 * Register a {@link ComputerServer} with the work queue, allowing that
	 * server to be assigned {@link WorkTask} objects to perform.
	 * 
	 * @param server
	 *            the {@link ComputeServer} to be added to the queue of workers
	 * @return the {@link UUID} generated to identify the worker; it will be
	 *         used in calls to {@link unregisterWorker}
	 * @throws RemoteException
	 */
	@Override
	public UUID registerWorker(ComputeServer server) throws RemoteException {
		UUID key = UUID.randomUUID();
		lock.lock();
		workers.put(key, server);
		freeWorkers.add(key);
		numFree++;
		System.out.println("Worker " + key.toString() + " registered. " + numFree + " free workers.");
		if (numFree == 1)
		    notEmpty.signal();
		lock.unlock();
		return key;
	}

	/**
	 * Unregister a {@link ComputeServer} from the queue of workers
	 * 
	 * @param workerID
	 *            the {@link UUID} that identifies the server to be removed from
	 *            the queue; this was returned by {@link registerWorker} when the
	 *            server was added to the queue
	 * @return {@code true} if the removal was successful; {@code false} if not
	 * @throws RemoteException
	 */
	@Override
	public boolean unregisterWorker(UUID workerID) throws RemoteException{
		if (null == workers.get(workerID)){
			return true;
		}
		
		lock.lock();
		workers.remove(workerID);
		freeWorkers.remove(workerID);
		busyWorkers.remove(workerID);
		numFree--;
		System.out.println("Worker " + workerID.toString() + " unregistered. " + numFree + " free workers.");
		lock.unlock();
		return true;
	}
	
	/**
	 * Send a {@link WorkTask} to a {@link ComputeServer} to complete the task.
	 * If a worker is not available, it waits until there is one free.
	 *
	 * @param work
	 *            the {@link WorkTask} to be completed
	 * @return the computed answer of the work
	 * @throws RemoteException
	 */
	@Override
	public Object sendWork(WorkTask work) throws RemoteException {
	    lock.lock();
	    System.out.println(numFree + " free workers.");
	    while (numFree == 0) {
		try {
		    notEmpty.await();
		}
		catch (Exception e) {
		    continue;
		}
	    }
	    

	    UUID id = freeWorkers.getFirst();
	    ComputeServer worker = workers.get(id);
	    freeWorkers.remove(id);
	    busyWorkers.add(id);
	    numFree--;
	    lock.unlock();

	    Object result = worker.sendWork(work);
	    lock.lock();
	    freeWorkers.add(id);
	    busyWorkers.remove(id);
	    numFree++;
	    if (numFree == 1)
		notEmpty.signal();
	    lock.unlock();
	    return result;	    
	}

	/**
     * Tell requestor that server has been accessed
     * 
     * @return a true boolean to represent that the server has been accessed
	 * @throws RemoteException
     */
	@Override
	public boolean PingServer() throws RemoteException {
		return true;
	}

	/**
     * Create {@link WorkQueue} to add {@link ComputeServer} objects to
     * so that {@link WorkTask} requests can be completed. Keeps track of 
     * which {@link ComputeServer} objects are free and which are occupied.
     * 
     * @param HOST
     *            the host to look for when getting registry
     * @param PORT
     *            the port to connect to when getting registry
     * @throws QueuedServerException
     */
	public static void main(String args[]) {
	    if (args.length < 2) {
		System.err.println("Usage: make run-wq HOST=host PORT=port");
		System.exit(1);
	    }
	    String hostName = args[0];
	    int port = Integer.parseInt(args[1]);

		try {
		    System.setProperty("java.security.policy", "security.policy");
		    if (System.getSecurityManager()==null){
			System.setSecurityManager(new SecurityManager());
		    }
            
			QueuedServer queuedServer = new QueuedServer();
			WorkQueue stub = (WorkQueue) UnicastRemoteObject.exportObject(queuedServer, 0);

			Registry registry = LocateRegistry.getRegistry(hostName, port);
			registry.bind("WorkQueue", stub);

			System.err.println("Server ready");
		}
		catch (Exception e) {
			System.err.println("QUEUED SERVER EXCEPTION: " + e.toString());
			e.printStackTrace();
		}
	}

}
