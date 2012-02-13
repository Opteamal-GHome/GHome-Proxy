import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class ClientEnvoiGHome  implements Runnable {

	private Thread clientEnvoiThread = null;
	private Socket socket;
	public boolean continuer = true;
	public static List<ProxyTrame> listeProxyTrames;

	public ClientEnvoiGHome(InetAddress adresseIP, int port) {

		System.out.println("ClientEnvoiGHome : dans le constructeur");
		
		// Création du Thread
		clientEnvoiThread = new Thread(this);

		// Création du socket
		try {
			socket = new Socket(adresseIP, port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("ClientEnvoiGHome : Thread et Socket créés");

		// Création de la liste
		listeProxyTrames = new ArrayList<ProxyTrame>();

		// Lancement du Thread
		clientEnvoiThread.start();
	}
	
	public ClientEnvoiGHome(Socket socket) {
		this.socket = socket;
		
		// Lancement Thread
		//this.clientEnvoiThread = new Thread(this);
		
		// Création de la liste
		listeProxyTrames = new ArrayList<ProxyTrame>();

		// Lancement du Thread
		//clientEnvoiThread.start();
		
		System.out.println("Constructeur Envoi GHome OK");
	}

	@Override
	public void run() {
		while (continuer) {
			try {
				clientEnvoiThread.sleep(500);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.out.println("ClientEnvoiGHome : dans le while");
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

	public static void addProxyTrame(ProxyTrame proxyTrame) {
		System.out.println("ClientEnvoiGHome : dans addProxyTrame");
		synchronized (listeProxyTrames) {
			// listeProxyTrames.wait();
			listeProxyTrames.add(proxyTrame);
			listeProxyTrames.notify();
		}
	}

	public void envoyerProxyTrame(ProxyTrame proxyTrame){
		
		System.out.println("ClientEnvoiGHome : dans envoyerProxyTrame");
		try {
			String chaine = "";
			
			// On recupere le timestamp
			Long timestamp = proxyTrame.getTimestamp();
			timestamp = timestamp / 1000;
			timestamp = (long) Math.round(timestamp);
			
			byte[] timestampB = Utilitaires.toByteArray(timestamp);

			// On recupere le type de la trame
			byte[] typeTrame =  (proxyTrame.getType()+"").getBytes();

			// On recupere les bytes correspondant aux attributs de proxyTrame
			byte[] resteTrame = proxyTrame.encodeTrame();
			
			// On concatene les trois
			byte[] both1 = Utilitaires.concat(timestampB, typeTrame);
			byte[] both= Utilitaires.concat(both1, resteTrame);
						
			// On l'envoie
			if(socket.isConnected()) {
				DataOutputStream dataReturn = new DataOutputStream(socket.getOutputStream());
				dataReturn.write(both);
				System.out.println(chaine);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	


}