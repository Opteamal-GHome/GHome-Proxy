import java.io.Serializable;


public class ProxyTrame implements Serializable

{
	//public Timestamp timestamp;
	private long timestamp;
	private char type; // 'S' (for status), 'D' (for data)
	
	public char getType() {
		return type;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public void setType(char type) {
		this.type = type;
	}

}
