/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iut.server;

import java.io.Serializable;

/**
 *
 * @author tanmaster
 */
public class Message implements Serializable{
    String messageType, sender;
    
     public Message(String messageType, String sender){
        this.messageType = messageType;
        this.sender = sender;
    }
    
    @Override
    public String toString(){
        return ""+messageType+" "+sender+"";
    }
}
