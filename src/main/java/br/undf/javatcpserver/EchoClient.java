package br.undf.javatcpserver;

import javax.net.ssl.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.time.Duration;

public final class EchoClient {
    // Cores =D
    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";
    private static final String CYAN = "\u001B[36m";
    private static final String RESET = "\u001B[0m";

    private static final String TRUSTSTORE = "certs/client.truststore";
    private static final String PASSWORD = "senha123";
    private static final String TOKEN = "12345"; // errado: 00000 | certo: 12345

    public static void main(String[] args) throws Exception {
        String host = args.length > 0 ? args[0] : "127.0.0.1";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 5001;
        System.out.printf("[client] conectando com TLS em %s:%d ...%n", host, port);

        // Carrega truststore
        KeyStore ts = KeyStore.getInstance("JKS");
        try (FileInputStream fis = new FileInputStream(TRUSTSTORE)) {
            ts.load(fis, PASSWORD.toCharArray());
        }

        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(ts);
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);

        SSLSocketFactory ssf = sslContext.getSocketFactory();
        try (SSLSocket sock = (SSLSocket) ssf.createSocket(host, port)) {
            sock.startHandshake();
            System.out.println("[client] conexão segura estabelecida.");

            SSLSession session = sock.getSession();
            System.out.println(CYAN + "[TLS] Protocolo: " + session.getProtocol() + RESET);
            System.out.println(CYAN + "[TLS] Cifra usada: " + session.getCipherSuite());
            System.out.println(CYAN + "[TLS] Servidor autenticado como: " + session.getPeerPrincipal());
            System.out.println();

            BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream(), StandardCharsets.UTF_8));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream(), StandardCharsets.UTF_8));
            BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

            // Autenticação
            String prompt = in.readLine();
            System.out.print("[server] " + prompt);
            out.write(TOKEN + "\n");
            out.flush();
            String authResp = in.readLine();
            if (!"AUTH OK".equals(authResp)) {
                System.err.println("[client] falha na autenticação.");
                return;
            }
            System.out.println(GREEN + "[client] autenticado com sucesso." + RESET);
            System.out.print("[client] > ");

            String line;
            while ((line = stdin.readLine()) != null) {
                long t0 = System.nanoTime();
                out.write(line + "\n");
                out.flush();
                String resp = in.readLine();
                long t1 = System.nanoTime();
                if (resp == null) break;
                System.out.printf("[server] > %s (RTT=%dms)%n", resp, Duration.ofNanos(t1 - t0).toMillis());
                if ("QUIT".equals(line)) break;
                System.out.print("[client] > ");
            }
        }
    }
}