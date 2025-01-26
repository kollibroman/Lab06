package org.filip.sockets;

import org.filip.Communication.BaseSocketHandler;
import org.filip.Request.GetRequest;
import org.filip.Request.OrderRequest;
import org.filip.parser.RequestParser;
import org.filip.parser.RequestSerializer;
import org.filip.sockets.interfaces.IHouse;

import java.util.Objects;
import java.util.concurrent.*;

public class House extends BaseSocketHandler implements IHouse
{
    private int sewage;
    private final int maxSewage;
    private final String officeHost;
    private final int officePort;

    private boolean isPaused = false;

    public House(int port, int maxSewage, String officeHost, int officePort)
    {
        super(port);
        this.maxSewage = maxSewage;
        this.sewage = 0;
        this.officeHost = officeHost;
        this.officePort = officePort;
    }


    public void startSimulation()
    {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() ->
        {
           if(!isPaused)
           {
               sewage += 10;

               if (sewage >= maxSewage)
               {
                   requestEmptying();
                   pause();
               }

               System.out.println("Poziom szamba: " + sewage);
           }

        }, 0, 3, TimeUnit.SECONDS);
    }

    private  void pause()
    {
        isPaused = true;
    }

    private void resume()
    {
        isPaused = false;
    }

    // Obsługa żądań przychodzących na socket
    @Override
    protected String handleRequest(String request)
    {
        var deserializedRequest = RequestParser.parseRequest(request);

        if(deserializedRequest instanceof GetRequest)
        {
            var getRequest = (GetRequest) deserializedRequest;

            if(Objects.equals(getRequest.getMethod(), "gp:"))
            {
                requestEmptying();
            }
        }
        return "-1"; // Nieznane żądanie
    }

    // Opróżnianie szamba
    public int getPumpOut(int maxCapacity)
    {
        if (sewage <= 0) return 0;

        int pumpedOut = Math.min(sewage, maxCapacity);
        sewage -= pumpedOut;

        System.out.println("Wypompowano: " + pumpedOut + " z szamba");
        resume();
        return pumpedOut;
    }

    // Wysłanie zamówienia do biura
    private void requestEmptying()
    {
        var sjRequest = new OrderRequest(this.officeHost, port);

        String response = sendRequest(officeHost, officePort, RequestSerializer.serializeRequest(sjRequest));

        System.out.println("Odpowiedź biura na zamówienie: " + response);
    }
}