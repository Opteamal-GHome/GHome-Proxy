import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		// Création des Capteurs
		DevicePhysique interrupteur = new DevicePhysique(Constantes.ID_INTERRUPTEUR_4, Constantes.TYPE_INTERRUPTEUR_4, null);
		DeviceLogique interrupteurLogique = new DeviceLogique(EnsembleDevices.getNextIdLogique(), Constantes.TYPE_INTERRUPTEUR, interrupteur);
		ArrayList<DeviceLogique> listeDevLogInt = new ArrayList<DeviceLogique>();
		listeDevLogInt.add(interrupteurLogique);
		interrupteur.setListeDevicesLogiques(listeDevLogInt);
		
		// Lancement du thread de réception des trames de la base
		try {
			ClientLectureBase clientLect = new ClientLectureBase(InetAddress.getByName(Constantes.IP_BASE), Constantes.PORT_BASE);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Lancement du Thread d'envoi
//		try {
//			ClientEnvoiGHome clientEnvoi = new ClientEnvoiGHome(InetAddress.getByName(Constantes.IP_GHOME), Constantes.PORT_GHOME);
//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		// Lancement du Thread d'envoi
		ServeurEnvoiGHome serveurEnvoi = new ServeurEnvoiGHome(Constantes.PORT_SERV_ENVOI);
		
		// Lancement du thread de parsing des trames
		Parseur parseur = new Parseur();
		
		/***********tests***************/
		/*char[] trame = {'A','5','5','A','0','B','0','5','0','0','0','0','0','0','0','0','0','0','2','1','C','B','E','5','2','0','0','1'};
		String dataByte0 = trame[14]+""+trame[15];
		System.out.println(dataByte0);
		int dataByte0Binaire = Integer.parseInt(dataByte0, 16);
		System.out.println(Integer.(dataByte0Binaire));*/
		
		// Trame quelconque
		char[] trame = {'A','5','5','A','0','B','0','5','0','0','0','0','0','0','0','0','0','0','2','1','C','B','E','5','2','0','0','1'};
		Parseur.addTrame(trame);
		// Trame teach-in
		char[] trame2 = {'A','5','5','A','0','B','0','5','0','0','0','0','0','0','8','7','0','0','2','1','C','B','E','5','2','0','0','1'};
		Parseur.addTrame(trame2);
		// Trame de données
		char[] trame3 = {'A','5','5','A','0','B','0','5','0','0','0','0','0','0','0','A','0','0','2','1','C','B','E','5','2','0','0','1'};
		Parseur.addTrame(trame3);
	}

}
