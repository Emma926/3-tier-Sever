package workerServer;

import edu.harvard.cs262.ComputeServer.WorkTask;
import edu.harvard.cs262.ComputeServer.WorkQueue;
import edu.harvard.cs262.ComputeServer.ComputeServer;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ComputeEngine implements ComputeServer, Serializable {

    private static final long serialVersionUID = 227L;

    /**
     * Tell the requestor that server has been accessed
     * 
     * @return a true boolean to represent that the server has been accessed
     */
    @Override
    public boolean PingServer() {
        return true;
    }

    /**
     * Take in a {@link WorkTask} and, after calculating the answer, return 
     * the answer.
     * 
     * @param w
     *            the {@link WorkTask} to compute
     * @return the computed answer to the task
     */
    @Override
    public Object sendWork(WorkTask w) {
        return w.doWork();
    }

    /**
     * Register a {@link ComputerEngine} with the work queue, allowing that
     * server to be assigned {@link WorkTask} objects to perform.
     * 
     * @param HOST
     *            the host to look for when getting registry
     * @param PORT
     *            the port to connect to when getting registry
     * @throws ComputeEngineException
     */
    public static void main(String[] args) {
	if (args.length < 2) {
	    System.err.println("Usage: make run-compute HOST=host PORT=port");
	    System.exit(1);
	}
        String hostName = args[0];
	int port = Integer.parseInt(args[1]);

        try {
            System.setProperty("java.security.policy", "security.policy");
            if (System.getSecurityManager()==null) {
                System.setSecurityManager(new SecurityManager());
            }

            Registry registry = LocateRegistry.getRegistry(hostName, port);
            WorkQueue wq = (WorkQueue)registry.lookup("WorkQueue");
            wq.registerWorker(new ComputeEngine());
            System.out.println("ComputeEngine bound");
        } catch (Exception e) {
            System.err.println("ComputeEngine exception:");
            e.printStackTrace();
        }
    }
}