import java.util.ArrayList;
import java.util.List;

import javax.swing.Timer;

/*
 * Représente un capteur physique, c'est-à-dire un capteur réel
 * Il comporte un capteur logique par type de donnée qu'il fournit
 */
public class DevicePhysique {
	private long idPhysique; // Id constructeur
	private String typePhysique; // eep
	// liste des capteurs logiques qui compose le capteur physique
	private List<DeviceLogique> listeDevicesLogiques = new ArrayList<DeviceLogique>();
	private Timer timerDevice; // timer associé au périphérique

	/*
	 * Constructeur
	 */
	public DevicePhysique(long idPhysique, String typePhysique,
			List<DeviceLogique> listeDevicesLogiques) {
		this.idPhysique = idPhysique;
		this.typePhysique = typePhysique;
		this.listeDevicesLogiques = listeDevicesLogiques;

		// Création du timer
		TimerListener taskPerformer = new TimerListener(this);
		this.timerDevice = new Timer(Constantes.DELAI_DECLENCHEMENT_TIMER,
				taskPerformer);
	}

	public List<DeviceLogique> getListeDevicesLogiques() {
		return listeDevicesLogiques;
	}

	public void setListeDevicesLogiques(List<DeviceLogique> listeDevicesLogiques) {
		this.listeDevicesLogiques = listeDevicesLogiques;
	}

	public long getIdPhysique() {
		return idPhysique;
	}

	public String getTypePhysique() {
		return typePhysique;
	}

	public void demarrerTimer() {
		this.timerDevice.start();
	}

	public void redemarrerTimer() {
		this.timerDevice.restart();
	}

	public void arreterTimer() {
		this.timerDevice.stop();
	}
}
