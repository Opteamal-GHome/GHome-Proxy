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

	public ProxyTrame parseTrame(char[] trame) {
		int dataByte0_1 = Integer.parseInt((""+trame[Constantes.DATA_BYTE_0_1]), 16);
		int dataByte0_2 = Integer.parseInt((""+trame[Constantes.DATA_BYTE_0_2]), 16);
		
		ProxyTrame proxyTrame = new ProxyTrame();
		// TODO timestamp
		// proxyTrame.setTimestamp(timestamp);

		// teach-in frame
		if (((dataByte0_1 & 8) == 8) && ((dataByte0_2 & 8) == 0)) {
			proxyTrame.setType(Constantes.TYPE_STATUS);
			ContenuTrameS contenuS = parseTeachIn(trame);
			proxyTrame.setData(contenuS);
		} else if ((dataByte0_2 & 8) == 8) {
			proxyTrame.setType(Constantes.TYPE_DONNEES);
			ContenuTrameD contenuD = parseTrameData(trame);
			proxyTrame.setData(contenuD);
		}
		else {
			System.out.println("Parseur : Trame non reconnue");
			return null;
		}
		return proxyTrame;
	}

	public static ContenuTrameS parseTeachIn(char[] trame) {
		System.out.println("Parseur : TeachIn");
		// TODO Créer le contenu et ajouter le nouveau capteur
		return null;
	}
	
	public static ContenuTrameD parseTrameData(char[] trame) {
		System.out.println("Parseur : Données");
		// TODO Rechercher le capteur correspondant et créer le contenu
		return null;
	}

	@Override
	public void run() {
		while(continuer){
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
			ProxyTrame proxyTrame = parseTrame(trame);
			if(proxyTrame != null){
				// Mettre la trame dans la liste des trames à envoyer
				ServeurEnvoiGHome.addProxyTrame(proxyTrame);
			}
		}
		
	}

}
