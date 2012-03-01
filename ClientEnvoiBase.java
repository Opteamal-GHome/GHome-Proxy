import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/*
 * Classe envoyant les commandes aux actionneurs
 */
public class ClientEnvoiBase implements Runnable

{
	private boolean OK = true;
	private static Thread clientThread = null;
	private Socket socket;
	private static List<String> toSend = new ArrayList<String>(); // liste des trames à envoyer à la base
	
	/*
	 * Constructeur
	 */
	public ClientEnvoiBase(Socket baseSocket) {
		this.socket = baseSocket;
		clientThread = new Thread(this);
		clientThread.start();
	}

	/*
	 * Appeler depuis la classe Commande pour ajouter une trame à la liste des trames à envoyer à la base
	 */
	public static void addToList(String command) {
		synchronized (toSend) {
			toSend.add(command);
			System.out
					.println("//////////////////add command to list /////////////// "
							+ command);
			toSend.notify();
		}
	}

	/*
	 * Boucle du thread
	 */
	public void run() {
		while (OK) {
			try {
				String messageToSend = null;
				synchronized (toSend) {
					if (toSend.isEmpty()) {
						try {
							toSend.wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if (!toSend.isEmpty()) { // Lorsqu'il y a une trame dans la liste des trames à envoyer, on la récupère
						messageToSend = toSend.remove(0);
					}

					toSend.notify();
				}

				DataOutputStream dataOut = new DataOutputStream(socket
						.getOutputStream());
				dataOut.write(messageToSend.getBytes()); // La trame est envoyée à la base

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
