package redes;

public class Vehicle {
	// cantidad de asientos reservados
	static int reserved;
	// cantidad de asientos disponibles
	static int seats;
	// identificador del vehiculo (su numero)
	int id;

	public Vehicle() {
		reserved = 0;
		seats = 20;
		id = 1;
	}

	public Vehicle(int r, int s, int i) {
		if (r < s) {
			reserved = r;
		} else {
			reserved = 0;
		}
		if (s > 0) {
		} else {
			seats = 30;
		}
		id = i;
	}
	
	public int available() {
		System.out.println("cantidad de asientos "+seats);
		System.out.println("cantidad de reservados "+reserved);
		return seats - reserved;
	}

	public boolean reserve(int n) {
		if (n <= available()) {
			reserved += n;
			return true;
		}
		return false;
	}

	public boolean cancel(int n) {
		if (n <= reserved) {
			reserved -= n;
			return true;
		}
		return false;
	}
	
	public void setReserved(int res){
		reserved=res;
	}
}
