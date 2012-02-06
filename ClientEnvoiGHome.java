import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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
			String chaine = "";
			
			// On recupere le timestamp
			Long timestamp = proxyTrame.getTimestamp();
			timestamp = timestamp / 1000;
			timestamp = (long) Math.round(timestamp);
			
			byte[] timestampB = toByteArray(timestamp);

			// On recupere le type de la trame
			chaine += proxyTrame.getType();

			// On recupere les bytes correspondant aux attributs de proxyTrame
			chaine += proxyTrame.encodeTrame();
			byte[] chaineTransformee = chaine.getBytes();
			
			// On concatene les deux 
			byte[] both= concat(timestampB, chaineTransformee);
						
			// On l'envoie
			if(_socket.isConnected()) {
				DataOutputStream dataReturn = new DataOutputStream(_socket.getOutputStream());
				dataReturn.write(both);
				System.out.println(chaine);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static byte[] concat(byte[] first, byte[] second) {
		byte[] result = Arrays.copyOf(first, first.length + second.length);
		  System.arraycopy(second, 0, result, first.length, second.length);
		  return result;
		}
	
	public static byte[] toByteArray(long l) {
	     return new byte[] { 
	        (byte)((l >> 56) & 0xff),
	         (byte)((l >> 48) & 0xff),
	         (byte)((l >> 40) & 0xff),
	         (byte)((l >> 32) & 0xff),
	         (byte)((l >> 24) & 0xff),
	         (byte)((l >> 16) & 0xff),
	         (byte)((l >> 8) & 0xff),
	         (byte)((l >> 0) & 0xff),
	     };
	 }

}