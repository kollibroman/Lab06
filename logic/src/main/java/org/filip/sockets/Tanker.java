package org.filip.sockets;


import lombok.Getter;
import org.filip.Communication.BaseSocketHandler;
import org.filip.Request.GetRequest;
import org.filip.Request.RegisterRequest;
import org.filip.Request.SetRequest;
import org.filip.parser.RequestParser;
import org.filip.parser.RequestSerializer;
import org.filip.sockets.interfaces.ITanker;

public class Tanker extends BaseSocketHandler implements ITanker
{
    private final String officeHost;
    private final int officePort;
    private final String sewagePlantHost;
    private final int sewagePlantPort;

    @Getter
    private final int maxCapacity;

    @Getter
    private int currentTankerNumber = -1;

    @Getter
    private int currentLoad = 0;

    public Tanker(int port, String officeHost, int officePort,
                  String sewagePlantHost, int sewagePlantPort, int maxCapacity) {
        super(port);
        this.officeHost = officeHost;
        this.officePort = officePort;
        this.sewagePlantHost = sewagePlantHost;
        this.sewagePlantPort = sewagePlantPort;
        this.maxCapacity = maxCapacity;
    }

    // Rejestracja w biurze podczas startu
    public void registerInOffice()
    {
        var registerRequest = new RegisterRequest(this.officeHost, port);

        String response = sendRequest(officeHost, officePort, RequestSerializer.serializeRequest(registerRequest));
        currentTankerNumber = Integer.parseInt(response);
        System.out.println("Zarejestrowano cysternę o numerze: " + currentTankerNumber);

        currentTankerNumber = Integer.parseInt(response);

        if(currentTankerNumber >= 0)
        {
            var setRequest = new SetRequest("sr:", currentTankerNumber);
            var response2 = sendRequest(officeHost, officePort, RequestSerializer.serializeRequest(setRequest));
        }
    }

    @Override
    protected String handleRequest(String request)
    {
        if(request.contains("sj:"))
        {
            var setRequest = (SetRequest) RequestParser.parseRequest(request);

            String houseHost = setRequest.getHost();
            int housePort = setRequest.getPort();
            setJob(houseHost, housePort);
            return "1"; // Potwierdzenie przyjęcia zlecenia
        }

        return "-1"; // Nieznane żądanie
    }

    @Override
    public void setJob(String houseHost, int housePort)
    {
        try
        {
            var pumpOutRequest = new GetRequest("gp:", (maxCapacity - currentLoad));

            String response = sendRequest(houseHost, housePort, RequestSerializer.serializeRequest(pumpOutRequest));
            System.out.println(response);

            int pumpedVolume = Integer.parseInt(response);

            if (pumpedVolume > 0)
            {
                currentLoad += pumpedVolume;
                System.out.println("Zebrano " + pumpedVolume + " nieczystości");

                transportToSewagePlant();

                var setRequest = new SetRequest("sr:", currentTankerNumber);

                var response1 = sendRequest(officeHost, officePort, RequestSerializer.serializeRequest(setRequest));

                if ("1".equals(response1))
                {
                    System.out.println("Zakończono zlecenie");
                }
            }
        } catch (Exception e) {
            System.err.println("Błąd podczas realizacji zlecenia: " + e.getMessage());
        }
    }

    // Transport nieczystości do oczyszczalni
    private void transportToSewagePlant()
    {
        var setRequest = new SetRequest("spi:", currentTankerNumber, currentLoad);

        String response = sendRequest(sewagePlantHost, sewagePlantPort, RequestSerializer.serializeRequest(setRequest));

        var response1 = sendRequest(sewagePlantHost, sewagePlantPort, RequestSerializer.serializeRequest(new SetRequest("spo:", currentTankerNumber)));
        System.out.println("Aktualny stan: " + response1);

        if ("1".equals(response))
        {
            System.out.println("Dostarczono " + currentLoad + " nieczystości do oczyszczalni");
            currentLoad = 0;

        }
    }
}
