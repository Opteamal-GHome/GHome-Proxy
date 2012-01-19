import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * 
 * Envoi au serveur GHome des trames prêtes à être envoyées
 * 
 */
public class ServeurEnvoiGHome implements Runnable {

	private Thread serveurEnvoiThread = null;
	private ServerSocket serverSocket;
	public boolean continuer = true;
	public static ArrayList<ProxyTrame> listeProxyTrames;

	public ServeurEnvoiGHome(int port) {

		System.out.println("ServeurEnvoiGHome : dans le constructeur");
		
		// Création du Thread
		serveurEnvoiThread = new Thread(this);

		// Création du socket
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("ServeurEnvoiGHome : Thread et Socket créés");

		// Création de la liste
		listeProxyTrames = new ArrayList<ProxyTrame>();

		// Lancement du Thread
		serveurEnvoiThread.start();
	}

	@Override
	public void run() {
		while (continuer) {
			System.out.println("ServeurEnvoiGHome : Dans le while");
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
				// listeProxyTrames.notify();
			}

			envoyerProxyTrame(proxyTrame);
		}

	}

	public static void addProxyTrame(ProxyTrame proxyTrame) {
		synchronized (listeProxyTrames) {
			// listeProxyTrames.wait();
			listeProxyTrames.add(proxyTrame);
			listeProxyTrames.notify();
		}
	}
	
	public void envoyerProxyTrame(ProxyTrame proxyTrame){
		try {
			Socket connectionSocket = serverSocket.accept();
			ObjectOutputStream outToClient = new ObjectOutputStream(connectionSocket.getOutputStream());
			outToClient.writeObject(proxyTrame);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
