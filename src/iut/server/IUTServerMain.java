/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iut.server;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tanmaster
 */
public class IUTServerMain {
    private int port,count;
    private ArrayList<StudentThread> st;
    private ArrayList<TeacherThread> tt;
    
    private BufferedWriter out;
    private BufferedReader in;
    
    public IUTServerMain(int port)
    {
        this.port = port;
        System.out.println(port);
        st = new ArrayList<StudentThread>();
        tt = new ArrayList<TeacherThread>();
        count = 0;
    }
    
    public void start()
    {
        try
        {
            ServerSocket serverSocket = new ServerSocket(port);
            while(true)
            {
                System.out.println("Server waiting for client " + count + " on port " + port);
                Socket socket = serverSocket.accept();
                String line;
                System.out.println("ashcheh");
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                line = in.readLine();
                System.out.println(line);
                Message msg;
                msg = new Message(line.split(" ")[0], line.split(" ")[1], line.split(" ")[2]);
                
                if(msg.messageType.equals("IDENTIFICATION"))
                {
                    //if a student has just connected to the server for the first time
                    if(msg.senderType.equals("STUDENT"))
                    {
                        System.out.println("Student " + count + msg.senderName + " connected...");
                        count++;
                        StudentThread s = new StudentThread(socket, msg.senderName);
                        new Thread(s).start();
                        st.add(s);

                    }
                    if(msg.senderType.equals("TEACHER"))
                    {
                        System.out.println("Teacher " + msg.senderName + " connected...");
                        TeacherThread t = new TeacherThread(socket, msg.senderName);
                        new Thread(t).start();
                        tt.add(t);
                    }
                }
                else if(msg.messageType.equals("FILE_REQUEST"))
                {
                    //students has requested the lab task files
                    for(StudentThread t:st)
                    {
                        if(t.socket.getInetAddress().equals(socket.getInetAddress()))
                        {
                            //send the lab task files to the student who requests
                            fileSend(t.socket);
                        }
                    }
                }
                else if(msg.messageType.equals("TEST"))
                {
                    //Receive ACK messages from client to check its connection
                    System.out.println("still connected");
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(IUTServerMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void fileSend(Socket socket)
    {
        
    }
    
    public static void main(String[] args) {
        int port = 8092;

        IUTServerMain iutserver = new IUTServerMain(port);
        iutserver.start();
    }
    
}
