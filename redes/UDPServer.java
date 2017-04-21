package redes;

import java.io.DataOutputStream;
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
    // cadena que se retorna al cliente via telnet
    static String toClient;

    public UDPServer(int Port) throws SocketException {
        serverSocket = new DatagramSocket(Port);
        receiveData = new byte[1024];
        sendData = new byte[1024];
        toClient="";
    }

    @Override
    public void run() {
        System.out.println("UDP SERVER ON, port: "+serverSocket.getLocalPort());
        while (true) {
        	toClient="";
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            try {
                serverSocket.receive(receivePacket);
                String sentence = new String(receivePacket.getData());
                sentence=sentence.trim();
                sentence.replace('"', ' ');
                String[] s = sentence.split("-");
                int timemsg=Integer.parseInt(s[1]);
                int pidmsg=Integer.parseInt(s[2]);
                Message msg = new Message(timemsg,pidmsg,PuntoDeVenta.available()); //s[1] = tiempo , s[2]= pid
                switch(s[0]){ //s[0] = comando
                    case Main.REQUEST:    
                        // crea el mensaje nuevo con lo que le llego
                        PuntoDeVenta.time = Math.max(PuntoDeVenta.time, msg.getTime()) + 1;              
                        Main.q.add(msg);
                        reply(msg.getPid());
                    break;
                    case Main.REPLY:
                        PuntoDeVenta.time = Math.max(PuntoDeVenta.time, msg.getTime()) + 1;
                        replyCount++;
                        if(replyCount >= Main.peerData.size()){
                            checkAndExecute();
                        }
                    break;
                    case Main.RELEASE:
                        Main.q.remove();
                        PuntoDeVenta.time = Math.max(PuntoDeVenta.time, msg.getTime()) + 1;
                        
                        //setReserved
                        PuntoDeVenta.setReserved(Integer.parseInt(s[3]));
                        if(replyCount >= Main.peerData.size()){
                            checkAndExecute();	//se fija si es su turno y ejecuta
                        }
                    break;
                
                }

                InetAddress IPAddress = receivePacket.getAddress();
                int port = receivePacket.getPort();
                String capitalizedSentence = sentence.toUpperCase();
                sendData = capitalizedSentence.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                serverSocket.send(sendPacket);
                
                //devolver el resultado del request a telnet 
                DataOutputStream outToClient = new DataOutputStream(Main.tcp.connectionSocket.getOutputStream());
                
                if(toClient.equals("")){
                	outToClient.writeBytes(UDPServer.toClient);
                }else{
                	outToClient.writeBytes(toClient);
                }
            } catch (IOException ex) {
                Logger.getLogger(UDPServer.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }
    
    /*Metodo que ejecuta el codigo correspondiente al request*/
    public static void exec(){
        switch(Main.command){
            /*case "available":
                //System.out.println("Quedan " + PuntoDeVenta.available() + " lugares");
                toClient="Quedan " + PuntoDeVenta.available() + " lugares\n";
            break;*/
            case "reserve":
                if(PuntoDeVenta.reserve(Main.parameter)){
                	//System.out.println("Reservaste exitosa");
                	toClient="Reserva exitosa\n";
                }else{
                	//System.out.println("No hay suficientes asientos disponibles, quedan: "+PuntoDeVenta.available());
                	toClient="No hay sufucientes asientos disponibles, quedan: "+PuntoDeVenta.available()+"\n";
                }
            break;
            case "cancel":
                if(PuntoDeVenta.cancel(Main.parameter)){
                	//System.out.println("Cancelación exitosa");
                	toClient="Cancelación exitosa\n";
                }else{
                	//System.out.println("Error al cancelar");
                	toClient="Error al cancelar\n";
                }
            break;
        }
    }
    
    /*Metodo que llama a exec solo si tiene derecho a acceder a la region critica*/
    private void checkAndExecute() throws IOException{
        if (!Main.q.isEmpty() && Main.q.peek().getPid() == Main.pid) {
            Main.q.remove(); 
            exec();
            release();
        }
    }
    
    /*Metodo que manda release a todos*/
    public static void release() throws IOException {
        replyCount = 0;
        PuntoDeVenta.time++;
        //Message m = new Message(PuntoDeVenta.time,Main.pid, PuntoDeVenta.available());
        Message m = new Message(PuntoDeVenta.time,Main.pid, PuntoDeVenta.getReserved());
        broadcast(m,Main.RELEASE);
    }
    
    /* Metodo que manda reply a quien corresponda,
     * determina mediante pid a que peer debe hacer reply*/
    private void reply(int dst) throws IOException {
        int i;
        // buscar el indice en que se encuentra el peer a cual debe responder mediante su pid
        for (i = 0; !(Main.peerData.get(i).getPid() == dst) && i<Main.peerData.size();i++){};
        
        // si no encuentra tal pid devuelve una excepcion
        if(i>=Main.peerData.size()){
        	throw new IOException("Current Process Id does not belong to any known peer");
        }else{
        	// sino, aumenta el tiempo del punto de venta
            PuntoDeVenta.time++;
            // crea un nuevo mensaje conteniendo el tiempo del punto de venta, el pid local y la cantidad de asientos reservados 
            Message m = new Message(PuntoDeVenta.time,Main.pid,PuntoDeVenta.available());
            // crea un nuevo DatagramSocket
            DatagramSocket clientSocket = new DatagramSocket();
            // obtiene el IP al cual debe enviar el mensaje
            InetAddress IPAddress = InetAddress.getByName(Main.peerData.get(i).getIp());
            // crea el contenido del nuevo mensaje "REPLY-time-pid-"
            String sentence = Main.REPLY+ "-" + m.toString();
            byte[] sendData = new byte[1024];
            sendData = sentence.getBytes();
            // crea un nuevo DatagramPacket con el contenido del mensaje, 
            // su longitud, IP al cual lo enviara y numero de Peer (puerto UDP)
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, Main.peerData.get(i).getUdpPort());
            // envía el mensaje y cierra la conexion
            clientSocket.send(sendPacket);             
            clientSocket.close();
        }
    }
    
    /*Metodo que manda un mensaje a todos los peers*/
    public static void broadcast(Message message, String type) throws IOException{
    	if(Main.peerData.size()>0){
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
    	}else{
        	System.out.println("No hay peers");
        }
    }
}
