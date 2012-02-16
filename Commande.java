import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Commande implements Runnable {
	private static List<ByteBuffer> listeTramesCommande;
	private Thread commandeThread = null;

	public Commande() {
		System.out.println("Commande : dans le constructeur");
		listeTramesCommande = new ArrayList<ByteBuffer>();
		commandeThread = new Thread(this);
		commandeThread.start();
	}

	public static void addCommande(ByteBuffer commandFromServer) {
		synchronized (listeTramesCommande) {
			listeTramesCommande.add(commandFromServer);
			listeTramesCommande.notify();
		}
	}

	public List<String> createCommands(ByteBuffer buffer) 
	{
		
		for (int i=0; i < buffer.capacity(); i++) {
			System.out.print(buffer.get(i));
			System.out.print(" ");
		}

		System.out.println("In createCommands " + buffer.capacity());
		List<String> commands = new ArrayList<String>();
		// TODO extract corresponding bytes
		//char typeOfMessage = 'x'; // 'O' -> order
		int idLogique = -1; // device to command
		int data = -1; // 1 to turn on, 0 to turn off
		
		/* On recupere le 8e octet du buffer. Celui qui provient du serveur est code sur
		 * 1 octet. Un char en Java fait 2 octets.
		 * On procede donc a une transformation vers UTF-8
		 */
		byte[] bufferTypeOfMess = new byte[1];
		bufferTypeOfMess[0] = buffer.get(8);
		String typeOfMess = "";
		try {
			typeOfMess = new String(bufferTypeOfMess, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}

		idLogique = buffer.getInt(9);
		System.out.println("idLogique : " + idLogique);

		data = buffer.getInt(13); // get the int corresponding to the command
		System.out.println("timestamp : " + buffer.getLong(0) + " - Type : " + typeOfMess + " - idLogique : "
				+ idLogique + "  - data : " + data);
		DeviceLogique devLog = EnsembleDevices.getDeviceLogiquebyID(idLogique);
		DevicePhysique devPhy = devLog.getDevicePhysique();

		if (typeOfMess.equals("O")) // frame of type Order
		{
//			if (devPhy.getIdPhysique() == Constantes.ID_PRISE) {
//				commands = createFrameForContact(data);
//				System.out.println("Commande : " + commands.get(0).toString());
//			}

			commands = createFrameForContact(data, devPhy.getIdPhysique());

		}
		return commands;
	}
	

	public static List<String> createFrameForContact(int data, long id) {
		List<String> commands = new ArrayList<String>();
		// create a TX-Telegram received from a Rocker Switch (RPS)
		
		String telegram = "A55A6B05";

		if (data == 1) // open Contact; Button B1 pushed
		{
			System.out.println("******************open contact****************");
			telegram += "50";
		} else // Button B0 pushed; close contact
		{
			telegram += "70";
		}
		telegram += "000000";
		// l'id de notre prise
//		telegram += "FF9F1E05";
		String idString = Long.toHexString(id);
		telegram += idString;
		// status
		telegram += "30";
		// checksum
		telegram += "00";

		commands.add(telegram);
		return commands;
	}

	public void run() {
		while(true) {
			synchronized (listeTramesCommande) {
				if (listeTramesCommande.isEmpty()) {
					try {
						listeTramesCommande.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	
				}
	
				if (listeTramesCommande.size() > 0) {
					List<String> commandes = createCommands(listeTramesCommande.remove(0));
					listeTramesCommande.notify(); 
					if (commandes != null && commandes.size() > 0) {
						System.out.println("Rentree dans la boucle magique !");
						for (int i = 0; i < commandes.size(); i++) {
							System.out.println("------------------send command to data base---------------");
							ClientEnvoiBase.addToList(commandes.get(i));
						}
					}
				}

			}
		}

	}
}