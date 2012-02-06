import java.nio.ByteBuffer;
import java.util.ArrayList;


public class Commande implements Runnable
{
	private static ArrayList<ByteBuffer> listeTramesCommande;
	private Thread commandeThread = null;

	
	Commande()
	{
		System.out.println("Commande : dans le constructeur");
		listeTramesCommande = new ArrayList<ByteBuffer>();
		commandeThread = new Thread(this);
		commandeThread.start();
	}
	
	
	public static void addCommande(ByteBuffer commandFromServer) 
	{
		synchronized (listeTramesCommande) 
		{
			listeTramesCommande.add(commandFromServer);
			listeTramesCommande.notify();
		}
	}
	
	public ArrayList<String> createCommands(/*byte[]*/ ByteBuffer buffer) 
	{
	    //ByteBuffer bb = ByteBuffer.allocate(18);
	    //continet toute l'information en bytes
		
		System.out.println("In createCommands ");
		ArrayList<String> commands = new ArrayList<String>();
		//TODO extract corresponding bytes
		char typeOfMessage = 'x'; // 'O' -> order
		int idLogique = -1; //device to command
		int data = -1; //1 to turn on, 0 to turn off
		
		typeOfMessage = buffer.getChar(8); // get char starting with the 8th byte in the buffer
		idLogique = buffer.getInt(10);
		data = buffer.getInt(14); // get the int corresponding to the command
		
		DeviceLogique devLog = EnsembleDevices.getDeviceLogiquebyID(idLogique);
		DevicePhysique devPhy = devLog.getDevicePhysique();
		
		if (typeOfMessage == 'O') // frame of type Order
		{	
			if(devPhy.getIdPhysique() == Constantes.ID_PRISE)
			{
				commands = createFrameForContact(data);
			}
			
		}	
		return commands;	
	}

	public static ArrayList<String> createFrameForContact (int data)
	{	
		ArrayList<String> commands = new ArrayList<String>();
		//create a TX-Telegram received from a Rocker Switch (RPS)
		String telegram = "A55A6B05";
		
		if(data == 1) // open Contact; Button B1 pushed
		{
			telegram += "50";
		}
		else //Button B0 pushed; close contact
		{
			telegram += "70";
		}
		telegram += "000000";
		//l'id de notre prise
		telegram += "FF9F1E07";
		//status
		telegram += "30";
		//checksum
		telegram += "00";

		commands.add(telegram);
		return commands;	
	}
	
	public void run()
	{
		synchronized(listeTramesCommande){
			if (listeTramesCommande.isEmpty())
				try {
					listeTramesCommande.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				ArrayList<String> commandes = createCommands(listeTramesCommande.remove(0));
				listeTramesCommande.notify(); //necessary ?
				if(commandes != null && commandes.size() > 0)
				{
					for (int i = 0; i < commandes.size(); i++)
					{
						ClientEnvoieBase.addToList(commandes.get(i));
					}
				}

		}
		
	}	
}