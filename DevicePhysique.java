import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.Timer;

public class DevicePhysique {
	private long idPhysique;
	private String typePhysique;
	private ArrayList<DeviceLogique> listeDevicesLogiques = new ArrayList<DeviceLogique>();
	private Timer timerDevice;
	
	public DevicePhysique(long idPhysique, String typePhysique,ArrayList<DeviceLogique> listeDevicesLogiques) {
		this.idPhysique = idPhysique;
		this.typePhysique = typePhysique;
		this.listeDevicesLogiques = listeDevicesLogiques;
		
		// Création du timer
		TimerListener taskPerformer = new TimerListener(this);
		this.timerDevice = new Timer(Constantes.DELAI_DECLENCHEMENT_TIMER, taskPerformer);
	}

	public ArrayList<DeviceLogique> getListeDevicesLogiques() {
		return listeDevicesLogiques;
	}

	public void setListeDevicesLogiques(ArrayList<DeviceLogique> listeDevicesLogiques) {
		this.listeDevicesLogiques = listeDevicesLogiques;
	}

	public long getIdPhysique() {
		return idPhysique;
	}

	public String getTypePhysique() {
		return typePhysique;
	}
	
	public void demarrerTimer(){
		System.out.println("On démarre le timer");
		this.timerDevice.start();
	}
	
	public void redemarrerTimer(){
		System.out.println("On redémarre le timer");
		this.timerDevice.restart();
	}
}
