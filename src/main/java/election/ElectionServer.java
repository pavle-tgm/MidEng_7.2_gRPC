package election;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.example.HelloWorldServer;

import java.io.IOException;

public class ElectionServer {
    private static final int PORT = 50051;
    private Server server;

    public void start() throws IOException {
        server = ServerBuilder.forPort(50051)
                .addService(new ElectionServiceImpl())  // Neues Service-Objekt registrieren
                .build()
                .start();
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (server == null) {
            return;
        }
        server.awaitTermination();
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        ElectionServer server = new ElectionServer();
        System.out.println( "Election Service is running!");
        server.start();
        server.blockUntilShutdown();
    }
}
