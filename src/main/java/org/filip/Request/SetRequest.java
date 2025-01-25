package org.filip.Request;

import lombok.Getter;

@Getter
public class SetRequest
{
    private final String method;
    private String host;
    private int value1;
    private int value2;

    public SetRequest(String method, int value)
    {
        if (!method.equals("sr:") && !method.equals("spi:") && !method.equals("spo:"))
        {
            throw new IllegalArgumentException("Invalid set method");
        }

        this.method = method;
        this.value1 = value;
    }

    public SetRequest(String method, String host, int value)
    {
        if (!method.equals("sj:"))
        {
            throw new IllegalArgumentException("Invalid set method");
        }
        this.method = method;
        this.host = host;
        this.value1 = value;
    }

    public SetRequest(String method, int value1, int value2)
    {
        if (!method.equals("spi:"))
        {
            throw new IllegalArgumentException("Invalid set method");
        }

        this.method = method;
        this.value1 = value1;
        this.value2 = value2;
    }
}