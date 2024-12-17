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
