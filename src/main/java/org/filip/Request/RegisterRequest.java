package org.filip.Request;

import lombok.Getter;

@Getter
public class RegisterRequest
{
    private final String method = "r:";
    private final String host;
    private final int port;

    public RegisterRequest(String host, int port)
    {
        this.host = host;
        this.port = port;
    }
}