package com.example.email.model;

import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DAO {

    private static Map<String,ReadWriteLock> look = new Hashtable<String,ReadWriteLock>();

    /*
    @param sent if true email sent, else email inbox
     */
    public static void writeEmailToFile(String utente, EmailComplete email,Boolean sent) throws IOException, ClassNotFoundException {
        String filePath;
        if (sent){
            filePath = "src/main/resources/email/sent_" + utente + ".txt";
        }
        else{
            filePath = "src/main/resources/email/inbox_" + utente + ".txt";
        }
        ReadWriteLock readWriteLock = writeLock(filePath);
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
            writeUnlock(filePath,readWriteLock);
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
            writeUnlock(filePath,readWriteLock);
        }
    }
    /*
    @param inbox If true email inbox else email sent
     */
    public static ArrayList<EmailComplete> getAllEmails(String utente,Boolean inbox) throws IOException, ClassNotFoundException {
        String userFilePath;
        if (inbox){
            userFilePath = "src/main/resources/email/inbox_" + utente + ".txt";
        }else{
            userFilePath = "src/main/resources/email/sent_" + utente + ".txt";
        }
        ReadWriteLock readWriteLock= readLock(userFilePath);
        File userFile = new File(userFilePath);
        if(userFile.exists() && !userFile.isDirectory()){
            FileInputStream in = new FileInputStream(userFile);
            ObjectInputStream inObjs = new ObjectInputStream(in);
            ArrayList<EmailComplete> inboxEmails = (ArrayList<EmailComplete>)inObjs.readObject();
            in.close();
            readUnlock(userFilePath,readWriteLock);
            return inboxEmails;
        }
        else{
            readUnlock(userFilePath,readWriteLock);
            return new ArrayList<>();
        }

    }




    private static synchronized ReadWriteLock readLock(String userFilePath){
        ReadWriteLock rwl;
        if (look.get(userFilePath)==null){
            rwl = new ReentrantReadWriteLock();
            look.put(userFilePath,rwl);
        }
        else {
            rwl = look.get(userFilePath);
        }
        rwl.readLock().lock();
        return rwl;
    }

    private static synchronized void readUnlock(String userFilePath, ReadWriteLock readWriteLock){
        readWriteLock.readLock().unlock();
        if (look.get(userFilePath)!=null) look.remove(userFilePath);
    }

    private static synchronized ReadWriteLock writeLock(String userFilePath){
        ReadWriteLock rwl;
        if (look.get(userFilePath)==null){
            rwl = new ReentrantReadWriteLock();
            look.put(userFilePath,rwl);
        }
        else {
            rwl = look.get(userFilePath);
        }
        rwl.writeLock().lock();
        return rwl;
    }

    private static synchronized void writeUnlock(String userFilePath, ReadWriteLock readWriteLock){
        readWriteLock.writeLock().unlock();
        if (look.get(userFilePath)!=null) look.remove(userFilePath);
    }



}
