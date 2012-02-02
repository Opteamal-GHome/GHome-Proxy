import java.io.Serializable;


public class ProxyTrameS extends ProxyTrame implements Serializable 

{
	private char typeMessage; // add/remove 
	private int deviceId;
	private char typeDevice; // T, H...
	
	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}
	public void setTypeMessage(char typeMessage) {
		this.typeMessage = typeMessage;
	}

	public void setTypeDevice(char typeDevice) {
		this.typeDevice = typeDevice;
	}
	
	
}
