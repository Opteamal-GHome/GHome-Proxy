/*
 * Représente un capteur logique, c'est-à-dire
 * le capteur tel qu'il est géré par le serveur GHome avec une seule fonctionnalité 
 */
public class DeviceLogique {
	private int idLogique;
	private char typeLogique;
	private DevicePhysique devicePhysique; // DevicePhysique auquel appartient le DeviceLogique

	/*
	 * Constructeur
	 */
	public DeviceLogique(int idLogique, char typeLogique,
			DevicePhysique devicePhysique) {
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