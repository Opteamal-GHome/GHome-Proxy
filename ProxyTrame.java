import java.security.Timestamp;


public class ProxyTrame 

{
	public Timestamp timestamp;
	public char type; // 'S' (for status), 'D' (for data)
	public ContenuTrame data;
	
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	public void setType(char type) {
		this.type = type;
	}
	public void setData(ContenuTrame data) {
		this.data = data;
	}

}
