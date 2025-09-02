# Java TCP Server (Echo "Ping-Pong")

A simple and educational **TCP client-server** project in Java, built with the standard `java.net` library.  
It demonstrates real-time text message exchange where the server echoes back exactly what the client sends, ensuring **low latency, message integrity, and clean session termination**.

---

## Features
- Server accepts client connections (one at a time, sequentially).
- UTF-8 text communication with line termination (`\n`).
- Special command `QUIT` closes the session safely.
- Client measures and prints round-trip time (RTT) for each message.
- Clean, well-structured, and documented code for learning and testing.

---

## Requirements
- Java 21 (or any modern JDK version)
- IntelliJ IDEA or any preferred IDE
- Linux, macOS, or Windows

---

## Project Structure
```md
src/
└── main/java/br/undf/javatcpserver/
├── EchoServer.java
└── EchoClient.java
```

## Compilation
From the project root:
```bash
javac -encoding UTF-8 -d out $(find src -name "*.java")
```

## Running
Start the server:
```bash
java -cp out br.undf.javatcpserver.EchoServer 5000
```

Start the client:
```bash
java -cp out br.undf.javatcpserver.EchoClient 127.0.0.1 5000
```

💡 If you want to start another client, just open a new terminal window and run the same client command again.

## Example
Server terminal:
```csharp
[server] starting on port 5000 ...
[server] client /127.0.0.1:60678 connected
[server] client /127.0.0.1:60678 disconnected
```

Client terminal:
```csharp
[client] connecting to 127.0.0.1:5000 ...
[client] type messages. 'QUIT' to exit.
hello
[client] <- hello  (RTT=1ms)
QUIT
[client] <- BYE  (RTT=1ms)
```