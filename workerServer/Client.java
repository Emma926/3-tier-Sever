package client;

import edu.harvard.cs262.ComputeServer.ComputeServer;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.math.BigDecimal;
import workerServer.Queens;

public class Client {

    /**
     * Send a job to a {@link QueuedServer}, allowing that
     * server to send {@link WorkTask} objects to be performed
     * on {@link ComputeEngine} servers before receiving answer
     * and subsequently printing it.
     * 
     * @param HOST
     *           Determines which hostname to connect to when getting registry
     * @param PORT
     *           Determines which port to connect to when getting registry
     * @param PINUM
     *           Used in determining the job that will be sent to the server
     * @throws ClientException
     */
    public static void main(String args[]) {
	if (args.length < 3) {
	    System.err.println("Usage: make run-client HOST=host PORT=port PINUM=num");
	    System.exit(1);
	}
    
    String hostName = args[0];
	int port = Integer.parseInt(args[1]);
	int q = Integer.parseInt(args[2]);

        try {
            System.setProperty("java.security.policy", "security.policy");
            if (System.getSecurityManager()==null){
                System.setSecurityManager(new SecurityManager());
            }

            Registry registry = LocateRegistry.getRegistry(hostName, port);
            ComputeServer comp = (ComputeServer) registry.lookup("WorkQueue");
       
            Queens task = new Queens(q);
           
            if(comp.PingServer()){
                String queen = (String)comp.sendWork(task);
                System.out.print(queen);
                
            }
            else {
                System.out.println("The server doesn't respond.\n");
            }
        } catch (Exception e) {
            System.err.println("Client exception:");
            e.printStackTrace();
        }
    }
}