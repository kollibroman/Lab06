package org.filip.utils;

import java.net.DatagramSocket;
import java.net.InetAddress;

public class HostUtils
{
    public static String getCurrentDeviceIp()
    {
        String ip = "";
        try(final DatagramSocket socket = new DatagramSocket())
        {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            ip = socket.getLocalAddress().getHostAddress();
            System.out.println(ip);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return ip;
    }
}
