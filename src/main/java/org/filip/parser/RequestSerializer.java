package org.filip.parser;

import org.filip.Request.GetRequest;
import org.filip.Request.OrderRequest;
import org.filip.Request.RegisterRequest;
import org.filip.Request.SetRequest;

public class RequestSerializer
{

    public static String serializeRequest(Object request)
    {
        if (request instanceof GetRequest)
        {
            return serializeGetRequest((GetRequest) request);
        }

        else if (request instanceof SetRequest)
        {
            return serializeSetRequest((SetRequest) request);
        }

        else if (request instanceof RegisterRequest)
        {
            return serializeRegisterRequest((RegisterRequest) request);
        }

        else if (request instanceof OrderRequest)
        {
            return serializeOrderRequest((OrderRequest) request);
        }

        else
        {
            throw new IllegalArgumentException("Invalid request type");
        }
    }

    private static String serializeGetRequest(GetRequest request)
    {
        return request.getMethod() + request.getValue();
    }

    private static String serializeSetRequest(SetRequest request)
    {
        String method = request.getMethod();

        if (method.equals("sr:") || method.equals("spi:") || method.equals("spo:"))
        {
            if (method.equals("spi:"))
            {
                return method + request.getValue1() + "," + request.getValue2();
            }

            else
            {
                return method + request.getValue1();
            }

        }

        else if (method.equals("sj:"))
        {
            return method + request.getHost() + "," + request.getValue1();
        }

        else
        {
            throw new IllegalArgumentException("Invalid set request method");
        }
    }

    private static String serializeRegisterRequest(RegisterRequest request)
    {
        return request.getMethod() + request.getHost() + "," + request.getPort();
    }

    private static String serializeOrderRequest(OrderRequest request)
    {
        return request.getMethod() + request.getHost() + "," + request.getPort();
    }
}