package redes;

// debe ser un monitor
// synchronized
public class PuntoDeVenta {
	String IPPort;
	static final int seats=20;
	// debe ser privada
	static private int reserved;
	static int time = 0;
	private static PuntoDeVenta pdv;

	public static PuntoDeVenta SingletonPuntoDeVenta(String ip){
		if(pdv==null){
			pdv=new PuntoDeVenta(ip);
		}else{
			System.out.println("No se puede crear una instancia de PuntoDeVenta porque ya existe una");
		}
		return pdv;
	}
	
	private PuntoDeVenta(String ip){
		IPPort=ip;
	}
	
	public synchronized static void setReserved(int r){
		reserved=r;
	}
	
	public synchronized static int getReserved(){
		return reserved;
	}
	
	public synchronized static int available() {
		//System.out.println("cantidad de asientos "+seats);
		//System.out.println("cantidad de reservados "+reserved);
		return seats - reserved;
	}

	public synchronized static boolean reserve(int n) {
		if (n <= available()) {
			reserved += n;
			return true;
		}
		return false;
	}

	public synchronized static boolean cancel(int n) {
		if (n <= reserved) {
			reserved -= n;
			return true;
		}
		return false;
	}
}
