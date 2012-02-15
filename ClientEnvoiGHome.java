import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class ClientEnvoiGHome  implements Runnable {

	private Thread clientEnvoiThread = null;
	private Socket socket;
	public boolean continuer = true;
	public static List<ProxyTrame> listeProxyTrames;

	public ClientEnvoiGHome(InetAddress adresseIP, int port) {

		System.out.println("ClientEnvoiGHome : dans le constructeur");

		// Création du socket
		try {
			socket = new Socket(adresseIP, port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Création du Thread
		clientEnvoiThread = new Thread(this);
		
		System.out.println("ClientEnvoiGHome : Thread et Socket créés");

		// Création de la liste
		listeProxyTrames = new ArrayList<ProxyTrame>();
		
		// Lancement du Thread
		clientEnvoiThread.start();
		
		//ajouter les capteurs et envoyer les trames d'ajout au serveur
		this.init();
	}
	
	private void init()
	{
		EnsembleDevices.parseDeviceFile(Constantes.pathToDeviceFile);
		int i;
		DevicePhysique devP;
		List<DeviceLogique> devLogList;
		int length = EnsembleDevices.mapDevicesPhysiques.size();
		long timestamp = System.currentTimeMillis();
		
		for (long mapKey : EnsembleDevices.mapDevicesPhysiques.keySet()) 
		{
			devP = EnsembleDevices.mapDevicesPhysiques.get(mapKey);
			devLogList = devP.getListeDevicesLogiques();
			int j;
			for (j = 0; j < devLogList.size(); j++)
			{
				ProxyTrameS addDevFrame = new ProxyTrameS(timestamp, 'S', 'A', 
						devLogList.get(j).getIdLogique(), devLogList.get(j).getTypeLogique());
				System.out.println("type dev phy = " + devP.getTypePhysique()
						+ "  type dev Log = "+ devLogList.get(j).getTypeLogique() + 
						"  id dev Log = " + devLogList.get(j).getIdLogique());
				synchronized (listeProxyTrames) {
					// listeProxyTrames.wait();
					listeProxyTrames.add(addDevFrame);
					listeProxyTrames.notify();
				}
				
			}
		}
		//System.out.println(listeProxyTrames.size());
	}
	
	
	public ClientEnvoiGHome(Socket socket) {
		this.socket = socket;
		
		// Lancement Thread
		this.clientEnvoiThread = new Thread(this);
		
		// Création de la liste
		listeProxyTrames = new ArrayList<ProxyTrame>();

		// Lancement du Thread
		clientEnvoiThread.start();
		
		System.out.println("Constructeur Envoi GHome OK");
		
		this.init();
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
			socket.close();
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
			
			// On recupere le timestamp
			Long timestamp = proxyTrame.getTimestamp();
			timestamp = timestamp / 1000;
			timestamp = (long) Math.round(timestamp);
			
			byte[] timestampB = Utilitaires.toByteArray(timestamp);

			// On recupere le type de la trame
			byte[] typeTrame =  (proxyTrame.getType()+"").getBytes();

			// On recupere les bytes correspondant aux attributs de proxyTrame
			byte[] resteTrame = proxyTrame.encodeTrame();
			
			// On concatene les trois
			byte[] both1 = Utilitaires.concat(timestampB, typeTrame);
			byte[] both= Utilitaires.concat(both1, resteTrame);
						
			// On l'envoie
			if(socket.isConnected()) {
				
				DataOutputStream dataReturn = new DataOutputStream(socket.getOutputStream());
				System.out.print("Trame envoi : ");
				for (int i=0; i < both.length; i++) { System.out.print(both[i]); System.out.print(" "); }
				System.out.println("");
				dataReturn.write(both);
				
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	


}