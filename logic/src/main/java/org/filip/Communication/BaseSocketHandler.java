package org.filip.Communication;

import lombok.Getter;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public abstract class BaseSocketHandler
{
    protected ServerSocket serverSocket;

    @Getter
    protected int port;
    protected ExecutorService executor;

    public BaseSocketHandler(int port)
    {
        this.port = port;
        this.executor = Executors.newCachedThreadPool();
    }

    // Abstrakcyjna metoda obsługi konkretnego żądania
    protected abstract String handleRequest(String request);

    // Główna pętla serwera nasłuchującego na żądania
    public void startServer()
    {
        try
        {
            serverSocket = new ServerSocket(port);
            System.out.println("Serwer uruchomiony na porcie: " + port);

            while (!Thread.currentThread().isInterrupted())
            {
                Socket clientSocket = serverSocket.accept();
                executor.submit(() -> processClientConnection(clientSocket));
            }
        }

        catch (IOException e)
        {
            System.err.println("Błąd serwera: " + e.getMessage());
        }
    }

    // Przetwarzanie pojedynczego połączenia klienta
    private void processClientConnection(Socket clientSocket)
    {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true))
        {
            String request = in.readLine();
            String response = handleRequest(request);
            out.println(response);
        }

        catch (IOException e)
        {
            System.err.println("Błąd podczas obsługi klienta: " + e.getMessage());
        }
    }

    // Wysyłanie żądania do innego serwera
    protected String sendRequest(String host, int port, String request)
    {
        try (
                Socket socket = new Socket(host, port);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())))
        {
            out.println(request);
            return in.readLine();
        }

        catch (IOException e)
        {
            System.err.println("Błąd wysyłania żądania: " + e.getMessage());
            return "-1"; // Domyślna wartość błędu
        }
    }

    public void shutdown()
    {
        try
        {
            if (serverSocket != null) serverSocket.close();
            executor.shutdown();
        }

        catch (IOException e)
        {
            System.err.println("Błąd zamykania serwera: " + e.getMessage());
        }
    }
}