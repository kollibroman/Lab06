package org.filip.parser;

public class MessageParser
{
    public static String parseGetMessage(int gp, int gs)
    {
        return "gp:" + gp + "| gs:" + gs;
    }

    public static String parseSetMessage(int sr, int sj, String host, int spi)
    {
        return "sr:" + sr + "| sj:" + host + "," + sj + "| spi:" + spi;
    }

    public static String parseRegisterMessage(int r, String host)
    {
        return "r:" + host + "," + r;
    }

    public static String parseOrderMessage(int o, String host)
    {
        return "o:" + host + "," + o;
    }
}
