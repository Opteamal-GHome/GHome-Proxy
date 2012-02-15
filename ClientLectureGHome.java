import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

public class ClientLectureGHome implements Runnable {

	private Thread clientLectureGhThread = null;
	private Socket socket;
	private BufferedReader in;
	public boolean continuer = true;
	
	public static Charset charset = Charset.forName("UTF-8");
	public static CharsetEncoder encoder = charset.newEncoder();


	public ClientLectureGHome(InetAddress adresseIP, int port) {

		System.out.println("ClientLectureGHome : dans le constructeur");

		// Création du Thread
		clientLectureGhThread = new Thread(this);

		// Création du socket
		try {
			socket = new Socket(adresseIP, port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("ClientLectureGHome : Thread et Socket créés");

		// Lancement du Thread
		clientLectureGhThread.start();
	}

	public ClientLectureGHome(Socket socket) {
		this.socket = socket;

		// Lancement Thread
		this.clientLectureGhThread = new Thread(this);
		this.clientLectureGhThread.start();

		System.out.println("Constructeur Client lecture GHome OK");
	}

	@Override
	public void run() {
		InputStream input = null;

		while (continuer) {
			System.out.println("ClientLectureGHome : Dans le while");
			

			try {
				
				// Lit la valeur du socket
				input = socket.getInputStream();
				
				System.out.println("input available ClientLectureGHome : " + input.available());
				InputStreamReader isr = new InputStreamReader(input);
				
				System.out.println("Apres getInputStream ClientLectureGHome");
				in = new BufferedReader(isr);
				System.out.println("Apres in ClientLectureGHome : ");
				if (in.ready()) {
					//String message = in.readLine();
					String message;
					char[] mess = new char[18];
					in.read(mess, 0, 18);
					message = new String(mess);
					
					System.out.println("Client Lecture GHome : Message recu : " + Utilitaires.stringToHexa(message));
					//ByteBuffer bbuffer = encoder.encode(CharBuffer.wrap(message));
					
					byte[] timestampB = message.substring(0, 8).getBytes();
					System.out.println(Utilitaires.stringToHexa(new String(timestampB)));
					byte[] typeB = message.substring(8,9).getBytes("UTF-8");
					System.out.println(Utilitaires.stringToHexa(new String(typeB)));
					byte[] dataB = message.substring(9).getBytes();
					System.out.println(Utilitaires.stringToHexa(new String(dataB)));
					
					byte[] both1 = Utilitaires.concat(timestampB, typeB);
					byte[] both = Utilitaires.concat(both1, dataB);
					
					
					ByteBuffer bbuffer = ByteBuffer.wrap(both);
					
					
					if (!message.isEmpty()) {
						Commande.addCommande(bbuffer);
					}

				} else {
					System.out.println("dedans !!!");
				}
				
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			// On suspend le thread pour laisser les autres
			
			try { clientLectureGhThread.sleep(1000); } catch
			 (InterruptedException e) { // TODO Auto-generated catch block
			 e.printStackTrace(); }
			 

			// Fermeture du socket
			/*try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}*/
		}
	}
	
	

}