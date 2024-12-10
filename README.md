# MidEng 7.2 gRPC Framework

Datum: **03.12.2024**

Verfasser: **Pavle Petrovic**

## Introduction

This exercise is intended to demonstrate the functionality and implementation of Remote Procedure Call (RPC) technology using the Open Source High Performance gRPC Framework **gRPC Frameworks** ([https://grpc.io](https://grpc.io/)). It shows that this framework can be used to develop a middleware system for connecting several services developed with different programming languages.

## Task

**GKÜ:**

1. Erstelle ein Gradle-Projekt in Intellij.

2. Erstelle dann eine Datei `hello.proto` in dem Ordner `src/main/proto`. Mit folgendem Inhalt:

  ```protobuf
  syntax = "proto3";
  option java_package = "org.example";
  service HelloWorldService {
    rpc hello(HelloRequest) returns (HelloResponse) {}
  }
  
  message HelloRequest {
    string firstname = 1;
    string lastname = 2;
  
  }
  
  message HelloResponse {
    string text = 1;
  }
  ```

<u>Anmerkung:</u> Bei dieser Datei ist es wichtig, dass man `ooption java_package = "org.example"` auf Zeile 2 hinschreibt. Denn ohne dem würde der Proto-Compiler nicht den richtigen Ordner finden. Dies kam im Video-Tutorial nicht vor.

Diese **Protocol Buffers (ProtoBuf)**-Datei beschreibt ein einfaches RPC (Remote Procedure Call)-Service mit dem Namen `HelloWorldService`. Hier sind die wichtigsten Aspekte:

**Syntax und Optionen**:

- `syntax = "proto3";`: Gibt an, dass die Datei die Protobuf-Version 3 verwendet. Diese Version ist moderner und einfacher zu verwenden als frühere.
- `option java_package = "org.example";`: Gibt an, in welches Java-Paket die generierte Datei eingebettet wird, wenn Protobuf-Code für Java generiert wird.

**Service**:

- `service HelloWorldService`: Definiert einen Service, der RPC-Methoden enthält.
- `rpc hello(HelloRequest) returns (HelloResponse) {}`: Beschreibt eine RPC-Methode namens `hello`, die ein `HelloRequest`-Objekt empfängt und ein `HelloResponse`-Objekt zurückgibt.

**Messages**:

- `message HelloRequest`: Definiert die Struktur der Eingabe (Request) für die Methode.
    - `string firstname = 1;`: Ein Feld, das den Vornamen enthält. Es hat die Nummer `1`, die in der binären Serialisierung verwendet wird.
    - `string lastname = 2;`: Ein Feld, das den Nachnamen enthält.
- `message HelloResponse`: Definiert die Struktur der Ausgabe (Response).
    - `string text = 1;`: Ein Feld, das den Rückgabetext enthält.

Die Datei legt somit eine klare Schnittstelle für die Kommunikation fest, bei der ein Name übermittelt und eine Begrüßung zurückgegeben wird.

3. In der `build.gradle`-file soll dann dieser Code eingefügt werden:

  ```gradle
  apply plugin: 'java'
  apply plugin: 'com.google.protobuf'
  
  buildscript {
      repositories {
          mavenCentral()
      }
      dependencies {
          classpath 'com.google.protobuf:protobuf-gradle-plugin:0.9.4'
      }
  }
  
  sourceSets {
      main {
          java {
              srcDirs 'build/generated/source/proto/main/grpc'
              srcDirs 'build/generated/source/proto/main/java'
          }
      }
  }
  
  def grpcVersion = '1.29.0'
  
  repositories {
      mavenCentral()
  }
  
  dependencies {
      implementation "io.grpc:grpc-netty:${grpcVersion}"
      implementation "io.grpc:grpc-protobuf:${grpcVersion}"
      implementation "io.grpc:grpc-stub:${grpcVersion}"
      implementation group: 'com.google.protobuf', name: 'protobuf-java-util', version: '3.12.2'
  
      compileOnly group: "javax.annotation", name: "javax.annotation-api", version: "1.3.2"
      compileOnly group: "org.jetbrains", name: "annotations", version: "13.0"
  }
  
  protobuf {
      protoc {
          artifact = 'com.google.protobuf:protoc:3.12.2'
      }
  
      plugins {
          grpc {
              artifact = "io.grpc:protoc-gen-grpc-java:${grpcVersion}"
          }
      }
      generateProtoTasks {
          all()*.plugins {
              grpc {}
          }
      }
  }
  ```

Diese `build.gradle`-Datei ist eine Konfigurationsdatei für ein **Java-Projekt**, das **gRPC** und **Protocol Buffers** verwendet. Hier sind die wichtigsten Punkte:
  
---

### **Plugins**:

- `apply plugin: 'java'`: Aktiviert das Java-Plugin für grundlegende Java-Build-Funktionen.
- `apply plugin: 'com.google.protobuf'`: Aktiviert das Protobuf-Plugin, das für die Verarbeitung von `.proto`-Dateien verantwortlich ist.

  ---

### **Buildscript**:

- Definiert **Repositories** und **Abhängigkeiten**, die für den Build-Prozess benötigt werden.
- `classpath 'com.google.protobuf:protobuf-gradle-plugin:0.9.4'`: Fügt das Protobuf-Gradle-Plugin hinzu, damit Protobuf-Dateien verarbeitet werden können.

  ---

### **SourceSets**:

- Gibt an, wo generierter Code abgelegt wird, sodass der Build-Prozess diese Dateien einbezieht:
    - `build/generated/source/proto/main/grpc`: Enthält generierten gRPC-spezifischen Code.
    - `build/generated/source/proto/main/java`: Enthält generierten Standard-Protobuf-Java-Code.

  ---

### **Abhängigkeiten**:

- **gRPC-Abhängigkeiten**:
    - `io.grpc:grpc-netty:${grpcVersion}`: Implementiert das gRPC-Netzwerkbackend.
    - `io.grpc:grpc-protobuf:${grpcVersion}`: Unterstützt die Nutzung von Protobuf mit gRPC.
    - `io.grpc:grpc-stub:${grpcVersion}`: Ermöglicht die Verwendung von gRPC-Stub-Code.
- **Protobuf-Utility**:
    - `com.google.protobuf:protobuf-java-util:3.12.2`: Bietet zusätzliche Werkzeuge für Protobuf, wie JSON-Konvertierung.
- **Annotationen**:
    - `javax.annotation-api` und `org.jetbrains:annotations`: Stellen Annotationen bereit, die nützlich für den generierten Code sind.

  ---

### **Repositories**:

- `mavenCentral()`: Die Quelle, aus der Abhängigkeiten bezogen werden.

  ---

### **Protobuf-Konfiguration**:

- `protoc`: Gibt die Version des Protobuf-Compilers an (`protoc:3.12.2`).
- **Plugins**:
    - `grpc`: Definiert das gRPC-Plugin für die Protobuf-Generierung (`protoc-gen-grpc-java`).
- **generateProtoTasks**:
    - Steuert die Generierung von Code aus allen definierten `.proto`-Dateien.

Danach kann man mit dem Befehl `./gradlew build` diese File dann builden.

4. Erstelle dann 3 Java-files `HelloWorldClient.java`, `HelloWorldServer.java`, `HelloWorldServiceImpl.java`:

  ---

### **HelloWorldClient**:

  ```java
  ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
          .usePlaintext()
          .build();
  
  HelloWorldServiceGrpc.HelloWorldServiceBlockingStub stub = HelloWorldServiceGrpc.newBlockingStub(channel);
  
  Hello.HelloResponse helloResponse = stub.hello(Hello.HelloRequest.newBuilder()
          .setFirstname("Max")
          .setLastname("Mustermann")
          .build());
  
  System.out.println(helloResponse.getText());
  channel.shutdown();
  ```

**Beschreibung**: Verbindet sich mit dem Server, sendet Vor- und Nachname, empfängt die Antwort und gibt sie aus.
  
---

### **HelloWorldServer**:

  ```java
  server = ServerBuilder.forPort(50051)
          .addService(new HelloWorldServiceImpl())
          .build()
          .start();
  
  server.awaitTermination();
  ```

**Beschreibung**: Startet den Server auf Port `50051`, registriert den Service und wartet auf Anfragen.
  
---

### **HelloWorldServiceImpl**:

  ```java
  @Override
  public void hello(Hello.HelloRequest request, StreamObserver<Hello.HelloResponse> responseObserver) {
      String text = "Hello World, " + request.getFirstname() + " " + request.getLastname();
      Hello.HelloResponse response = Hello.HelloResponse.newBuilder().setText(text).build();
  
      responseObserver.onNext(response);
      responseObserver.onCompleted();
  }
  ```

**Beschreibung**: Verarbeitet die `hello`-Anfrage:

- Erstellt die Begrüßung mit Vor- und Nachname.
- Sendet die Antwort an den Client und beendet die Kommunikation.

5. Starte die Server-Klasse zuerst und danach die Client-Klasse:

Wenn alles funktioniert hat sollte der Client `"Hello World, Max Mustermann"` ausgeben.