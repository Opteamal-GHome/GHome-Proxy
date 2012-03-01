import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 
 * Simule le serveur GHome
 * 
 */
public class ServeurEnvoiGHome implements Runnable {

	private Thread serveurEnvoiThread = null;
	private ServerSocket serverSocket;
	public boolean continuer = true;

	public ServeurEnvoiGHome(int port) {

		// Création du Thread
		serveurEnvoiThread = new Thread(this);

		// Création du socket
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Lancement du Thread
		serveurEnvoiThread.start();
	}

	@Override
	public void run() {
		Socket s = null;
		try {
			s = serverSocket.accept();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		while (continuer) {
			try {
				serveurEnvoiThread.sleep(500);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}

	}
}
