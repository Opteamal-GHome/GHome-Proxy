import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class ClientEnvoiGHome  implements Runnable {

	private Thread clientEnvoiThread = null;
	private Socket _socket;
	public boolean continuer = true;
	public static List<ProxyTrame> listeProxyTrames;

	public ClientEnvoiGHome(InetAddress adresseIP, int port) {

		System.out.println("ClientEnvoiGHome : dans le constructeur");
		
		// Création du Thread
		clientEnvoiThread = new Thread(this);

		// Création du socket
		try {
			_socket = new Socket(adresseIP, port);
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
			_socket.close();
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
			ObjectOutputStream outToServer = new ObjectOutputStream(_socket.getOutputStream());
			outToServer.writeObject(proxyTrame);
			//outToServer.writeObject("Coucou");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}