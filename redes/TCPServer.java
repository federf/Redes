package redes;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TCPServer extends Thread {

    String clientSentence;
    String capitalizedSentenceTCP;
    ServerSocket welcomeSocket;
    DataOutputStream outToClient;  
       
    public TCPServer() throws IOException {
        welcomeSocket = new ServerSocket(1234);
    }
    
    /*Metodo que se encarga de procesar un request del cliente*/
    /*private void request(String data)throws IOException{
        String[] arrayData = data.split(" ");
        String command = arrayData[0];
        Main.parameter = -1; 
        Main.command = command;
        Terminal.time++;
        Message message = new Message(Terminal.time,Main.pid); //agregarle el parametro al mensaje
        if(arrayData.length > 1){
            Main.parameter = Integer.parseInt(arrayData[1]);
        }
        Main.q.add(message);
        UDPServer.broadcast(message, Main.REQUEST);
    }*/
    
    @Override
    public void run() {
        System.out.println("TCP SERVER ON");
 
            Socket connectionSocket;
            try {
                connectionSocket = welcomeSocket.accept();
                while (!connectionSocket.isClosed()) {                                      
                    BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                    outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                    clientSentence = inFromClient.readLine();
                    //request(clientSentence);
                }
                
            } catch (IOException ex) {
                Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        
    }
}
