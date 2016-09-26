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
    String messageType, senderType, senderName;
    
     public Message(String messageType, String senderType, String senderName){
        this.messageType = messageType;
        this.senderType = senderType;
        this.senderName = senderName;
    }
    
    @Override
    public String toString(){
        return ""+messageType+" "+senderType+" "+senderName+"";
    }
}
