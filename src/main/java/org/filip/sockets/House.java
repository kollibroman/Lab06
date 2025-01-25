package org.filip.sockets;

import lombok.Getter;
import lombok.Setter;
import org.filip.sockets.interfaces.IHouse;
import org.filip.sockets.interfaces.IOffice;

public class House extends Thread implements IHouse
{
    private final int MAX_SEWAGE = 50;
    private int sewage = 0;

    @Getter
    @Setter
    private String host;

    @Getter
    @Setter
    private int port;

    private IOffice office;

    public House(IOffice office)
    {
        this.office = office;
    }

    public House(String host, int port)
    {
        this.host = host;
        this.port = port;
    }

    private void createSewage()
    {
        sewage++;
    }

    @Override
    public int getPumpOut(int max)
    {
        sewage -= max;
        return sewage;
    }

    @Override
    public void run()
    {
        while(true)
        {
            try
            {
                createSewage();
                Thread.sleep(500);

                if(sewage >= MAX_SEWAGE)
                {
                    System.out.println("Sewage is full");
                    office.order(host, port);
                    break;
                }
            }

            catch(InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
}
