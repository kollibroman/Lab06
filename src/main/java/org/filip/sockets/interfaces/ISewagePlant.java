package org.filip.sockets.interfaces;

public interface ISewagePlant
{
    int setPumpIn(int number, int volume);
    int getStatus(int number);
    void setPayOff(int tankerNumber);
}
