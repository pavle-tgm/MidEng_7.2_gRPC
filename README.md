# MidEng 7.2 gRPC Framework

Datum: **03.12.2024**

Verfasser: **Pavle Petrovic**

## Introduction

This exercise is intended to demonstrate the functionality and implementation of Remote Procedure Call (RPC) technology using the Open Source High Performance gRPC Framework **gRPC Frameworks** ([https://grpc.io](https://grpc.io/)). It shows that this framework can be used to develop a middleware system for connecting several services developed with different programming languages.

## Task

**GKv:**
Anpassung des gRPC-Services, damit ein einfaches `ElectionData`-Record übertragen werden kann, mit Hinweisen auf die zu ändernden Teile des Programms.

---

### Anpassungen in der `.proto`-Datei

**Datei:** `election.proto`

```protobuf
syntax = "proto3";
option java_package = "org.example";

service ElectionService {
  rpc getElectionData(ElectionRequest) returns (ElectionResponse) {}
}

message ElectionRequest {
  string electionId = 1;  // ID der Wahl, um Daten anzufordern.
}

message ElectionResponse {
  string electionId = 1;
  string candidateName = 2;
  int32 votes = 3;
}
```

**Änderung:** Neue Service- und Nachrichtendefinitionen für den Austausch von `ElectionData`.

---

### Anpassungen im Server

**Klasse:** `ElectionServiceImpl`

```java
package org.example;

import io.grpc.stub.StreamObserver;

public class ElectionServiceImpl extends ElectionServiceGrpc.ElectionServiceImplBase {

    @Override
    public void getElectionData(ElectionRequest request, StreamObserver<ElectionResponse> responseObserver) {
        System.out.println("Handling getElectionData for electionId: " + request.getElectionId());

        // Beispiel für Daten (können aus einer Datenbank oder API stammen)
        String electionId = request.getElectionId();
        String candidateName = "Jane Doe";
        int votes = 12345;

        // Antwort erstellen
        ElectionResponse response = ElectionResponse.newBuilder()
                .setElectionId(electionId)
                .setCandidateName(candidateName)
                .setVotes(votes)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
```

**Änderung:** Neue Implementierung für die Methode `getElectionData`.

---

### Anpassungen im Server-Starter

**Klasse:** `ElectionServer`

```java
server = ServerBuilder.forPort(50051)
        .addService(new ElectionServiceImpl())  // Neues Service-Objekt registrieren
        .build()
        .start();

server.awaitTermination();
```

**Änderung:** Registrierung von `ElectionServiceImpl` anstelle von `HelloWorldServiceImpl`.

---

### Anpassungen im Client

**Klasse:** `ElectionClient`

```java
package org.example;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class ElectionClient {

    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        ElectionServiceGrpc.ElectionServiceBlockingStub stub = ElectionServiceGrpc.newBlockingStub(channel);

        ElectionResponse response = stub.getElectionData(ElectionRequest.newBuilder()
                .setElectionId("2024-Presidential")  // Beispiel-ID
                .build());

        System.out.println("Election ID: " + response.getElectionId());
        System.out.println("Candidate: " + response.getCandidateName());
        System.out.println("Votes: " + response.getVotes());

        channel.shutdown();
    }
}
```

**Änderung:** Der Client verwendet die neue Methode `getElectionData`, um Wahlinformationen abzurufen.

---

Durch diese Änderungen kann der gRPC-Service nun `ElectionData`-Daten zwischen Server und Client austauschen.