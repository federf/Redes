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
		return seats - reserved;
	}

	public boolean reserve(int n) {
		if (n <= available()) {
			//System.out.println("Reservaste " + n);
			reserved += n;
			return true;
		}
		return false;
	}

	public boolean cancel(int n) {
		if (n <= reserved) {
			//System.out.println("Cancelaste " + n);
			reserved -= n;
			return true;
		}
		return false;
	}
}
