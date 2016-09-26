/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iut.server;

import java.net.Socket;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tanmaster
 */
public class TeacherThread implements Runnable{
    
    Socket socket;
    String username;
    Boolean connected = true;

    BufferedReader in;
    PrintWriter out;
    
    TeacherThread(Socket socket, String username) throws IOException
    {
        this.socket = socket;
        this.username = username;

        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream());

        System.out.println("Server for " + username + " is started.");
    }

    @Override
    public void run() {
        try {
            //File receive
            new Thread(new Runnable(){
                public void run(){
                    try {
                        ServerSocket fserversocket = new ServerSocket(7092);
                        Socket fsock = fserversocket.accept();
                        byte[] contents = new byte[10000];
                        
                        //Initialize the FileOutputStream to the output file's full path.
                        
                        InputStream is = fsock.getInputStream();
                        DataInputStream din = new DataInputStream(fsock.getInputStream());
                        String filename;
                        filename = din.readUTF();
                        FileOutputStream fos = new FileOutputStream("./" + filename);
                        BufferedOutputStream bos = new BufferedOutputStream(fos);
                        
                        try {
                            is = fsock.getInputStream();
                        } catch (IOException ex) {
                            Logger.getLogger(TeacherThread.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                        //No of bytes read in one read() call
                        int bytesRead = 0;
                        
                        while((bytesRead=is.read(contents))!=-1)
                            bos.write(contents, 0, bytesRead);
                        
                        bos.flush();
                        fsock.close();
                        
                        System.out.println("File saved successfully!");
                    } catch (IOException ex) {
                        Logger.getLogger(TeacherThread.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }).start();
            
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
                            System.out.println("TEACHER " + username + " disconnected");
                            connected = false;
                            //Logger.getLogger(IUTServerMain.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
            ).start();
        } catch(Exception ex){
        }
    }
}
