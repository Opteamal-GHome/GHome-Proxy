import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Classe permettant la gestion des différents capteurs/actionneurs enregistrés
 */
public class EnsembleDevices {
	public static HashMap<Long, DevicePhysique> mapDevicesPhysiques = new HashMap<Long, DevicePhysique>();
	public static HashMap<Integer, DeviceLogique> mapDevicesLogiques = new HashMap<Integer, DeviceLogique>();
	public static int nextIDLogique = 0;

	/*
	 * Permet d'ajouter un capteur à l'ensemble des capteurs
	 */
	public static void ajouterDevice(long nouveauIDPysique,
			DevicePhysique nouveauDevicePhysique) {
		
		// On ajoute le capteur physique à la map avec son id constructeur comme clé
		mapDevicesPhysiques.put(nouveauIDPysique, nouveauDevicePhysique);

		// Ajout des capteurs logiques correspondants
		List<DeviceLogique> v = nouveauDevicePhysique.getListeDevicesLogiques();
		for (int i = 0; i < v.size(); i++) {
			DeviceLogique dl = v.get(i);
			mapDevicesLogiques.put(dl.getIdLogique(), dl);
		}

		ajouterDeviceDansFichierXml(nouveauDevicePhysique); // On ajoute le capteur physique dans le fichier xml

		// Démarrer le timer du device
		nouveauDevicePhysique.demarrerTimer();
	}

	/*
	 * Permet de supprimer un capteur de l'ensemble des capteurs
	 */
	public static void supprimerDevice(int idLogique) {
		
		// On récupère le capteur logique grâce à son id
		DeviceLogique dl = mapDevicesLogiques.get(idLogique);
		// On récupère le capteur physique auquel appartient le capteur logique
		DevicePhysique dp = dl.getDevicePhysique();
		
		// On supprime tous les capteurs logiques associés à ce capteur physique de la map
		List<DeviceLogique> vDL = dp.getListeDevicesLogiques();
		for (int i = 0; i < vDL.size(); i++) {
			mapDevicesLogiques.remove(vDL.get(i).getIdLogique());
		}
		
		// On supprime le timer qui était associé au capteur et on supprime le capteur
		dp.arreterTimer();
		mapDevicesPhysiques.remove(dp.getIdPhysique());
		supprimerDeviceDuFichierXml(dp.getIdPhysique());
	}

	public static DevicePhysique getDevicePhysiqueByID(long idPhysique) {
		return mapDevicesPhysiques.get(idPhysique);
	}

	public static DeviceLogique getDeviceLogiquebyID(int idLogique) {
		return mapDevicesLogiques.get(idLogique);
	}

	/*
	 * Permet d'obtenir une id unique pour chaque capteur logique
	 */
	public static int getNextIdLogique() {
		nextIDLogique++;
		return nextIDLogique;
	}

	/*
	 * Méthode permettant de recréer et ajouter à l'ensemble des capteurs les capteurs récupérés à partir des informations contenues dans le fichier xml
	 */
	public static void parseDeviceFile(String path) {
		String tag = "Dev";
		Element el = null;
		Document document = Utilitaires.loadConfiguration(path);
		Element root = document.getDocumentElement();
		if (root.getNodeName().equals("Devices")) {
			NodeList listOfDev = root.getElementsByTagName(tag);
			if (listOfDev != null && listOfDev.getLength() > 0) {
				int i = 0;
				int length = listOfDev.getLength();
				for (i = 0; i < length; i++) {
					el = (Element) listOfDev.item(i);
					addDeviceStatically(
							(Long.parseLong(el.getAttribute("id"))), el
									.getAttribute("eep"));
				}
			}
		}
	}

