package org.filip.sockets;

import org.filip.sockets.interfaces.IOffice;
import org.filip.sockets.interfaces.ITanker;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Office extends Thread implements IOffice
{
    private int port;

    public Office(int port)
    {
        this.port = port;
    }

    @Override
    public int register(String host, int port)
    {
        return 0;
    }

    @Override
    public int order(String host, int port)
    {
        return 0;
    }

    @Override
    public void setReadyToServe(int tankerNumber)
    {

    }

    @Override
    public void run()
    {
        while(true)
        {
            try
            {
                var socket = new ServerSocket(port);
                var client = socket.accept();

                var in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                var out = new PrintWriter(client.getOutputStream(), true);
            }

            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
