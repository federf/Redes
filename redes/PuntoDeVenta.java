package redes;

public class PuntoDeVenta {
	int IPPort;
	static Vehicle v;
	static int time = 0;

	public PuntoDeVenta(int ip, Vehicle ve) {
		IPPort = ip;
		v = ve;
	}
	
	public void setVehicle(Vehicle vhcl){
		v=vhcl;
	}
	
	public static int available(){
        return v.available();
    }
    
    public static boolean reserve(int n){
        return v.reserve(n);
    }
    
    public static boolean cancel(int n){
        return v.cancel(n);
    }
}
