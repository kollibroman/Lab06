package org.filip.gui;

import org.filip.sockets.Tanker;

public class TankerGui
{
    public static void main(String[] args)
    {
        int tankerPort = 6001;
        String officeHost = "localhost";
        int officePort = 9001;
        String sewagePlantHost = "localhost";
        int sewagePlantPort = 7001;
        int maxCapacity = 500;

        Tanker tanker = new Tanker(
                tankerPort, officeHost, officePort,
                sewagePlantHost, sewagePlantPort, maxCapacity
        );

        // Rejestracja w biurze
        tanker.registerInOffice();

        // Uruchomienie serwera
        tanker.startServer();
    }
}
