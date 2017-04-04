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
       
    public TCPServer(int Port) throws IOException {
        welcomeSocket = new ServerSocket(Port);
    }
    
    /*Metodo que se encarga de procesar un request del cliente*/
   private void request(String data)throws IOException{
	   System.out.println("request recibe: "+data+".");
        String[] arrayData = data.split(" ");
        String command = arrayData[0];
        Main.parameter = -1; 
        Main.command = command;
        PuntoDeVenta.time++;
        Message message = new Message(PuntoDeVenta.time,Main.pid,PuntoDeVenta.available()); //agregarle el parametro al mensaje
        if(arrayData.length > 1){
            Main.parameter = Integer.parseInt(arrayData[1]);
        }else{//CAMBIE ACA EN CASO DE AVAILABLE SETEA EL PARAMETRO EN LA CANTIDAD DE ASIENTOS DISPONIBLES
        	Main.parameter = PuntoDeVenta.available();
        }
        System.out.println("mensaje request: "+message+".");
        Main.q.add(message);
        // si hay al menos un peer se debe hacer broadcast
        if(Main.peerData.size()>0){
        	UDPServer.broadcast(message, Main.REQUEST);
        }else{
        	checkAndExecute();
        }
        
    }
    
    @Override
    public void run() {
        System.out.println("TCP SERVER ON, port: "+welcomeSocket.getLocalPort());
 
            Socket connectionSocket;
            try {
                connectionSocket = welcomeSocket.accept();
                while (!connectionSocket.isClosed()) {                                      
                    BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                    outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                    clientSentence = inFromClient.readLine()+" "+PuntoDeVenta.available();
                    System.out.println("me llegó: "+clientSentence);
                    request(clientSentence);
                }
                
            } catch (IOException ex) {
                Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        
    }
    
    /*Metodo que ejecuta el codigo correspondiente al request*/
    public static void exec(){        
        switch(Main.command){
            case "available":
                System.out.println("Quedan " + PuntoDeVenta.available() + " lugares");
            break;
            case "reserve":
                if(PuntoDeVenta.reserve(Main.parameter)){
                	System.out.println("Reservaste exitosa");
                }else{
                	System.out.println("No hay suficientes asientos disponibles");
                }
            break;
            case "cancel":
                if(PuntoDeVenta.cancel(Main.parameter)){
                	System.out.println("Cancelacion exitosa");
                }else{
                	System.out.println("Error al cancelar");
                }
            break;
        }
    }
    
    /*Metodo que llama a exec solo si tiene derecho a acceder a la region critica*/
    private void checkAndExecute() throws IOException{
        if (!Main.q.isEmpty() && Main.q.peek().getPid() == Main.pid) {
            Main.q.remove(); 
            exec();
        }
    }
}
