import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		// Création d'un capteur de contact
		DevicePhysique contact = new DevicePhysique(Constantes.ID_CONTACT, Constantes.TYPE_P_CONTACT, null);
		DeviceLogique contactLogique = new DeviceLogique(EnsembleDevices.getNextIdLogique(), Constantes.TYPE_L_CONTACT, contact);
		ArrayList<DeviceLogique> listeDevLogContact = new ArrayList<DeviceLogique>();
		listeDevLogContact.add(contactLogique);
		contact.setListeDevicesLogiques(listeDevLogContact);
		EnsembleDevices.ajouterDevice(Constantes.ID_CONTACT, contact);
		
		//Création d'un interrupteur rocker switch 2
		DevicePhysique interrupteur = new DevicePhysique(Constantes.ID_INTERRUPTEUR_4, Constantes.TYPE_P_INTERRUPTEUR_4, null);
		ArrayList<DeviceLogique> listeDevLogInt = new ArrayList<DeviceLogique>();
		DeviceLogique interrupteurLogique = new DeviceLogique(EnsembleDevices.getNextIdLogique(), Constantes.TYPE_L_INTERRUPTEUR, interrupteur);
		listeDevLogInt.add(interrupteurLogique);
		interrupteur.setListeDevicesLogiques(listeDevLogInt);
		EnsembleDevices.ajouterDevice(Constantes.ID_INTERRUPTEUR_4, interrupteur);
		
		//Création d'un actionneur (prise)
//		DevicePhysique prise = new DevicePhysique(Constantes.ID_PRISE, "06-00-01", null);
//		ArrayList<DeviceLogique> listeDevLogPrise = new ArrayList<DeviceLogique>();
//		DeviceLogique priseLogique = new DeviceLogique(EnsembleDevices.getNextIdLogique(), Constantes.TYPE_L_ACTIONNEUR, prise);
//		listeDevLogPrise.add(priseLogique);
//		prise.setListeDevicesLogiques(listeDevLogPrise);
//		EnsembleDevices.ajouterDevice(Constantes.ID_PRISE, prise);

		// TODO Vérifier ordre de création + faire le lancement des threads (start) dans le main plutôt que dans les constructeurs
		
		// Lancement du serveur (test)
		ServeurEnvoiGHome serv = new ServeurEnvoiGHome(Constantes.PORT_SERV_ENVOI);
		
		// Lancement du thread de parsing des trames
		Parseur parseur = new Parseur();
		
//		contact.demarrerTimer();
//		interrupteur.demarrerTimer();
		
		// Lancement du Thread d'envoi
		/*try {
			ClientEnvoiGHome clientEnvoi = new ClientEnvoiGHome(InetAddress.getByName(Constantes.IP_GHOME), Constantes.PORT_GHOME);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Lancement du thread de réception des trames de la base
//		try {
//			ClientLectureBase clientLect = new ClientLectureBase(InetAddress.getByName(Constantes.IP_BASE), Constantes.PORT_BASE);
//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		/***********tests***************/
		/*char[] trame = {'A','5','5','A','0','B','0','5','0','0','0','0','0','0','0','0','0','0','2','1','C','B','E','5','2','0','0','1'};
		String dataByte0 = trame[14]+""+trame[15];
		System.out.println(dataByte0);
		int dataByte0Binaire = Integer.parseInt(dataByte0, 16);
		System.out.println(Integer.(dataByte0Binaire));*/
		
//		// Trame quelconque
//		char[] trame = {'A','5','5','A','0','B','0','5','0','0','0','0','0','0','0','0','0','0','2','1','C','B','E','5','2','0','0','1'};
//		Parseur.addTrame(trame);
//		// Trame teach-in
//		char[] trame2 = {'A','5','5','A','0','B','0','5','0','0','0','0','0','0','8','7','0','0','2','1','C','B','E','5','2','0','0','1'};
//		Parseur.addTrame(trame2);
//		// Trame de données
//		char[] trame3 = {'A','5','5','A','0','B','0','5','0','0','0','0','0','0','0','A','0','0','2','1','C','B','E','5','2','0','0','1'};
//		Parseur.addTrame(trame3);
		
//		char[] trame = {'A','5','5','A','0','B','0','7','1','0','0','8','0','2','8','7','0','0','0','4','E','9','5','7','0','0','8','8'};
//		
//		int[] funcType = Parseur.parseFuncAndType(trame);
//		System.out.println("func "+funcType[0]);
//		System.out.println("type "+funcType[1]);
//		System.out.println(Integer.parseInt("060001", 16));
		
		// Trame du contact
		for(int i=0;i<999999999;i++);
		contact.redemarrerTimer();
		
		//send command telegram to device
		ClientEnvoieBase.addToList("A55A6B0560000000FF9F1E073000");
		try {
			ClientEnvoieBase c = new ClientEnvoieBase(InetAddress.getByName(Constantes.IP_BASE), Constantes.PORT_BASE);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
