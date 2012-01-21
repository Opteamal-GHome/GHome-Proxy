import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * 
 * Thread récupérant les tâches reçues, les parsant, puis les envoyant au serveur GHome
 *
 */
public class Parseur implements Runnable {

	private static ArrayList<char[]> listeTrames;
	private Thread parseurThread = null;
	public static boolean continuer = true;
	
	Parseur(){
		System.out.println("Parseur : dans le constructeur");
		Parseur.listeTrames = new ArrayList<char[]>();
		this.parseurThread = new Thread(this);
		this.parseurThread.start();
	}

	public static void addTrame(char[] trame) {
		synchronized (listeTrames) {
			//listeTrames.wait();
			listeTrames.add(trame);
			listeTrames.notify();
		}
	}

	public ArrayList<ProxyTrame> parseTrame(char[] trame) {
		
		System.out.println("Parseur : dans parseTrame");
		
		int dataByte0_1 = Integer.parseInt((""+trame[Constantes.DATA_BYTE_0_1]), 16);
		int dataByte0_2 = Integer.parseInt((""+trame[Constantes.DATA_BYTE_0_2]), 16);
		
		ArrayList<ProxyTrame> listeProxyTrames = new ArrayList<ProxyTrame>();
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());

		// teach-in frame
		if (((dataByte0_1 & 8) == 8) && ((dataByte0_2 & 8) == 0)) {
			/*proxyTrame.setType(Constantes.TYPE_STATUS);
			ContenuTrameS contenuS = parseTeachIn(trame);
			proxyTrame.setData(contenuS);*/
		} else {
			/*proxyTrame.setType(Constantes.TYPE_DONNEES);
			ContenuTrameD contenuD = parseTrameData(trame);
			proxyTrame.setData(contenuD);*/
			listeProxyTrames = parseTrameD(trame, timestamp);
		}
		return listeProxyTrames;
	}
	
	public static ArrayList<ProxyTrame> parseTrameD(char[] trame, Timestamp timestamp) {
		
		System.out.println("Parseur : dans parseTrameD");
		
		ArrayList<ProxyTrame> listeProxyTrames = new ArrayList<ProxyTrame>();
		
		// Parser ID physique
		int idPhysique = parseID(trame);
		// Chercher id dans la map
		DevicePhysique devPhysique = EnsembleDevices.getDevicePhysiqueByID(idPhysique);
		if(devPhysique == null){
			return null;
		}
		
		int typePhysique = devPhysique.getTypePhysique();
		
		switch (typePhysique)
		{
		//case Constantes.TYPE_INTERRUPTEUR_4 : 
		//case :
		case Constantes.TYPE_P_CONTACT : listeProxyTrames = parseTypeContact(trame, devPhysique, timestamp);
		}
		
		return listeProxyTrames;
	}
	
	public static ArrayList<ProxyTrame> parseTypeContact(char[] trame, DevicePhysique devPhysique, Timestamp timestamp)
	{
		System.out.println("Parseur : dans parseTypeContact");
		
		ArrayList<ProxyTrame> listeTrames = new ArrayList<ProxyTrame>();
		
		int dataByte3_2 = Integer.parseInt(""+trame[Constantes.DATA_BYTE_3_2],16);
		
		ContenuTrameD contenu = new ContenuTrameD();
		contenu.setValeurLue(dataByte3_2 & 1);
		DeviceLogique devLog = devPhysique.getListeDevicesLogiques().get(0);
		contenu.setDeviceID(devLog.getIdLogique());
		
		ProxyTrame proxyTrame = new ProxyTrame();
		proxyTrame.setData(contenu);
		proxyTrame.setType(Constantes.TYPE_DONNEES);
		proxyTrame.setTimestamp(timestamp);
		
		listeTrames.add(proxyTrame);

		return listeTrames;
	}	

	public static ArrayList<ProxyTrame> parseTypeInterruteur4(char[] trame)
	{
		ArrayList<ProxyTrame> listeTrames = new ArrayList<ProxyTrame>();
		
		int statusByte1 = Integer.parseInt(""+trame[Constantes.STATUS_1],16);
		if((statusByte1 & 1) == 1) // N-Message
		{
			//int dataByte3_1 = 
		}
		
		// TODO

		return listeTrames;
	}
	
	
	
/*	public static ContenuTrameS parseTeachIn(char[] trame) {
		System.out.println("Parseur : TeachIn");
		// TODO Créer le contenu et ajouter le nouveau capteur
		return null;
	}
	
	public static ContenuTrameD parseTrameData(char[] trame) {
		System.out.println("Parseur : Données");
		// TODO Rechercher le capteur correspondant et créer le contenu
		// Parser ID physique
		int idPhysique = parseID(trame);
		// Chercher id dans la map
		DevicePhysique devPhysique = EnsembleDevices.getDevicePhysiqueByID(idPhysique);
		if(devPhysique == null){
			return null;
		}
		
		
		return null;
	}*/
	
	public static int parseID(char[] trame){
		int idPhysique = Integer.parseInt(("" + trame[Constantes.ID_BYTE_3_1] + trame[Constantes.ID_BYTE_3_2] + 
				trame[Constantes.ID_BYTE_2_1] + trame[Constantes.ID_BYTE_2_2] + trame[Constantes.ID_BYTE_1_1] + 
				trame[Constantes.ID_BYTE_1_2] + trame[Constantes.ID_BYTE_0_1] + trame[Constantes.ID_BYTE_0_2]), 16);
		return idPhysique;
	}

	@Override
	public void run() {
		while(continuer){
			try {
				parseurThread.sleep(500);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.out.print("Parseur : dans le while");
			char[] trame;
			synchronized (listeTrames) {
				if(listeTrames.isEmpty()){
					try {
						listeTrames.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				trame = listeTrames.remove(0);
				//listeTrames.notify();
			}
			ArrayList<ProxyTrame> listeProxyTrames = parseTrame(trame);
			if((listeProxyTrames != null) && (listeProxyTrames.size() != 0)){
				for(int i=0; i<listeProxyTrames.size(); i++) {
					// Mettre la trame dans la liste des trames à envoyer
					ClientEnvoiGHome.addProxyTrame(listeProxyTrames.get(i));
				}
			}
		}
		
	}

}
