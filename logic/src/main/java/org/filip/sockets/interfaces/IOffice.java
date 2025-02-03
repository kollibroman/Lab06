package org.filip.sockets.interfaces;

public interface IOffice
{
    int register(String host, int port);
    int order(String host, int port);
    void setReadyToServe(int tankerNumber);
}
