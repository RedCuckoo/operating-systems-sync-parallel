# operating-systems-sync-parallel
Laboratory work for subject "Operating systems". Tasks on synchronization and parallelization.

6. Use Java, processes, sockets (java.nio.channel.SocketChannel), and selector (java.nio.channels.Selector). The main process is a single-threaded socket server.

How to install:
1) clone repository on your computer

	`git clone https://github.com/RedCuckoo/operating-systems-sync-parallel.git`

2) navigate to the folder

	`cd operating-systems-sync-parallel`

3) add lab1.jar to your local maven repository

	`mvn install:install-file -Dfile=lab1 -DgroupId=spos.lab1 -DartifactId=demo -Dversion=1.0 -Dpackaging=jar`

4) navigate to project root folder

	`cd operating-systems-sync-parallel`

5) run maven install

	`mvn install`

6) navigate to created jar folder

	`cd operating-systems-sync-parallel/application/target`

7) run the programm 
	a) with computation cancellation

		java -cp full-application-1.0.jar com.momotenko.os.lab1.manager.ServerManagerAbstract computation_cancellation.ServerManager
	
	b) with periodic user prompt

		java -cp full-application-1.0.jar com.momotenko.os.lab1.manager.ServerManagerAbstract periodic_prompt.ServerManager

Result:
| x | F | F time | G | G time | Result |
|---|---|--------|---|--------|--------|
| 0 | 3 | 1046ms | 5 | 3028ms | 15     |
| 1 | 3 | 3051ms | 5 | 1018ms | 15     |
| 2 | 0 | 3017ms | - | -      | 0      |
| 3 | - | -      | 0 | 3031ms | 0      |
| 4 | 3 | 1029ms | - | inf    | -      |
| 5 | - | inf    | 5 | 1018ms | -      |