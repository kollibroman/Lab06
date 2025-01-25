package org.filip.sockets;

import lombok.Getter;
import lombok.Setter;
import org.filip.Communication.BaseCommunication;
import org.filip.sockets.interfaces.IHouse;
import org.filip.utils.HostUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class House extends BaseCommunication implements IHouse
{
    private final int MAX_SEWAGE = 50;
    private int sewage = 0;

    @Getter
    @Setter
    private String host;

    @Getter
    @Setter
    private int port;

    private String officeHost;
    private int officePort;


    private void createSewage()
    {
        sewage++;
    }

    @Override
    public int getPumpOut(int max)
    {
        sewage -= max;
        return sewage;
    }


    @Override
    protected void handleRequest(Socket clientSocket) throws IOException
    {
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

        String request = in.readLine();

        if (request.startsWith("gp:"))
        {
            int maxCapacity = Integer.parseInt(request.split(":")[1]);
            int pumpedOut = getPumpOut(maxCapacity);
            out.println(pumpedOut);
        }
    }

    private void requestEmptying() {
        try {
            String request = String.format("o:%s,%d", HostUtils.getCurrentDeviceIp(), port);
            String response = sendRequest(this.officeHost, this.officePort, request);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run()
    {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try {
                startServer();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Symulacja napełniania szamba
        while (true)
        {
            try
            {
                Thread.sleep(1000); // Co 5 sekund zwiększaj poziom
                sewage++;

                if (sewage > MAX_SEWAGE)
                {
                    System.out.println("Szambo jest pełne!");
                    break;
                }
            }
            catch (InterruptedException e)
            {
                break;
            }
        }
    }
}
