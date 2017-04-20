package redes;

// debe ser un monitor
// synchronized
public class PuntoDeVenta {
	String IPPort;
	static final int seats=20;
	// debe ser privada
	static private int reserved;
	static int time = 0;

	public PuntoDeVenta(String ip){
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
