package org.filip.tui;

import org.filip.sockets.Tanker;

import java.util.Scanner;

public class TankerTui
{
    public static void main(String[] args)
    {
        var scanner = new Scanner(System.in);

        System.out.println("Podaj port cysterny: ");
        int tankerPort = scanner.nextInt();

        String officeHost = "localhost";
        int officePort = 9001;
        String sewagePlantHost = "localhost";
        int sewagePlantPort = 7001;
        int maxCapacity = 500;

        Tanker tanker = new Tanker(
                tankerPort, officeHost, officePort,
                sewagePlantHost, sewagePlantPort, maxCapacity
        );

        tanker.registerInOffice();
        tanker.startServer();
    }
}
