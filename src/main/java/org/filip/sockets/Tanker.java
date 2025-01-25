package org.filip.sockets;

import lombok.Getter;
import lombok.Setter;
import org.filip.sockets.interfaces.IOffice;
import org.filip.sockets.interfaces.ISewagePlant;
import org.filip.sockets.interfaces.ITanker;

public class Tanker implements ITanker
{
    @Getter
    @Setter
    private int tankerId = 0;

    @Getter
    @Setter
    private Boolean isBusy = false;

    @Getter
    @Setter
    private int sewageRidSummary = 0;

    private IOffice office;
    private ISewagePlant sewagePlant;

    public Tanker(IOffice office, ISewagePlant sewagePlant)
    {
        this.office = office;
        this.sewagePlant = sewagePlant;

        office.setReadyToServe(tankerId);
    }

    @Override
    public void setJob(String host, int port) throws InterruptedException
    {
        isBusy = true;
        Thread.sleep(5000);

    }
}
