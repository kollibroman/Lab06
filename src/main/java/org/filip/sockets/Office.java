package org.filip.sockets;

import lombok.Getter;
import org.filip.Request.OrderRequest;
import org.filip.Request.RegisterRequest;
import org.filip.Request.SetRequest;
import org.filip.parser.RequestParser;
import org.filip.parser.RequestSerializer;
import org.filip.sockets.interfaces.IOffice;
import org.filip.sockets.interfaces.ITanker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Office extends Thread implements IOffice
{
    // Konfiguracja połączenia
    private final int port;
    private final String sewagePlantHost;
    private final int sewagePlantPort;

    // Serwer socketowy
    private ServerSocket serverSocket;

    // Zarządzanie cysternami
    private final Map<Integer, TankerDetails> tankers = new ConcurrentHashMap<>();

    // Kolejka zamówień
    private final Queue<OrderRequest> pendingOrders = new ConcurrentLinkedQueue<>();

    // Licznik numerów cystern
    private int nextTankerNumber = 1;

    private static class TankerDetails
    {
        String host;
        int port;
        boolean isReady;

        TankerDetails(String host, int port)
        {
            this.host = host;
            this.port = port;
            this.isReady = false;
        }
    }

    public Office(int port, String sewagePlantHost, int sewagePlantPort)
    {
        this.port = port;
        this.sewagePlantHost = sewagePlantHost;
        this.sewagePlantPort = sewagePlantPort;
    }

    // Metoda startowa serwera
    public void start()
    {
        try
        {
            serverSocket = new ServerSocket(port);
            System.out.println("Biuro uruchomione na porcie: " + port);

            // Wątek obsługi zamówień
            new Thread(this::processOrders).start();

            // Główna pętla akceptacji połączeń
            while (!Thread.currentThread().isInterrupted())
            {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            }
        }
        catch (IOException e)
        {
            System.err.println("Błąd serwera: " + e.getMessage());
        }
    }

    // Obsługa pojedynczego klienta
    private void handleClient(Socket clientSocket)
    {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true))
        {
            String request = in.readLine();
            String response = processRequest(request);
            out.println(response);
        }

        catch (IOException e)
        {
            System.err.println("Błąd obsługi klienta: " + e.getMessage());
        }
    }

    // Przetwarzanie żądań
    private String processRequest(String request)
    {
        var requestObj = RequestParser.parseRequest(request);

        System.out.println("Otrzymano żądanie: " + request);

        //Znaleźć completableFeature i próbować to tym zrobić

        if (requestObj instanceof RegisterRequest registerRequest && registerRequest.getMethod().equals("r:"))
        {
            String host = registerRequest.getHost();
            int port = registerRequest.getPort();
            return String.valueOf(register(host, port));
        }

        else if (requestObj instanceof OrderRequest orderRequest && orderRequest.getMethod().equals("o:"))
        {
            String host = orderRequest.getHost();
            int port = orderRequest.getPort();
            return String.valueOf(order(host, port));
        }

        else if (requestObj instanceof SetRequest setRequest && setRequest.getMethod().equals("sr:"))
        {
            int tankerNumber = setRequest.getPort();
            setReadyToServe(tankerNumber);
            return "1";
        }

        return "-1"; // Nieznane żądanie
    }

    // Przetwarzanie zamówień
    private void processOrders()
    {
        while (!Thread.currentThread().isInterrupted())
        {
            OrderRequest order = pendingOrders.poll();

            if (order != null)
            {
                dispatchOrder(order);
            }

            try
            {
                Thread.sleep(1000); // Opóźnienie między próbami
            }
            catch (InterruptedException e)
            {
                break;
            }
        }
    }

    // Wysłanie zamówienia do cysterny
    private void dispatchOrder(OrderRequest order)
    {
        for (Map.Entry<Integer, TankerDetails> entry : tankers.entrySet())
        {
            if (entry.getValue().isReady)
            {
                try
                {
                    // Przygotowanie żądania do cysterny
                    var setJobRequest = new SetRequest("sj:", order.getHost(), order.getPort());
                    String jobRequest = RequestSerializer.serializeRequest(setJobRequest);

                    // Wysłanie żądania do cysterny
                    Socket socket = new Socket(entry.getValue().host, entry.getValue().port);
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    out.println(jobRequest);
                    String response = in.readLine();

                    System.out.println(response);

                    socket.close();

                    if ("1".equals(response))
                    {
                        // Oznaczenie cysterny jako zajętej
                        entry.getValue().isReady = false;
                        System.out.println("Zlecono zadanie cysternie: " + entry.getKey());
                        break;
                    }
                }

                catch (IOException e)
                {
                    System.err.println("Błąd wysyłania zlecenia: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public int register(String host, int port)
    {
        int tankerNumber = nextTankerNumber++;
        tankers.put(tankerNumber, new TankerDetails(host, port));
        System.out.println("Zarejestrowano cysternę " + tankerNumber);
        return tankerNumber;
    }

    @Override
    public int order(String host, int port)
    {
        pendingOrders.add(new OrderRequest(host, port));
        System.out.println("Przyjęto zamówienie z: " + host + ":" + port);
        return 1; // Zamówienie przyjęte
    }

    @Override
    public void setReadyToServe(int number)
    {
        TankerDetails tanker = tankers.get(number);
        if (tanker != null)
        {
            tanker.isReady = true;

            System.out.println("Cysterna " + number + " gotowa do pracy");
        }
    }
}
