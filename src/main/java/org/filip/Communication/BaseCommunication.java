package org.filip.Communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class BaseCommunication
{
    protected ServerSocket serverSocket;
    protected int port;

    protected void startServer() throws IOException
    {
        serverSocket = new ServerSocket(port);

        while (true)
        {
            Socket clientSocket = serverSocket.accept();
            handleRequest(clientSocket);
        }
    }

    protected abstract void handleRequest(Socket clientSocket) throws IOException;

    protected String sendRequest(String host, int port, String request) throws IOException
    {
        try (Socket socket = new Socket(host, port);
         PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

         BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())))
            {

                out.println(request);
                return in.readLine();
            }
    }
}
