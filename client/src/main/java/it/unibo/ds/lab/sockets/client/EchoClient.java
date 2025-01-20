/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package it.unibo.ds.lab.sockets.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class EchoClient {

    public static void main(String[] args) {
        var host = args[0];
        var port = Integer.parseInt(args[1]);
        try {
            echo(host, port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (InterruptedException ignored) {
            // silently ignored
        }
    }

    public static void echo(String host, int port) throws IOException, InterruptedException {
        Socket server = new Socket();

        System.out.printf("Contacting host %s:%d...\n", host, port);
        // connect to the host, possibly with timeout
        // Connessione al server con timeout di 5 secondi
        server.connect(new InetSocketAddress(host, port), 5000);
        System.out.println("Connection established");

        // start two concurrent activities:
        // - read bytes from stdin and redirect them to the socket's output stream
        Thread sender = new Thread(() -> {
            try (var out = server.getOutputStream()) { // Stream di output del socket, usato per inviare i byte letti da stdin
                int byteRead;
                while ((byteRead = System.in.read()) != -1) { // Per leggere da terminale (stdin) i byte richiesti
                    out.write(byteRead); // Scrive i byte letti sull'OutputStream del socket
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // - read bytes from the socket's input stream and redirect them to stdout
        Thread receiver = new Thread(() -> {
            try (var in = server.getInputStream()) { // Stream di input del socket, usato per ricevere dati letti dal server
                int byteRead;
                while ((byteRead = in.read()) != -1) {
                    System.out.write(byteRead); // Stampa nel terminale (stdout) i byte letti
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        sender.start();
        receiver.start();

        sender.join();
        receiver.join();
    }
}
