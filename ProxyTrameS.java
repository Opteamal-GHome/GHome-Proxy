import java.io.Serializable;


public class ProxyTrameS extends ProxyTrame implements Serializable 
{
	private static final long serialVersionUID = -4671709585012946100L;
	
	private char typeMessage; // add/remove 
	private int deviceId;
	private char typeDevice; // T, H...
	

	/**
	 * Constructeur de la classe ProxyTrameS
	 * @param timestamp
	 * @param type
	 * @param typeMessage
	 * @param deviceId
	 * @param typeDevice
	 */
	public ProxyTrameS (long timestamp, char type, char typeMessage, int deviceId, char typeDevice) {
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
	public String encodeTrame() {
		return "" + typeMessage + deviceId + typeDevice;
	}
}
