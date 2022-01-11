package com.example.email.server;

import java.io.FileWriter;
import java.io.IOException;

public class ServeClient extends Thread{

    @Override
    public void run() {
        try {
            String name="";
            FileWriter fileInput = new FileWriter("Email"+name);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
