import java.io.Serializable;
import java.io.UnsupportedEncodingException;

/**
 * Classe abstraite ProxyTrame
 */
public abstract class ProxyTrame implements Serializable

{
	private static final long serialVersionUID = 4418423202442270112L;
	
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
	
	public long getTimestamp() {
		return this.timestamp;
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
	 * @throws UnsupportedEncodingException 
	 */
	public abstract byte[] encodeTrame() throws UnsupportedEncodingException;

}
