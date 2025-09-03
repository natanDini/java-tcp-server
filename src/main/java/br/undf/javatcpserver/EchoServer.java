package br.undf.javatcpserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public final class EchoServer {

    public static void main(String[] args) {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 5000;

        System.out.printf("[server] iniciando na porta %d ...%n", port);
        try (ServerSocket server = new ServerSocket(port)) {
            while (true) {
                try (Socket sock = server.accept()) {
                    sock.setTcpNoDelay(true);
                    System.out.printf("[server] cliente %s conectado%n", sock.getRemoteSocketAddress());

                    try (BufferedReader in = new BufferedReader(
                            new InputStreamReader(sock.getInputStream(), StandardCharsets.UTF_8));
                         BufferedWriter out = new BufferedWriter(
                                 new OutputStreamWriter(sock.getOutputStream(), StandardCharsets.UTF_8))) {

                        String line;
                        while ((line = in.readLine()) != null) {
                            if ("QUIT".equals(line)) {
                                out.write("BYE\n");
                                out.flush();
                                break;
                            }
                            out.write(line);
                            out.write('\n');
                            out.flush();
                        }
                    }

                    System.out.printf("[server] cliente %s desconectado%n", sock.getRemoteSocketAddress());
                } catch (IOException e) {
                    System.err.println("[server] erro na sessão: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("[server] falha fatal: " + e.getMessage());
            System.exit(1);
        }
    }
}