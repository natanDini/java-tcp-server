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
- Encrypted communication using Java's built-in TLS/SSL support (javax.net.ssl)
- Server identity validation through a self-signed certificate
- Token-based authentication before message exchange

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
certs/
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

> [!TIP]
> If you want to start another client, just open a new terminal window and run the same client command again.

## Example
Server terminal:
```bash
[server] starting on port 5000 ...
[server] client /127.0.0.1:60678 connected
[server] client /127.0.0.1:60678 disconnected
```

Client terminal:
```bash
[client] connecting to 127.0.0.1:5000 ...
[client] type messages. 'QUIT' to exit.
[client] > hello
[server] > hello  (RTT=1ms)
[client] > QUIT
[server] > BYE  (RTT=1ms)
```

## TLS/SSL — Generating Certificates

Each user must generate their own keystore and truststore, as certificates are private and not shared in the repository.

Run these commands from the project root (Linux/macOS terminal or Windows PowerShell):

### Create server keystore and private key
```bash
keytool -genkeypair -alias serverkey -keyalg RSA -keysize 2048 \
-keystore certs/server.keystore -storepass senha123 -validity 365
```

### Export the public certificate from the keystore
```bash
keytool -export -alias serverkey -keystore certs/server.keystore \
-file certs/server.cer -storepass senha123
```

### Create the client's truststore (to trust the server)
```bash
keytool -import -alias serverkey -file certs/server.cer \
-keystore certs/client.truststore -storepass senha123 -noprompt
```

The files will be generated inside the certs/ folder:

```md
certs/
├── server.keystore
├── server.cer
└── client.truststore
```

> [!IMPORTANT]
> Never commit these certificate files or passwords to public repositories.
They must be generated locally by each user for privacy and authenticity.