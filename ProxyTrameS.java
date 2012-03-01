import java.io.Serializable;
import java.io.UnsupportedEncodingException;

/*
 * Correspond aux trames de status/informations envoyées au serveur GHome
 * Hérite de ProxyTrame
 */
public class ProxyTrameS extends ProxyTrame implements Serializable {
	private static final long serialVersionUID = -4671709585012946100L;

	private char typeMessage; // add/remove (A/R)
	private int deviceId;
	private char typeDevice; // T, H...

	/**
	 * Constructeur de la classe ProxyTrameS
	 * 
	 * @param timestamp
	 * @param type
	 * @param typeMessage
	 * @param deviceId
	 * @param typeDevice
	 */
	public ProxyTrameS(long timestamp, char type, char typeMessage,
			int deviceId, char typeDevice) {
		super(timestamp, type);
		this.typeMessage = typeMessage;
		this.deviceId = deviceId;
		this.typeDevice = typeDevice;
	}

	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}

	public void setTypeMessage(char typeMessage) {
		this.typeMessage = typeMessage;
	}

	public void setTypeDevice(char typeDevice) {
		this.typeDevice = typeDevice;
	}

	public char getTypeDevice() {
		return typeDevice;
	}

	@Override
	public byte[] encodeTrame() throws UnsupportedEncodingException {
		byte[] typeM = ("" + typeMessage).getBytes("UTF-8");
		byte[] deviceI = Utilitaires.intToByteArray(deviceId);
		byte[] typeD = ("" + typeDevice).getBytes("UTF-8");

		// Concatenation
		byte[] tab1 = Utilitaires.concat(typeM, deviceI);
		byte[] tabFinal = Utilitaires.concat(tab1, typeD);

		return tabFinal;
	}
}
