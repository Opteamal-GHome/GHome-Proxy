import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/*
 * Cette classe envoie au serveur central GHome les informations sur les différents capteurs
 */
public class ClientEnvoiGHome implements Runnable {

	private Thread clientEnvoiThread = null;
	private Socket socket;
	public boolean continuer = true;
	public static List<ProxyTrame> listeProxyTrames; // Liste des trames à envoyer au serveur

	/*
	 * Constructeur
	 */
	public ClientEnvoiGHome(Socket socket) {
		this.socket = socket;
		// Lancement Thread
		this.clientEnvoiThread = new Thread(this);
		// Création de la liste
		listeProxyTrames = new ArrayList<ProxyTrame>();
		// Lancement du Thread
		clientEnvoiThread.start();
		this.init();
	}

	/*
	 * Méthode d'initialisation
	 * Au lancement du proxy, tous les capteurs contenus dans le fichier xml sont ajoutés à l'ensemble des capteurs
	 * et envoyés au serveur GHome
	 */
	private void init() {
		EnsembleDevices.parseDeviceFile(Constantes.pathToDeviceFile); // Ajout des capteurs à l'ensemble des capteurs
		DevicePhysique devP;
		List<DeviceLogique> devLogList;
		long timestamp = System.currentTimeMillis();

		// Pour tous les capteurs physique de l'ensemble des capteurs, on va envoyer les trames correspondant à chacun des capteurs logiques
		for (long mapKey : EnsembleDevices.mapDevicesPhysiques.keySet()) {
			devP = EnsembleDevices.mapDevicesPhysiques.get(mapKey);
			devLogList = devP.getListeDevicesLogiques();
			int j;
			for (j = 0; j < devLogList.size(); j++) {
				ProxyTrameS addDevFrame = new ProxyTrameS(timestamp,
						Constantes.TYPE_STATUS, Constantes.TYPE_AJOUT,
						devLogList.get(j).getIdLogique(), devLogList.get(j)
								.getTypeLogique());
				System.out
						.println("ClientEnvoiGHome - Init - Capteur créé : type dev phy = "
								+ devP.getTypePhysique()
								+ "  type dev Log = "
								+ devLogList.get(j).getTypeLogique()
								+ "  id dev Log = "
								+ devLogList.get(j).getIdLogique());
				synchronized (listeProxyTrames) {
					// listeProxyTrames.wait();
					listeProxyTrames.add(addDevFrame);
					listeProxyTrames.notify();
				}

			}
		}
	}

	/*
	 * Boucle du thread
	 */
	public void run() {
		while (continuer) {
			try {
				clientEnvoiThread.sleep(500);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			ProxyTrame proxyTrame;
			synchronized (listeProxyTrames) {
				if (listeProxyTrames.isEmpty()) {
					try {
						listeProxyTrames.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				proxyTrame = listeProxyTrames.remove(0);
				listeProxyTrames.notify();
			}

			envoyerProxyTrame(proxyTrame);
		}
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * Appelée depuis la classe Parseur pour ajouter une trame à la liste des trames à envoyer au serveur GHome
	 */
	public static void addProxyTrame(ProxyTrame proxyTrame) {
		synchronized (listeProxyTrames) {
			// listeProxyTrames.wait();
			listeProxyTrames.add(proxyTrame);
			listeProxyTrames.notify();
		}
	}

	/*
	 * Envoie la ProxyTrame au serveur GHome
	 */
	public void envoyerProxyTrame(ProxyTrame proxyTrame) {

		System.out.println("ClientEnvoiGHome : dans envoyerProxyTrame");
		try {

			// On recupere le timestamp
			Long timestamp = proxyTrame.getTimestamp();
			timestamp = timestamp / 1000;
			timestamp = (long) Math.round(timestamp);

			byte[] timestampB = Utilitaires.toByteArray(timestamp);

			// On recupere le type de la trame
			byte[] typeTrame = (proxyTrame.getType() + "").getBytes();

			// On recupere les bytes correspondant aux attributs de proxyTrame
			byte[] resteTrame = proxyTrame.encodeTrame();

			// On concatene les trois
			byte[] both1 = Utilitaires.concat(timestampB, typeTrame);
			byte[] both = Utilitaires.concat(both1, resteTrame);

			// On l'envoie
			if (socket.isConnected()) {

				DataOutputStream dataReturn = new DataOutputStream(socket
						.getOutputStream());
				System.out.print("Trame envoi : ");
				for (int i = 0; i < both.length; i++) {
					System.out.print(both[i]);
					System.out.print(" ");
				}
				System.out.println("");
				dataReturn.write(both);

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}