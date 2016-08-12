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
    
    private BufferedWriter out;
    private BufferedReader in;
    
    public IUTServerMain(int port)
    {
        this.port = port;
        System.out.println(port);
        st = new ArrayList<StudentThread>();
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
                msg = new Message(line.split(" ")[0], line.split(" ")[1]);
                if(msg.messageType.equals("IDENTIFICATION"))
                {
                    //if a student has just connected to the server for the first time
                    System.out.println("Client " + count + msg.sender + " connected...");
                    count++;
                    StudentThread t = new StudentThread(socket, msg.sender);
                    new Thread(t).start();
                    st.add(t);

                    //t.start();
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
//        if(args.length != 1)
//        {
//            System.out.println("Usage: java -jar example.jar [port]");
//            return;
//        }
//        port = Integer.parseInt(args[0]);
        IUTServerMain iutserver = new IUTServerMain(port);
        iutserver.start();
    }
    
    class StudentThread implements Runnable
    {
        Socket socket;
        String username;
        Boolean connected = true;
        
        BufferedReader in;
        PrintWriter out;
        
        private StudentThread(Socket socket, String username) throws IOException {
            this.socket = socket;
            this.username = username;
            
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());
      
            System.out.println("Server for " + username + " is started.");
        }

        @Override
        public void run() {
            //Send
            new Thread(new Runnable(){
                    public void run()
                    {
                        while(connected)
                        {
                             {
                                //System.out.println("TEST");
                                out.println("TEST");
                                //Thread.sleep(1000);
                            } 
                        }
                    }
                }).start();
            
            //Receive
            new Thread(new Runnable(){
                    public void run()
                    {
                        while(connected)
                        {
                            //String msg;
                            try {
                                //System.out.println("baal amar");
                                String msg = in.readLine();
                                System.out.println(msg);
                            } catch (IOException ex) {
                                //
                                System.out.println(username + " disconnected");
                                connected = false;
                                //Logger.getLogger(IUTServerMain.class.getName()).log(Level.SEVERE, null, ex);
                            }
//                            try {
//                                Thread.sleep(1000);
//                            } catch (InterruptedException ex) {
//                                Logger.getLogger(IUTServerMain.class.getName()).log(Level.SEVERE, null, ex);
//                            }
                        }
                    }
                }
            ).start();
            
            new Thread(new Runnable(){
                @Override
                public void run() {
                    try {
                        ServerSocket fserversocket = new ServerSocket(9092);
                        Socket fsock = fserversocket.accept();
                        
                        //Specify the file
                        File file = new File("./test.txt");
                        FileInputStream fis = new FileInputStream(file);
                        BufferedInputStream bis = new BufferedInputStream(fis); 

                        //Get socket's output stream
                        OutputStream os = socket.getOutputStream();

                        //Read File Contents into contents array 
                        byte[] contents;
                        long fileLength = file.length(); 

                        long current = 0;

                        long start = System.nanoTime();
                        while(current!=fileLength){ 
                            int size = 10000;
                            if(fileLength - current >= size)
                                current += size;    
                            else{ 
                                size = (int)(fileLength - current); 
                                current = fileLength;
                            } 
                            contents = new byte[size]; 
                            bis.read(contents, 0, size); 
                            os.write(contents);
                            System.out.print("Sending file ... "+(current*100)/fileLength+"% complete!");
                        }   

                        os.flush(); 
                        //File transfer done. Close the socket connection!
                        fserversocket.close();
                        fsock.close();
                        System.out.println("File sent succesfully!");
                            } catch (IOException ex) {
                                Logger.getLogger(IUTServerMain.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                
            }).start();
        }
    }
}
