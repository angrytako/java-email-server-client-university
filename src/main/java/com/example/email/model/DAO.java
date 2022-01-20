package com.example.email.model;

import java.io.*;
import java.util.ArrayList;

public class DAO {


    public void writeEmailToFile(String filePath, EmailComplete email) throws IOException, ClassNotFoundException {
        File userFile = new File(filePath);
        if(userFile.exists() && !userFile.isDirectory()){
            FileInputStream in = new FileInputStream(userFile);
            ObjectInputStream inObjs = new ObjectInputStream(in);
            ArrayList<EmailComplete> availableEmails = (ArrayList<EmailComplete>)inObjs.readObject();
            availableEmails.add(email);
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            ObjectOutputStream storico = new ObjectOutputStream(fileOutputStream);
            storico.writeObject(availableEmails);
            storico.flush();
            storico.close();
            in.close();
            fileOutputStream.close();
        }
        else {
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            ObjectOutputStream storico = new ObjectOutputStream(fileOutputStream);
            ArrayList<EmailComplete> arr = new ArrayList<EmailComplete>();
            arr.add(email);
            storico.writeObject(arr);
            storico.flush();
            storico.close();
            fileOutputStream.close();

        }
    }


}
