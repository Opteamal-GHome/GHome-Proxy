import java.io.Serializable;


public class ProxyTrameD extends ProxyTrame implements Serializable {
	private static final long serialVersionUID = 7190586747266758299L;
	
	private int deviceId;
	private int valeurLue; // data
	
	/**
	 * Constructeur de la classe ProxyTrameD
	 * @param timestamp
	 * @param type
	 * @param deviceId
	 * @param valeurLue
	 */
	public ProxyTrameD (long timestamp, char type, int deviceId) {
		super(timestamp, type);
		this.deviceId = deviceId;
	}
	
	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}
	public void setValeurLue(int valeurLue) {
		this.valeurLue = valeurLue;
	}

	@Override
	public byte[] encodeTrame() {
		byte[] deviceI = Utilitaires.intToByteArray(deviceId);
		byte[] valeurL = Utilitaires.intToByteArray(valeurLue);
		
		return Utilitaires.concat(deviceI, valeurL);
	}
	

	
	
}
