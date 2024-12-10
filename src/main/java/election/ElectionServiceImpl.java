package election;

import io.grpc.stub.StreamObserver;

public class ElectionServiceImpl extends ElectionServiceGrpc.ElectionServiceImplBase {

    @Override
    public void getElectionData(Election.ElectionRequest request, StreamObserver<Election.ElectionResponse> responseObserver) {
        System.out.println("Handling getElectionData for electionId: " + request.getElectionId());

        // Beispiel für Daten (können aus einer Datenbank oder API stammen)
        String electionId = request.getElectionId();
        String candidateName = "Jane Doe";
        int votes = 12345;

        // Antwort erstellen
        Election.ElectionResponse response = Election.ElectionResponse.newBuilder()
                .setElectionId(electionId)
                .setCandidateName(candidateName)
                .setVotes(votes)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
