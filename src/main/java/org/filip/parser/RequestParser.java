package org.filip.Request;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestParser {

    public static Object parseRequest(String request) {
        if (request.startsWith("gp:") || request.startsWith("gs:")) {
            return parseGetRequest(request);
        } else if (request.startsWith("sr:") || request.startsWith("sj:") || request.startsWith("spi:") || request.startsWith("spo:")) {
            return parseSetRequest(request);
        } else if (request.startsWith("r:")) {
            return parseRegisterRequest(request);
        } else if (request.startsWith("o:")) {
            return parseOrderRequest(request);
        } else {
            throw new IllegalArgumentException("Invalid request format");
        }
    }

    private static GetRequest parseGetRequest(String request) {
        String[] parts = request.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid get request format");
        }
        String method = parts[0] + ":";
        int value = Integer.parseInt(parts[1]);
        return new GetRequest(method, value);
    }

    private static SetRequest parseSetRequest(String request) {
        String[] parts = request.split(":|,");
        String method = parts[0] + ":";
        if (method.equals("sr:") || method.equals("spi:") || method.equals("spo:")) {
            if (parts.length != 2 && parts.length != 3) {
                throw new IllegalArgumentException("Invalid set request format");
            }
            int value1 = Integer.parseInt(parts[1]);
            if (parts.length == 2) {
                return new SetRequest(method, value1);
            } else {
                int value2 = Integer.parseInt(parts[2]);
                return new SetRequest(method, value1, value2);
            }
        } else if (method.equals("sj:")) {
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid set request format");
            }
            String host = parts[1];
            int value = Integer.parseInt(parts[2]);
            return new SetRequest(method, host, value);
        } else {
            throw new IllegalArgumentException("Invalid set request method");
        }
    }

    private static RegisterRequest parseRegisterRequest(String request) {
        String[] parts = request.split(":|,");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid register request format");
        }
        String host = parts[1];
        int port = Integer.parseInt(parts[2]);
        return new RegisterRequest(host, port);
    }

    private static OrderRequest parseOrderRequest(String request) {
        String[] parts = request.split(":|,");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid order request format");
        }
        String host = parts[1];
        int port = Integer.parseInt(parts[2]);
        return new OrderRequest(host, port);
    }
}