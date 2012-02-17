import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

public class ClientLectureBase implements Runnable {
	
	private Thread clientLectureThread = null;
	private Socket _socket;
	public boolean continuer = true;
	public char[] cbuf = new char[Constantes.TAILLE_TRAME_ENOCEAN];

	public ClientLectureBase(Socket baseSocket) 
	{
		
		System.out.println("ClientLectureBase : dans le constructeur");
		
		// Création du Thread
		clientLectureThread = new Thread(this);
		
		this._socket = baseSocket;
		System.out.println("ClientLectureBase : Thread et Socket créés");
		
		// Lancement du Thread
		clientLectureThread.start();
	}

	@Override
	public void run() {
		InputStream input = null;
		while (continuer) {
			
			System.out.println("ClientLectureBase : Dans le while");

			// On récupère les données
			try {
				input = _socket.getInputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("ClientLectureBase : Après getInputStream");

			BufferedReader response = new BufferedReader(new InputStreamReader(input));
			
			// On attend d'avoir 28 octets et on ajoute la trame aux trames à parser
			try {
				int msg = response.read(cbuf, 0, Constantes.TAILLE_TRAME_ENOCEAN);
				if (msg > 0) {
					//System.out.print("ClientLectureBase : trame reçue : ");
					for(int i=0; i<cbuf.length; i++)
					{
						System.out.print(cbuf[i]);
					}
					System.out.println();
					Parseur.addTrame(cbuf);
				} else {
					System.out.println("Pas de message");
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
