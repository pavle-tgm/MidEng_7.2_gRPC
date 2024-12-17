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
            print("Candidate:", candidate.candidateName, "\nVotes:", candidate.votes, "\n")
    except grpc.RpcError as e:
        print(f"RPC failed: {e.code()}: {e.details()}")

if __name__ == "__main__":
    run()
