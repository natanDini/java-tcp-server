package br.undf.javatcpserver;

import javax.net.ssl.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;

public final class EchoServer {

    private static final String KEYSTORE = "certs/server.keystore";
    private static final String PASSWORD = "senha123";
    private static final String VALID_TOKEN = "12345";

    public static void main(String[] args) throws Exception {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 5001;
        System.out.printf("[server] iniciando servidor seguro na porta %d ...%n", port);

        // Carrega keystore
        KeyStore ks = KeyStore.getInstance("JKS");
        try (FileInputStream fis = new FileInputStream(KEYSTORE)) {
            ks.load(fis, PASSWORD.toCharArray());
        }

        // Configura SSL
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, PASSWORD.toCharArray());
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), null, null);

        SSLServerSocketFactory ssf = sslContext.getServerSocketFactory();
        try (SSLServerSocket server = (SSLServerSocket) ssf.createServerSocket(port)) {
            while (true) {
                try (SSLSocket sock = (SSLSocket) server.accept()) {
                    System.out.printf("[server] cliente %s conectado%n", sock.getRemoteSocketAddress());

                    BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream(), StandardCharsets.UTF_8));
                    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream(), StandardCharsets.UTF_8));

                    // Autenticação por token
                    out.write("AUTH TOKEN: \n");
                    out.flush();
                    String token = in.readLine();
                    if (!VALID_TOKEN.equals(token)) {
                        out.write("AUTH FAILED\n");
                        out.flush();
                        System.out.println("[server] cliente não autorizado.");
                        continue;
                    }
                    out.write("AUTH OK\n");
                    out.flush();

                    String line;
                    while ((line = in.readLine()) != null) {
                        if ("QUIT".equals(line)) {
                            out.write("BYE\n");
                            out.flush();
                            break;
                        }
                        out.write(line + "\n");
                        out.flush();
                    }

                    System.out.printf("[server] cliente %s desconectado%n", sock.getRemoteSocketAddress());
                }
            }
        }
    }
}
