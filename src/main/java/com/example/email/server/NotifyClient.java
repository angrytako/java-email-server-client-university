package com.example.email.server;

import com.example.email.model.SocketActive;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class NotifyClient extends Thread{
    private ObjectOutputStream outStream;
    private SocketActive socketActive;

    public NotifyClient(ObjectOutputStream outStream, SocketActive socketActive) {
        this.outStream = outStream;
        this.socketActive = socketActive;
    }

    @Override
    public synchronized void run() {
        while(true) {
            try {
                //waith()
                while(socketActive.emailSize()>0) {
                    System.out.println("invio mail");
                    outStream.writeObject(socketActive.popEmail());
                    outStream.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
