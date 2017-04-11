package redes;

public class PuntoDeVenta {
	String IPPort;
	static final int seats=20;
	static int reserved;
	static int time = 0;

	public PuntoDeVenta(String ip, Vehicle ve) {
		IPPort = ip;
	}
	
	public PuntoDeVenta(String ip){
		IPPort=ip;
	}
	
	public static int available() {
		//System.out.println("cantidad de asientos "+seats);
		//System.out.println("cantidad de reservados "+reserved);
		return seats - reserved;
	}

	public static boolean reserve(int n) {
		if (n <= available()) {
			reserved += n;
			return true;
		}
		return false;
	}

	public static boolean cancel(int n) {
		if (n <= reserved) {
			reserved -= n;
			return true;
		}
		return false;
	}
}
