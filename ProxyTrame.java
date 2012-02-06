import java.io.Serializable;

/**
 * Classe abstraite ProxyTrame
 */
public abstract class ProxyTrame implements Serializable

{
	//public Timestamp timestamp;
	private long timestamp;
	private char type; // 'S' (for status), 'D' (for data)
	
	/**
	 * Constructeur de la classe ProxyTrame
	 * @param timestamp
	 * @param type
	 */
	public ProxyTrame (long timestamp, char type) {
		this.timestamp = timestamp;
		this.type = type;
	}
	
	public char getType() {
		return type;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public void setType(char type) {
		this.type = type;
	}
	
	/**
	 * Encode les attributs de la classe
	 * @return
	 */
	public abstract String encodeTrame();

}
