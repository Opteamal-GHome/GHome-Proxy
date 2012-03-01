import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Main {

	/**
	 * @param args
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	public static void main(String[] args) throws Exception {

		// Lancement du serveur (test)
		// ServeurEnvoiGHome serv = new
		// ServeurEnvoiGHome(Constantes.PORT_SERV_ENVOI);

		Socket gHomeSocket = new Socket(InetAddress
				.getByName(Constantes.IP_GHOME), Constantes.PORT_GHOME);
		Socket baseSocket = new Socket(InetAddress.getByName(Constantes.IP_BASE), Constantes.PORT_BASE);

		// Lancement du thread d'envoi des trames de la base
		new ClientEnvoiBase(baseSocket);

		// thread commande
		new Commande();

		new ClientEnvoiGHome(gHomeSocket);
		new ClientLectureGHome(gHomeSocket);

		// Lancement du thread de parsing des trames
		new Parseur();

		// Lancement du thread de réception des trames de la base
		new ClientLectureBase(baseSocket);
	}

}
