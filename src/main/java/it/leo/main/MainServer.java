package it.leo.main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import it.leo.main.config.ApplicationConfig;
import it.leo.main.data.connection.ConnectionThread;
import it.leo.main.data.connection.DbConnection;

public class MainServer {

    private static ApplicationConfig appConfig;
    
    public static void main(String[] args) throws IOException {
        appConfig = new ApplicationConfig();

        // Creating connection and workers pools
        ThreadPoolExecutor connectionThreadPool = new ThreadPoolExecutor(ApplicationConfig.CORE_THREAD_POOL_SIZE, 
            ApplicationConfig.MAX_THREAD_POOL_SIZE,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        ThreadPoolExecutor workerThreadPool = new ThreadPoolExecutor(ApplicationConfig.CORE_WORKER_POOL_SIZE, 
            ApplicationConfig.MAX_WORKER_POOL_SIZE,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

        try (ServerSocket ss = new ServerSocket(ApplicationConfig.SERVER_PORT)) {
            Socket clientSocket;
            while (true) { 
                clientSocket = ss.accept();
                // TODO: replace this line: clientSocket.setSoTimeout(ApplicationConfig.CONNECTION_TIMEOUT);
                DbConnection conn = new DbConnection(UUID.randomUUID().toString(), ApplicationConfig.CHARSET, clientSocket, ApplicationConfig.SERVER_VERSION);
                ConnectionThread connectionThread = new ConnectionThread(conn, workerThreadPool, appConfig.getRepository());
                connectionThreadPool.submit(connectionThread);
                System.out.println("Client connected!!");
            }

            // System.out.println("Finished Execution");
        } catch (IOException e) {
            System.err.println("IO Exception: "+e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        connectionThreadPool.close();
        workerThreadPool.close();
    }
}
