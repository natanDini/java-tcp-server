package br.undf.javatcpserver;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public final class EchoClient {

    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "127.0.0.1";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 5000;

        System.out.printf("[client] conectando em %s:%d ...%n", host, port);

        try (Socket sock = new Socket(host, port)) {
            sock.setTcpNoDelay(true);

            try (BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
                 BufferedReader in = new BufferedReader(
                         new InputStreamReader(sock.getInputStream(), StandardCharsets.UTF_8));
                 BufferedWriter out = new BufferedWriter(
                         new OutputStreamWriter(sock.getOutputStream(), StandardCharsets.UTF_8))) {

                String line;
                System.out.println("[client] digite mensagens. 'QUIT' encerra.");
                System.out.print("[client] > ");
                while ((line = stdin.readLine()) != null) {
                    long t0 = System.nanoTime();
                    out.write(line);
                    out.write('\n');
                    out.flush();

                    String resp = in.readLine();
                    long t1 = System.nanoTime();

                    if (resp == null) {
                        System.out.println("[client] conexão encerrada pelo servidor.");
                        break;
                    }
                    System.out.printf("[server] > %s  (RTT=%s)%n",
                            resp, Duration.ofNanos(t1 - t0).toMillis() + "ms");

                    if ("QUIT".equals(line)) {
                        break;
                    }
                    System.out.print("[client] > ");
                }
            }
        } catch (IOException e) {
            System.err.println("[client] erro: " + e.getMessage());
            System.exit(1);
        }
    }
}