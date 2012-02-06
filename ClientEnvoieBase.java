import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;



public class ClientEnvoieBase implements Runnable 

{
	private boolean OK = true;
	private static Thread clientThread = null;
	private Socket socket;
	private static List<String> commandes; 
	
	private static List<String> toSend = new ArrayList<String>();
	
	//this method is called in the run method of the Commande class
	public static void addToList( String command)
	{
		synchronized (toSend) {
			toSend.add(command);
			toSend.notify();
		}
	}
	
	public ClientEnvoieBase (InetAddress ip, int port)
	{
		clientThread = new Thread(this);
		commandes = new ArrayList<String>();
		
		try {
			this.socket = new Socket(ip, port);
			System.out.println("client socket created");
		} catch (IOException e) {
			e.printStackTrace();
		}
		clientThread.start();
	}

	public void run()
	{
		while(OK)
		{
			System.out.println("in the client's while ");
			try 
			{
				String messageToSend = null;
				synchronized(toSend)
				{
					if (toSend.size() == 0){
						try {
							toSend.wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					messageToSend = toSend.remove(0);
					toSend.notify(); // necessary ?
				}
				
				ObjectOutputStream outToServer = new ObjectOutputStream(socket.getOutputStream());
				byte [] byteMessage = messageToSend.getBytes();

				outToServer.writeObject(byteMessage);
				System.out.println("client sent object ");

						
			} catch (IOException e) 
			{
				e.printStackTrace();
			} 
			
		}
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
