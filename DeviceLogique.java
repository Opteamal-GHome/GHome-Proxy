
public class DeviceLogique {
	private int idLogique;
	private char typeLogique;
	private DevicePhysique devicePhysique;
	
	public DeviceLogique(int idLogique, char typeLogique,DevicePhysique devicePhysique) {
		this.idLogique = idLogique;
		this.typeLogique = typeLogique;
		this.devicePhysique = devicePhysique;
	}

	public int getIdLogique() {
		return idLogique;
	}

	public char getTypeLogique() {
		return typeLogique;
	}

	public DevicePhysique getDevicePhysique() {
		return devicePhysique;
	}
	
}