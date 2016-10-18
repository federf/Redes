package redes;

public class Vehicle {
	static int reserved;
	static int seats;
	int id;
	
	public Vehicle(){
		reserved=0;
		seats=20;
		id=0;
	}
	
	public Vehicle(int r, int s, int i){
		reserved=r;
		seats=s;
		id=i;
	}

	public static int getReserved() {
		return reserved;
	}

	public static void setReserved(int reserved) {
		Vehicle.reserved = reserved;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public static int getSeats() {
		return seats;
	}

	public static int available() {
		return seats - reserved;
	}

	public static boolean reserve(int n) {
		if (n <= available()) {
			System.out.println("Reservaste " + n);
			reserved += n;
			return true;
		}
		return false;
	}

	public static boolean cancel(int n) {
		if (n <= reserved) {
			System.out.println("Cancelaste " + n);
			reserved -= n;
			return true;
		}
		return false;
	}
}
