package redes;

/**
 * 
 * @author
 */
class Message {
	private int time;
	private int state;
	private int pid;

	public Message(int time, int pid, int state) {
		this.time = time;
		this.pid = pid;
		this.state=state;
	}
	
	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}
	
	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	@Override
	public String toString() {
		return time + "-" + pid + "-"+ state;
	}

}
