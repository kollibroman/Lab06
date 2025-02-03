package org.filip.tui;

import org.filip.sockets.House;

import java.util.Scanner;

public class HouseTui
{
    public static void main(String[] args)
    {
        var scanner = new Scanner(System.in);

        System.out.println("Podaj port domu: ");
        int port = scanner.nextInt();
        int maxSewage = 50;

        System.out.println("Podaj adres i port biura: ");
        String officeHost = scanner.next();
        int officePort = scanner.nextInt();

        House house = new House(port, maxSewage, officeHost, officePort);

        house.startSimulation();

        house.startServer();
    }
}
