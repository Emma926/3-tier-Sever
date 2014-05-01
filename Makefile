PINUM=25
PORT=1099
HOST=127.0.0.1

# this should be changed to an HTTP server when going across the network
CB=file:./

default:
	javac -d . ./edu/harvard/cs262/ComputeServer/ComputeServer.java ./edu/harvard/cs262/ComputeServer/WorkQueue.java ./edu/harvard/cs262/ComputeServer/WorkTask.java workerServer/Client.java workerServer/Pi.java workerServer/QueuedServer.java workerServer/ComputeEngine.java

run-client:
	java -cp ./ -Djava.rmi.server.useCodebaseOnly=false -Djava.rmi.server.codebase=$(CB) client.Client $(HOST) $(PORT) $(PINUM)

run-wq:
	java -cp ./ -Djava.rmi.server.useCodebaseOnly=false workerServer.QueuedServer $(HOST) $(PORT)

run-compute:
	java -cp ./ -Djava.rmi.server.useCodebaseOnly=false edu.harvard.cs262.ComputeServer.ComputeEngine $(HOST) $(PORT)
