import java.util.ArrayList;
import java.util.Vector;

public class DevicePhysique {
	private int idPhysique;
	private String typePhysique;
	private ArrayList<DeviceLogique> listeDevicesLogiques = new ArrayList<DeviceLogique>();
	
	public DevicePhysique(int idPhysique, String typePhysique,ArrayList<DeviceLogique> listeDevicesLogiques) {
		this.idPhysique = idPhysique;
		this.typePhysique = typePhysique;
		this.listeDevicesLogiques = listeDevicesLogiques;
	}

	public ArrayList<DeviceLogique> getListeDevicesLogiques() {
		return listeDevicesLogiques;
	}

	public void setListeDevicesLogiques(ArrayList<DeviceLogique> listeDevicesLogiques) {
		this.listeDevicesLogiques = listeDevicesLogiques;
	}

	public int getIdPhysique() {
		return idPhysique;
	}

	public String getTypePhysique() {
		return typePhysique;
	}

}
