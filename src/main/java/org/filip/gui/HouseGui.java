package org.filip.gui;

import org.filip.sockets.House;

public class HouseGui
{
    public static void main(String[] args)
    {
        int port = 8001;
        int maxSewage = 50;
        String officeHost = "localhost";
        int officePort = 9001;

        House house = new House(port, maxSewage, officeHost, officePort);

        house.startSimulation();

        house.startServer();
    }
}
