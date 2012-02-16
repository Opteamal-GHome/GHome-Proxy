import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;



public class ClientEnvoiBase implements Runnable 

{
	private boolean OK = true;
	private static Thread clientThread = null;
	private Socket socket;
	private static List<String> toSend = new ArrayList<String>(); // list of frame commands

	//this method is called in the run method of Commande
	public static void addToList( String command)
	{
		synchronized (toSend) {
			toSend.add(command);

			System.out.println("//////////////////add command to list /////////////// "+ command);
			toSend.notify();
		}
	}

	public ClientEnvoiBase (Socket baseSocket)
	{
		this.socket = baseSocket;
		clientThread = new Thread(this);
		System.out.println("ClientEnvoiBase : socket and thread created");
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
					if (toSend.isEmpty())
					{
						try {
							toSend.wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if(!toSend.isEmpty()) 
					{
						messageToSend = toSend.remove(0);
						System.out.println("!!!!!!!!!!!!!! toSendMessage !!!!!!!!!!!!!!!!!"+messageToSend);
					}

					toSend.notify();
				}
			
				DataOutputStream dataOut = new DataOutputStream (socket.getOutputStream());
				dataOut.write(messageToSend.getBytes());

//				ObjectOutputStream outToServer = new ObjectOutputStream(socket.getOutputStream());
//				byte [] byteMessage = messageToSend.getBytes();
//				outToServer.writeObject(byteMessage); 
				
				System.out.println("ClientEnvoiBase : sent frame to device Base ! ");


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
