package org.filip.sockets;

import lombok.Getter;
import org.filip.Communication.BaseSocketHandler;
import org.filip.Request.GetRequest;
import org.filip.Request.SetRequest;
import org.filip.parser.RequestParser;
import org.filip.sockets.interfaces.ISewagePlant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SewagePlant extends BaseSocketHandler implements ISewagePlant
{
    @Getter
    private final ConcurrentMap<Integer, Integer> tankerVolumes = new ConcurrentHashMap<>();
    private final String officeHost;
    private final int officePort;

    public SewagePlant(int port, String officeHost, int officePort) {
        super(port);
        this.officeHost = officeHost;
        this.officePort = officePort;
    }


    @Override
    protected String handleRequest(String request)
    {
        if (request.contains("spi: "))
        {
            var setRequest = (SetRequest) RequestParser.parseRequest(request);
            int tankerNumber = setRequest.getPort();
            int volume = setRequest.getValue2();
            setPumpIn(tankerNumber, volume);
            return "1";
        }

        else if (request.contains("gs:"))
        {
            var getRequest = (GetRequest) RequestParser.parseRequest(request);
            int tankerNumber = getRequest.getValue();
            return String.valueOf(getStatus(tankerNumber));
        }

        else if (request.contains("spo:"))
        {
            var setRequest = (SetRequest) RequestParser.parseRequest(request);
            int tankerNumber = setRequest.getPort();
            setPayOff(tankerNumber);

            return "1"; // Potwierdzenie rozliczenia
        }

        return "-1"; // Nieznane żądanie
    }

    @Override
    public int setPumpIn(int tankerNumber, int volume)
    {
        tankerVolumes.merge(tankerNumber, volume, Integer::sum);

        System.out.println("Cysterna " + tankerNumber + " dostarczyła " +
                volume + " jednostek nieczystości");

        return 1;
    }

    @Override
    public int getStatus(int tankerNumber)
    {
        // Zwraca sumaryczną objętość nieczystości dla danej cysterny
        return tankerVolumes.getOrDefault(tankerNumber, 0);
    }

    @Override
    public void setPayOff(int tankerNumber)
    {
        int totalVolume = tankerVolumes.getOrDefault(tankerNumber, 0);

        System.out.println("Rozliczenie cysterny " + tankerNumber +
                ". Łączna objętość: " + totalVolume);

        tankerVolumes.put(tankerNumber, 0);
    }
}
