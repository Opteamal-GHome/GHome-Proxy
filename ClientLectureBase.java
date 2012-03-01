import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

/*
 * Reçoit les trames envoyées par la base des capteurs
 */
public class ClientLectureBase implements Runnable {

	private Thread clientLectureThread = null;
	private Socket _socket;
	public boolean continuer = true;
	public char[] cbuf = new char[Constantes.TAILLE_TRAME_ENOCEAN];

	/*
	 * Constructeur
	 */
	public ClientLectureBase(Socket baseSocket) {
		// Création du Thread
		clientLectureThread = new Thread(this);

		this._socket = baseSocket;

		// Lancement du Thread
		clientLectureThread.start();
	}

	/*
	 * Boucle du thread
	 */
	public void run() {
		InputStream input = null;
		while (continuer) {

			// On récupère les données
			try {
				input = _socket.getInputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			BufferedReader response = new BufferedReader(new InputStreamReader(
					input));

			// On attend d'avoir 28 octets et on ajoute la trame aux trames à
			// parser
			try {
				int msg = response.read(cbuf, 0,
						Constantes.TAILLE_TRAME_ENOCEAN);
				if (msg > 0) {
					// Affichage de la trame reçue
					for (int i = 0; i < cbuf.length; i++) {
						System.out.print(cbuf[i]);
					}
					System.out.println();
					Parseur.addTrame(cbuf); // On ajoute la trame à la liste des trames à parser
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// On suspend le thread pour laisser les autres
			try {
				clientLectureThread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// Fermeture du socket
		try {
			input.close();
			_socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
