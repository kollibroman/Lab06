package org.filip.Request;

import lombok.Getter;

@Getter
public class OrderRequest
{
    private final String method = "o:";
    private final String host;
    private final int port;

    public OrderRequest(String host, int port)
    {
        this.host = host;
        this.port = port;
    }
}