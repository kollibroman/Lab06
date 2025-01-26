package org.filip.Request;

import lombok.Getter;

@Getter
public class SetRequest
{
    private final String method;
    private String host;
    private int port;
    private int value2;

    public SetRequest(String method, int value)
    {
        if (!method.equals("sr:") && !method.equals("spi:") && !method.equals("spo:"))
        {
            throw new IllegalArgumentException("Invalid set method");
        }

        this.method = method;
        this.port = value;
    }

    public SetRequest(String method, String host, int value)
    {
        if (!method.equals("sj:"))
        {
            throw new IllegalArgumentException("Invalid set method");
        }
        this.method = method;
        this.host = host;
        this.port = value;
    }

    public SetRequest(String method, int port, int value2)
    {
        if (!method.equals("spi:"))
        {
            throw new IllegalArgumentException("Invalid set method");
        }

        this.method = method;
        this.port = port;
        this.value2 = value2;
    }
}