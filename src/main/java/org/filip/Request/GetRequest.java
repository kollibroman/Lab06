package org.filip.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
public class GetRequest
{
    private final String method;
    private final int value;

    public GetRequest(String method, int value)
    {
        if (!method.equals("gp:") && !method.equals("gs:"))
        {
            throw new IllegalArgumentException("Invalid get method");
        }
        this.method = method;
        this.value = value;
    }
}