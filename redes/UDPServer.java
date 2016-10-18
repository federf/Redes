package redes;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UDPServer extends Thread {

    static int replyCount = 0;
    DatagramSocket serverSocket;
    byte[] receiveData;
    byte[] sendData;

    public UDPServer() throws SocketException {
        serverSocket = new DatagramSocket(1235);
        receiveData = new byte[1024];
        sendData = new byte[1024];
    }

    @Override
    public void run() {
        System.out.println("UDP SERVER ON");
        while (true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            try {
                serverSocket.receive(receivePacket);
                String sentence = new String(receivePacket.getData());
                String[] s = sentence.split("-");
                //Message msg = new Message(Integer.parseInt(s[1]),Integer.parseInt(s[3])); //s[1] = tiempo , s[3]= pid
                /*switch(s[0]){ //s[0] = comando
                    case Main.REQUEST:    
                         // crea el mensaje nuevo con lo que le llego
                        Terminal.time = Math.max(Terminal.time, msg.getTime()) + 1;              
                        Main.q.add(msg);
                        reply(msg.getPid());
                    break;
                    case Main.REPLY:
                        Terminal.time = Math.max(Terminal.time, msg.getTime()) + 1;
                        replyCount++;
                        if(replyCount >= Main.peerData.size()){  
                            checkAndExecute();
                        }
                        
                    break;
                    case Main.RELEASE:
                        Main.q.remove();
                        Terminal.time = Math.max(Terminal.time, msg.getTime()) + 1;
                        Terminal.reserved = Integer.parseInt(s[2]); // s[2] = estado
                        if(replyCount >= Main.peerData.size()){  
                            checkAndExecute(); //se fija si es su turno y ejecuta 
                        }                   
                    break;
                
                }*/

                InetAddress IPAddress = receivePacket.getAddress();
                int port = receivePacket.getPort();
                String capitalizedSentence = sentence.toUpperCase();
                sendData = capitalizedSentence.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                serverSocket.send(sendPacket);
            } catch (IOException ex) {
                Logger.getLogger(UDPServer.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }
    
    /*Metodo que ejecuta el codigo correspondiente al request*/
    public static void exec(){        
        /*switch(Main.command){
            case "available":
                System.out.println("Quedan " + Terminal.available() + " lugares");
            break;
            case "reserve":
                Terminal.reserve(Main.parameter);
            break;
            case "cancel":
                Terminal.cancel(Main.parameter);
            break;
        }*/
    }
    
    /*Metodo que llama a exec solo si tiene derecho a acceder a la region critica*/
    private void checkAndExecute() throws IOException{
        /*if (!Main.q.isEmpty() && Main.q.peek().getPid() == Main.pid) {
            Main.q.remove(); 
            exec();
            release();
        }*/
    }
    
    /*Metodo que manda release a todos*/
    public static void release() throws IOException {
        /*replyCount = 0;
        Terminal.time++;
        Message m = new Message(Terminal.time,Main.pid,Terminal.reserved);
        broadcast(m,Main.RELEASE);*/
    }
    
    /*Metodo que manda reply a quien corresponda*/
    private void reply(int dst) throws IOException {
        /*int i;
        for (i = 0; !(Main.peerData.get(i).getPid() == dst);i++){};
        Terminal.time++;
        Message m = new Message(Terminal.time,Main.pid);
        DatagramSocket clientSocket = new DatagramSocket();            
        InetAddress IPAddress = InetAddress.getByName(Main.peerData.get(i).getIp());
        String sentence = Main.REPLY+ "-" + m.toString();
        byte[] sendData = new byte[1024];
        sendData = sentence.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, Main.peerData.get(i).getUdpPort());
        clientSocket.send(sendPacket);             
        clientSocket.close();*/  
    }
    
    /*Metodo que manda un mensaje a todos los peers*/
    /*public static void broadcast(Message message, String type) throws IOException{
        for (int i = 0; i < Main.peerData.size(); i++) {
            DatagramSocket clientSocket = new DatagramSocket();            
            InetAddress IPAddress = InetAddress.getByName(Main.peerData.get(i).getIp());
            String sentence = "";
            if(message != null)
                sentence = type+ "-" + message.toString();
            else
                sentence = type+ "-";
            byte[] sendData = new byte[1024];
            sendData = sentence.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, Main.peerData.get(i).getUdpPort());
            clientSocket.send(sendPacket);                
            clientSocket.close();  
        }
    }*/
}
