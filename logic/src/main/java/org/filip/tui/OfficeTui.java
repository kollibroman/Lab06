package org.filip.tui;

import org.filip.sockets.Office;

public class OfficeTui
{
    public static void main(String[] args)
    {
        Office office = new Office(9001, "localhost", 7001);
        office.start();
    }
}
