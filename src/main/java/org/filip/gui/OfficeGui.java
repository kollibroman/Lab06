package org.filip.gui;

import org.filip.sockets.Office;

public class OfficeGui
{
    public static void main(String[] args)
    {
        Office office = new Office(9001, "localhost", 7001);
        office.start();
    }
}
