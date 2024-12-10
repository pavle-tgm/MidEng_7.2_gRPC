package election;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class ElectionClient {

    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        ElectionServiceGrpc.ElectionServiceBlockingStub stub = ElectionServiceGrpc.newBlockingStub(channel);

        Election.ElectionResponse response = stub.getElectionData(Election.ElectionRequest.newBuilder()
                .setElectionId("2024-Presidential")  // Beispiel-ID
                .build());

        System.out.println("Election ID: " + response.getElectionId());
        System.out.println("Candidate: " + response.getCandidateName());
        System.out.println("Votes: " + response.getVotes());

        channel.shutdown();
    }
}
