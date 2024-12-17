**EK:**

Ich habe dass in nochmal in Python umgesetzt.

Mit dem Befehl `pip install grpcio grpcio-tools` kannst man gRPC holen.

Die proto-file sieht so aus:

```protobuf
syntax = "proto3";

service ElectionService {
  rpc getElectionData(ElectionRequest) returns (ElectionResponse) {}
}

message ElectionRequest {
  string electionId = 1;
}

message CandidateData {
  string candidateName = 1;
  int32 votes = 2;
}

message ElectionResponse {
  string electionId = 1;
  repeated CandidateData candidates = 3;  // Liste der Kandidaten
}

```

In dieser proto gibt es ein array bzw. list.

Danach wird dann mit diesem Befehl gebuilded:

`python -m grpc_tools.protoc -I. --python_out=. --grpc_python_out=. election.proto`

- **`-I.`**: Das Verzeichnis, in dem die `proto`-Datei liegt.
- **`--python_out`**: Gibt den generierten Python-Code für die Nachrichten aus.
- **`--grpc_python_out`**: Generiert den Code für den gRPC-Service.

Nach dem Kompilieren sollten **zwei Dateien** erstellt werden:

- `election_pb2.py` (Nachrichten und Datentypen)
  
- `election_pb2_grpc.py` (Service-Stub für Server und Client)
  

`election_server.py`

```python
import grpc
from concurrent import futures
import election_pb2
import election_pb2_grpc

class ElectionServiceImpl(election_pb2_grpc.ElectionServiceServicer):

    def getElectionData(self, request, context):
        print(f"Handling getElectionData for electionId: {request.electionId}")

        # Beispiel für Kandidatendaten (normalerweise aus einer Datenbank oder einer API)
        candidates = [
            election_pb2.CandidateData(candidateName="Jane Doe", votes=12345),
            election_pb2.CandidateData(candidateName="John Smith", votes=67890),
            election_pb2.CandidateData(candidateName="Alice Johnson", votes=34567)
        ]

        # Antwort erstellen
        response = election_pb2.ElectionResponse(
            electionId=request.electionId,
            candidates=candidates
        )

        return response

def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    election_pb2_grpc.add_ElectionServiceServicer_to_server(ElectionServiceImpl(), server)
    server.add_insecure_port('[::]:50051')
    server.start()
    print("Election Service is running on port 50051...")
    server.wait_for_termination()

if __name__ == "__main__":
    serve()

```

`election_client.py`

```python
import grpc
import election_pb2
import election_pb2_grpc

def run():
    channel = grpc.insecure_channel('localhost:50051')
    stub = election_pb2_grpc.ElectionServiceStub(channel)

    # Anfrage mit einer Beispiel-Wahl-ID
    request = election_pb2.ElectionRequest(electionId="2024-Presidential")

    try:
        response = stub.getElectionData(request)
        print("Election ID:", response.electionId)
        for candidate in response.candidates:
            print("Candidate:", candidate.candidateName, "Votes:", candidate.votes)
    except grpc.RpcError as e:
        print(f"RPC failed: {e.code()}: {e.details()}")

if __name__ == "__main__":
    run()

```
