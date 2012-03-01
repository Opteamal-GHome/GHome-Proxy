import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import meteo.Meteo;

/*
 * Permet de faire les traitements associés au déclenchement d'un timer lié à un capteur physique
 * Gère la suppression d'un capteur lorsque celui-ci n'émet plus pendant une certaine durée
 * Gère l'envoi régulier des informations de météo (source externe)
 */
public class TimerListener implements ActionListener {

	private DevicePhysique devPhy; // Capteur physique auquel correspond le timer
	private int delay = 0;

	/*
	 * Constructeur
	 */
	public TimerListener(DevicePhysique devPhy) {
		super();
		this.devPhy = devPhy;
	}

	/*
	 * Méthode appelée lors du déclenchement du timer
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (!this.devPhy.getTypePhysique().equals(Constantes.TYPE_P_METEO)) { // Si le capteur associé n'est pas la météo
			if(delay == 30) {
				supprimerCapteur();
				delay = 0;
			}
			else {
				delay++;
			}
			
		} else {
			envoyerMeteo();
		}
	}

	/*
	 * Construit les trames correspondant à l'envoi des données météo (température et humidité de la ville)
	 */
	private void envoyerMeteo() {
		Meteo meteo = new Meteo(Constantes.VILLE);

		// Création des trames de Retrait pour tous les devices logiques
		// associés
		long timestamp = System.currentTimeMillis();
		char typeTrame = Constantes.TYPE_DONNEES;

		// Température
		DeviceLogique tempLogique = this.devPhy.getListeDevicesLogiques()
				.get(0); // Capteur logique température
		ProxyTrameD proxyTrameDT = new ProxyTrameD(timestamp, typeTrame,
				tempLogique.getIdLogique());
		proxyTrameDT.setValeurLue(meteo.getTemperature());
		// Ajout de la trame à envoyer
		ClientEnvoiGHome.addProxyTrame(proxyTrameDT); // On ajoute la trame à la liste des trames à envoyer au serveur GHome

		// Humidité
		DeviceLogique humLogique = this.devPhy.getListeDevicesLogiques().get(1); // Capteur logique humidité
		ProxyTrameD proxyTrameDH = new ProxyTrameD(timestamp, typeTrame,
				humLogique.getIdLogique());
		proxyTrameDH.setValeurLue(meteo.getHumidite());
		// Ajout de la trame à envoyer
		ClientEnvoiGHome.addProxyTrame(proxyTrameDH); // On ajoute la trame à la liste des trames à envoyer au serveur GHome
	}

	/*
	 * Supprime un capteur qui n'émet plus et crée les trames correspondantes
	 */
	public void supprimerCapteur() {
		// Création des trames de Retrait pour tous les devices logiques associés
		long timestamp = System.currentTimeMillis();
		char typeTrame = Constantes.TYPE_STATUS;
		char typeStatus = Constantes.TYPE_RETRAIT;
		// On envoie des trames pour chaque capteur logique qui va être supprimé
		for (int i = 0; i < this.devPhy.getListeDevicesLogiques().size(); i++) {
			DeviceLogique capteurLogique = this.devPhy
					.getListeDevicesLogiques().get(i);
			int idCapteurLogique = capteurLogique.getIdLogique();
			char typeCapteurLogique = capteurLogique.getTypeLogique();
			ProxyTrameS proxyTrameS = new ProxyTrameS(timestamp, typeTrame,
					typeStatus, idCapteurLogique, typeCapteurLogique);
			// Ajout des trames à envoyer
			ClientEnvoiGHome.addProxyTrame(proxyTrameS);
		}
		// On supprime l'un des capteurs logique (entraine la suppression des autres capteurs logique et du capteur physique)
		EnsembleDevices.supprimerDevice(this.devPhy.getListeDevicesLogiques()
				.get(0).getIdLogique());
	}

}
