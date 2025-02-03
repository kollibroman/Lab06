package org.filip.utils;

import lombok.Getter;
import lombok.Setter;

public class TankerDetails
{
    @Getter
    private String host;

    @Getter
    private int port;

    @Getter
    @Setter
    private boolean isReady;

    public TankerDetails(String host, int port)
    {
        this.host = host;
        this.port = port;
        this.isReady = false;
    }
}
