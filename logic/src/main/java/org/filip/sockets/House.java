package org.filip.sockets;

import lombok.Getter;
import lombok.SneakyThrows;
import org.filip.Communication.BaseSocketHandler;
import org.filip.Request.GetRequest;
import org.filip.Request.OrderRequest;
import org.filip.Request.SetRequest;
import org.filip.parser.RequestParser;
import org.filip.parser.RequestSerializer;
import org.filip.sockets.interfaces.IHouse;

import java.util.Objects;
import java.util.concurrent.*;

public class House extends BaseSocketHandler implements IHouse
{
    @Getter
    private int sewage;
    @Getter
    private final int maxSewage;

    private final String officeHost;
    private final int officePort;

    @Getter
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

    public   void pause()
    {
        isPaused = true;
    }

    public void resume()
    {
        isPaused = false;
    }

    // Obsługa żądań przychodzących na socket
    @Override
    protected String handleRequest(String request)
    {
        if(request.contains("gp:"))
        {
            var getRequest = (GetRequest) RequestParser.parseRequest(request);

            if(Objects.equals(getRequest.getMethod(), "gp:"))
            {
                return String.valueOf(getPumpOut(getRequest.getValue()));
            }
        }
        return "-1"; // Nieznane żądanie
    }

    // Opróżnianie szamba
    @SneakyThrows
    public int getPumpOut(int maxCapacity)
    {
        if (sewage <= 0) return 0;

        int pumpedOut = Math.min(sewage, maxCapacity);
        sewage -= pumpedOut;

        System.out.println("Wypompowano: " + pumpedOut + " z szamba");
        Thread.sleep(3000);
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