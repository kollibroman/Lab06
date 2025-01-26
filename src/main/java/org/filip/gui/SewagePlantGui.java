package org.filip.gui;

import org.filip.sockets.SewagePlant;

public class SewagePlantGui
{
    public static void main(String[] args)
    {
        int sewagePlantPort = 7001;
        String officeHost = "localhost";
        int officePort = 9001;

        SewagePlant sewagePlant = new SewagePlant(sewagePlantPort, officeHost, officePort);

        // Uruchomienie serwera oczyszczalni
        sewagePlant.startServer();
    }
}
