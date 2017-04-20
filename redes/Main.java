package redes;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Comparator;

public class Main {

    public static final String REQUEST = "REQUEST";
    public static final String REPLY = "REPLY";
    public static final String RELEASE = "RELEASE";
    public static PuntoDeVenta pdv;
    public static final int NPROCESSES = 1;
    public static int pid;
    public static int udpPort;
    public static int tcpPort;
    public static ArrayList<PeerData> peerData = new ArrayList<PeerData>();
    public static int parameter = -1; // parametro opcional de las consultas
    public static String command = "";
    
    //Cola de prioridades, donde se encolan consultas, es la estructura que usa el algoritmo distribuido de Lamport para la exclusion mutua
    static SyncQueue<Message> q = new SyncQueue<Message>(10, new Comparator<Message>() {
        public int compare(Message m1, Message m2) {
        	//ordena segun el "tiempo" en que llegan los mensajes
            return (m1.getTime() > m2.getTime()) ? 1
                    : (m1.getTime() < m2.getTime()) ? -1
                    		//en caso de que dos mensajes lleguen al mismo tiempo
                    		//se encola primero el mensaje cuyo PId es mayor
                            : (m1.getPid() > m2.getPid()) ? 1 : -1;
        }
    });

    /*Metodo que lee el archivo de configuracion y carga los arreglos ips y pids*/
    private static void loadConfig(String path) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        try {
            String line = br.readLine();
            String[] fstLine = line.split("-");//fstLine[0]=pid, fstLine[1]=udpPort, fstLine[2]=tcpPort
            pid = Integer.parseInt(fstLine[0]);
            udpPort = Integer.parseInt(fstLine[1]);
            tcpPort = Integer.parseInt(fstLine[2]);
            line = br.readLine();
            while (line != null) {
                String[] sLine = line.split("-");//sLine[0]=ip, sLine[1]=pid, sLine[2]=udpPort, sLine[3]=tcpPort
                peerData.add(new PeerData(sLine[0],Integer.parseInt(sLine[1]), Integer.parseInt(sLine[2]), Integer.parseInt(sLine[3])));
                line = br.readLine();
            }
        } finally {
            br.close();
        }
    }

    /*Metodo principal, inicia los hilos de ejecucion de tcp para el cliente y udp para los peers*/
    public static void main(String[] args) throws IOException, InterruptedException {
    	//loadConfig("config.txt");
        loadConfig("config.txt");
        for (PeerData pd : peerData){
            System.out.println(pd.toString());
        }
        pdv=new PuntoDeVenta(InetAddress.getLocalHost().toString());
        System.out.println("mis datos. id: " + pid +", udpPort: " + udpPort + ", tcpPort: " + tcpPort);
        TCPServer tcp = new TCPServer(tcpPort);
        UDPServer udp = new UDPServer(udpPort);
        tcp.start();
        udp.start();
        
        //wait for threads
        tcp.join();
        udp.join();
    }
}
