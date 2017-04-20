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
    // cadena que se retorna al cliente via telnet
    static String toClient;
       
    public TCPServer(int Port) throws IOException {
        welcomeSocket = new ServerSocket(Port);
        toClient="";
    }
    
    /*Metodo que se encarga de procesar un request del cliente*/
   private void request(String data)throws IOException{
        String[] arrayData = data.split(" ");
        String command = arrayData[0];
        Main.parameter = -1;
        Main.command = command;
        PuntoDeVenta.time++;
        
        //Message message = new Message(PuntoDeVenta.time,Main.pid,PuntoDeVenta.available()); //agregarle el parametro al mensaje
        Message message = new Message(PuntoDeVenta.time,Main.pid);
        if(command.compareTo("available")!=0){
        	
            Main.parameter = Integer.parseInt(arrayData[1]);
        }
        
        // verificamos si es posible realizar una reserva/cancelacion
        // antes de siquiera encolar el request
        // asi evitamos entrar a la zona critica para no hacer ningun cambio
        if(command.compareTo("reserve")==0){
        	if(PuntoDeVenta.available()<Main.parameter){
        		toClient="No es posible reservar "+Main.parameter+", hay "+PuntoDeVenta.available()+" asientos disponibles.\n";
        		return;
        	}
        }
        
        if(command.compareTo("cancel")==0){
        	if(PuntoDeVenta.getReserved()<Main.parameter){
        		toClient="No es posible cancelar "+Main.parameter+", hay "+PuntoDeVenta.getReserved()+" asientos reservados.\n";
        		return;
        	}
        }
        
        
        
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
                    clientSentence = inFromClient.readLine();
                    
                    //devolver el resultado del request a telnet 
                    DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                    
                    request(clientSentence);
                    if(toClient.equals("")){
                    	outToClient.writeBytes(UDPServer.toClient);
                    }else{
                    	outToClient.writeBytes(toClient);
                    }
                    
                    
                }
                
            } catch (IOException ex) {
                Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        
    }
    
    /*Metodo que ejecuta el codigo correspondiente al request*/
    public static void exec(){
        switch(Main.command){
            case "available":
                //System.out.println("Quedan " + PuntoDeVenta.available() + " lugares");
                toClient="Quedan " + PuntoDeVenta.available() + " lugares\n";
            break;
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
        }
    }
}
