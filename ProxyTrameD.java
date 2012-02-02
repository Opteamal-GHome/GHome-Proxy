import java.io.Serializable;


public class ProxyTrameD extends ProxyTrame implements Serializable

{
	private int deviceId;
	private int valeurLue; // data
	
	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}
	public void setValeurLue(int valeurLue) {
		this.valeurLue = valeurLue;
	}
}