	/*
	 * Crée et ajoute le capteur en fonction de son type
	 */
	public static void addDeviceStatically(long id, String type) {
		if (type.equals("06-00-01")) // contact
		{
			DevicePhysique contact = new DevicePhysique(id,
					Constantes.TYPE_P_CONTACT, null);
			DeviceLogique contactLogique = new DeviceLogique(EnsembleDevices
					.getNextIdLogique(), Constantes.TYPE_L_CONTACT, contact);
			List<DeviceLogique> listeDevLogContact = new ArrayList<DeviceLogique>();
			listeDevLogContact.add(contactLogique);
			contact.setListeDevicesLogiques(listeDevLogContact);
			EnsembleDevices.ajouterDevice(id, contact);
		} else if (type.equals("07-02-05")) // température
		{
			DevicePhysique temp = new DevicePhysique(id,
					Constantes.TYPE_P_TEMPERATURE, null);
			DeviceLogique tempLogique = new DeviceLogique(EnsembleDevices
					.getNextIdLogique(), Constantes.TYPE_L_TEMPERATURE, temp);
			List<DeviceLogique> listeDevLogContact = new ArrayList<DeviceLogique>();
			listeDevLogContact.add(tempLogique);
			temp.setListeDevicesLogiques(listeDevLogContact);
			EnsembleDevices.ajouterDevice(id, temp);
		} else if (type.equals("07-08-01")) // luminosité, présence, (température)
		{
			DevicePhysique presence = new DevicePhysique(id,
					Constantes.TYPE_P_PRESENCE, null);
			List<DeviceLogique> listeDevLogPre = new ArrayList<DeviceLogique>();

			DeviceLogique lumLogique = new DeviceLogique(EnsembleDevices
					.getNextIdLogique(), Constantes.TYPE_L_LUMINOSITE, presence);
			DeviceLogique tempLogique = new DeviceLogique(EnsembleDevices
					.getNextIdLogique(), Constantes.TYPE_L_TEMPERATURE,
					presence);
			DeviceLogique presenceLogique = new DeviceLogique(EnsembleDevices
					.getNextIdLogique(), Constantes.TYPE_L_PRESENCE, presence);

			listeDevLogPre.add(lumLogique);
			listeDevLogPre.add(tempLogique);
			listeDevLogPre.add(presenceLogique);

			presence.setListeDevicesLogiques(listeDevLogPre);
			EnsembleDevices.ajouterDevice(id, presence);

		} else if (type.equals("05-02-01")) // interrupteur
		{
			DevicePhysique interrupteur = new DevicePhysique(id,
					Constantes.TYPE_P_INTERRUPTEUR_4, null);
			List<DeviceLogique> listeDevLogInt = new ArrayList<DeviceLogique>();

			DeviceLogique interrupteurLogique = new DeviceLogique(
					EnsembleDevices.getNextIdLogique(),
					Constantes.TYPE_L_INTERRUPTEUR, interrupteur);
			listeDevLogInt.add(interrupteurLogique);
			interrupteur.setListeDevicesLogiques(listeDevLogInt);
			EnsembleDevices.ajouterDevice(id, interrupteur);
		}

		else if (type.equals("prise")) { // prise électrique (actionneur)
			DevicePhysique prise = new DevicePhysique(id,
					Constantes.TYPE_P_PRISE, null);
			ArrayList<DeviceLogique> listeDevLogPrise = new ArrayList<DeviceLogique>();
			DeviceLogique priseLogique = new DeviceLogique(EnsembleDevices
					.getNextIdLogique(), Constantes.TYPE_L_ACTIONNEUR, prise);
			listeDevLogPrise.add(priseLogique);
			prise.setListeDevicesLogiques(listeDevLogPrise);
			EnsembleDevices.ajouterDevice(id, prise);
		}

		else if (type.equals("meteo")) { // meteo (source externe)
			DevicePhysique meteo = new DevicePhysique(id,
					Constantes.TYPE_P_METEO, null);
			ArrayList<DeviceLogique> listeDevLogMeteo = new ArrayList<DeviceLogique>();
			DeviceLogique temperatureLogique = new DeviceLogique(
					getNextIdLogique(), Constantes.TYPE_L_METEO_TEMP, meteo);
			listeDevLogMeteo.add(temperatureLogique);
			DeviceLogique humiditeLogique = new DeviceLogique(
					getNextIdLogique(), Constantes.TYPE_L_METEO_HUM, meteo);
			listeDevLogMeteo.add(humiditeLogique);
			meteo.setListeDevicesLogiques(listeDevLogMeteo);
			ajouterDevice(id, meteo);
		}
	}

	/*
	 * Permet d'ajouter un capteur dans le fichier xml de sauvegarde
	 */
	public static void ajouterDeviceDansFichierXml(
			DevicePhysique capteurPhysique) {
		Document doc = Utilitaires
				.loadConfiguration(Constantes.pathToDeviceFile);
		Element root = doc.getDocumentElement();

		// On vérifie qu'il n'y est pas déjà
		if (doc.getElementById(Long.toString(capteurPhysique.getIdPhysique())) != null) {
			return;
		}

		// Ajout du device au document XML
		Element device = doc.createElement("Dev");
		device.setAttribute("eep", capteurPhysique.getTypePhysique());
		// device.setIdAttribute("id", true);
		device.setAttribute("id", Long
				.toString(capteurPhysique.getIdPhysique()));
		root.appendChild(device);

		Utilitaires.transformerXml(doc, Constantes.pathToDeviceFile);
	}

	/*
	 * Permet de supprimer un capteur du fichier xml de sauvegarde lorsqu'un capteur est supprimé
	 */
	public static void supprimerDeviceDuFichierXml(long id) {
		Document doc = Utilitaires
				.loadConfiguration(Constantes.pathToDeviceFile);
		Element root = doc.getDocumentElement();
		Element e = doc.getElementById(Long.toString(id));

		root.removeChild(e);
		Utilitaires.transformerXml(doc, Constantes.pathToDeviceFile);
	}

}
