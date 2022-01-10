package com.example.email.Server;

import java.io.FileWriter;
import java.io.IOException;

public class ServeClient extends Thread{

    @Override
    public void run() {
        try {
            FileWriter fileInput = new FileWriter("Email");
            
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
