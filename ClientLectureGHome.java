import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Arrays;

import meteo.Meteo;

/*
 * Reçoit les trames envoyées par le serveur GHome
 */
public class ClientLectureGHome implements Runnable {

	private Thread clientLectureGhThread = null;
	private Socket socket;
	private BufferedReader in;
	public boolean continuer = true;

	public static Charset charset = Charset.forName("UTF-8");
	public static CharsetEncoder encoder = charset.newEncoder();

	/*
	 * Constructeur
	 */
	public ClientLectureGHome(Socket socket) {
		this.socket = socket;

		// Lancement Thread
		this.clientLectureGhThread = new Thread(this);
		this.clientLectureGhThread.start();
	}

	/*
	 * Boucle du thread
	 */
	@Override
	public void run() {
		InputStream input = null;

		while (continuer) {

			try {

				// Lit la valeur du socket
				input = socket.getInputStream();

				InputStreamReader isr = new InputStreamReader(input, "ISO-8859-1");

				in = new BufferedReader(isr);
				String message = "";

				char[] debut = new char[20];
				in.read(debut, 0, 9); // On récupère les 9 premiers octets (timestamp + type de trame)
				message = new String(debut);

				System.out
						.println("Client Lecture GHome : Début du Message recu : "
								+ Utilitaires.stringToHexa(message));

				byte[] timestampB = message.substring(0, 8).getBytes();
				byte[] typeB = message.substring(8, 9).getBytes("UTF-8");
				// System.out.println("type hexa : " +
				// Utilitaires.stringToHexa(new String(typeB)));

				byte[] ordreB = { (byte) Constantes.TYPE_ORDRE };
				byte[] villeB = { (byte) Constantes.TYPE_UPDATE_VILLE };
				
				if (Arrays.equals(typeB, ordreB)) { // La trame est une trame d'ordre (commande à un actionneur)

					char[] finOrdre = new char[8];
					in.read(finOrdre, 0, 8); // On récupère le reste des données (id actionneur + commande)
					String finOrdreString = new String(finOrdre);

					byte[] dataB = finOrdreString.getBytes();
					// System.out.println("dataB : " +
					// Utilitaires.stringToHexa(new String(dataB)));

					byte[] both1 = Utilitaires.concat(timestampB, typeB);
					byte[] both = Utilitaires.concat(both1, dataB);

					ByteBuffer bbuffer = ByteBuffer.wrap(both);

					if (!message.isEmpty()) {
						Commande.addCommande(bbuffer); // On ajoute la trame aux commandes à traiter
					}
				} else if (Arrays.equals(typeB, villeB)) { // La trame est une trame de changement de localisation pour la météo
					char[] finVille = new char[4];
					in.read(finVille, 0, 4); // On récupère le reste des données (code postal)
					String finVilleString = Utilitaires
							.stringToHexa(new String(finVille));

					String[] tableau = finVilleString.split(" ");
					String villeString = "";
					for (int i = 0; i < tableau.length; i++) {
						if (tableau[i].length() == 1) {
							villeString += '0';
						}
						villeString += tableau[i];
					}

					int cp = Integer.parseInt(villeString, 16);
					Constantes.VILLE = cp;
					// System.out.println("Nouveau code postal : "+
					// Constantes.VILLE);
					Meteo m = new Meteo(Constantes.VILLE);
					System.out
							.println("Client lecture Ghome - nouvelle ville : "
									+ m.getVille());
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			// On suspend le thread pour laisser les autres

			try {
				clientLectureGhThread.sleep(1000);
			} catch (InterruptedException e) { // TODO Auto-generated catch
				// block
				e.printStackTrace();
			}
		}
		// Fermeture du socket

		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}