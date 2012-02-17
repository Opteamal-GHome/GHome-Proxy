import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import meteo.Meteo;

public class Main {

	/**
	 * @param args
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	public static void main(String[] args) throws Exception {

		// TODO Vérifier ordre de création + faire le lancement des threads
		// (start) dans le main plutôt que dans les constructeurs

		// Lancement du serveur (test)
		 //sServeurEnvoiGHome serv = new ServeurEnvoiGHome(Constantes.PORT_SERV_ENVOI);

		Socket gHomeSocket = new Socket (InetAddress.getByName(Constantes.IP_GHOME),Constantes.PORT_GHOME);
		new ClientEnvoiGHome(gHomeSocket);
		new ClientLectureGHome(gHomeSocket);
		
		// Lancement du thread de parsing des trames
		 Parseur parseur = new Parseur();

		// thread commande
		 new Commande();

		Socket baseSocket = new Socket (InetAddress.getByName(Constantes.IP_BASE),Constantes.PORT_BASE); 
		// Lancement du thread d'envoi des trames de la base
		 ClientEnvoiBase clientBase = new ClientEnvoiBase(baseSocket);

		// Lancement du thread de réception des trames de la base
		 ClientLectureBase clientLect = new ClientLectureBase(baseSocket);
		

		Meteo meteo = new Meteo();
		System.out.println(meteo.getTemperature());

		/*********** tests ***************/
		/*
		 * char[] trame =
		 * {'A','5','5','A','0','B','0','5','0','0','0','0','0','0'
		 * ,'0','0','0','0','2','1','C','B','E','5','2','0','0','1'}; String
		 * dataByte0 = trame[14]+""+trame[15]; System.out.println(dataByte0);
		 * int dataByte0Binaire = Integer.parseInt(dataByte0, 16);
		 * System.out.println(Integer.(dataByte0Binaire));
		 */

		// // Trame quelconque
		// char[] trame =
		// {'A','5','5','A','0','B','0','5','0','0','0','0','0','0','0','0','0','0','2','1','C','B','E','5','2','0','0','1'};
		// Parseur.addTrame(trame);
		// // Trame teach-in
		// char[] trame2 =
		// {'A','5','5','A','0','B','0','5','0','0','0','0','0','0','8','7','0','0','2','1','C','B','E','5','2','0','0','1'};
		// Parseur.addTrame(trame2);
		// // Trame de données
		// char[] trame3 =
		// {'A','5','5','A','0','B','0','5','0','0','0','0','0','0','0','A','0','0','2','1','C','B','E','5','2','0','0','1'};
		// Parseur.addTrame(trame3);

		// char[] trame =
		// {'A','5','5','A','0','B','0','7','1','0','0','8','0','2','8','7','0','0','0','4','E','9','5','7','0','0','8','8'};
		//		
		// int[] funcType = Parseur.parseFuncAndType(trame);
		// System.out.println("func "+funcType[0]);
		// System.out.println("type "+funcType[1]);
		// System.out.println(Integer.parseInt("060001", 16));

		// Trame du contact
		// for(int i=0;i<999999999;i++);
		// contact.redemarrerTimer();

		// send command telegram to device
		// ClientEnvoiBase.addToList("A55A6B0570000000FF9F1E073000");
		//ClientEnvoiBase.addToList("A55A6B0550000000FF9F1E073000"); // turns on the contact
	}

}
