import java.util.ArrayList;

/**
 * 
 * Thread récupérant les tâches reçues, les parsant, puis les envoyant au serveur GHome
 *
 */
public class Parseur implements Runnable {


	private static ArrayList<char[]> listeTrames;
	private Thread parseurThread = null;
	public static boolean continuer = true;
	
	Parseur()
	{
		System.out.println("Parseur : dans le constructeur");
		Parseur.listeTrames = new ArrayList<char[]>();
		this.parseurThread = new Thread(this);
		this.parseurThread.start();
	}

	public static void addTrame(char[] trame) 
	{
		synchronized (listeTrames) {
			//listeTrames.wait();
			listeTrames.add(trame);
			listeTrames.notify();
		}
	}

	public ArrayList<ProxyTrame> parseTrame(char[] trame) {
		
		System.out.println("Parseur : dans parseTrame");
		
		int dataByte0_1 = Integer.parseInt((""+trame[Constantes.DATA_BYTE_0_1]), 16);
		int dataByte0_2 = Integer.parseInt((""+trame[Constantes.DATA_BYTE_0_2]), 16);
		
		long id = parseID(trame);
		
		ArrayList<ProxyTrame> listeProxyTrames = new ArrayList<ProxyTrame>();
		long timestamp = System.currentTimeMillis();

		// teach-in frame and unknown device
		if (EnsembleDevices.getDevicePhysiqueByID(id) == null && (((dataByte0_1 & 8) == 0) && ((dataByte0_2 & 8) == 1))) 
		{
			//parse teach in for 4BS
			int org = Integer.parseInt(""+trame[Constantes.ORG_1]+trame[Constantes.ORG_2], 16); //has to be 7
			int[] funcType = parseFuncAndType(trame);
			
			listeProxyTrames = parseTeachIn(id, org, funcType[0], funcType[1], timestamp);
		}
		else if(EnsembleDevices.getDevicePhysiqueByID(id) == null)
		{
			//parse frame of type 'S' for devices without teachin
			System.out.println("capteur inconnu !");
		}
		else // Trame de données
		{
			// remettre le watchdog à 0 (car on a bien reçu une trame de ce capteur)
			EnsembleDevices.getDevicePhysiqueByID(id).redemarrerTimer();
			listeProxyTrames = parseTrameD(id, trame, timestamp);
		}
		return listeProxyTrames;
	}
	
	public static int[] parseFuncAndType(char[] trame)
	{
		int funcType[] = new int[2];
		
		int db3 = Integer.parseInt(""+trame[Constantes.DATA_BYTE_3_1]+trame[Constantes.DATA_BYTE_3_2],16);
		String db3String = Integer.toBinaryString(db3);
		while (db3String.length() < 8)
		{
			db3String = "0" + db3String;
		}
		int func = Integer.parseInt(db3String.substring(0, 6),2); // first 5 bits
		
		int db2 = Integer.parseInt(""+trame[Constantes.DATA_BYTE_2_1]+trame[Constantes.DATA_BYTE_2_2],16);
		String db2String = Integer.toBinaryString(db2);
		while (db2String.length() < 8)
		{
			db2String = "0" + db2String;
		}
		
		String typeString = db3String.substring(6, 8)+db2String.substring(0,5);
		int type = Integer.parseInt(typeString, 2);
		funcType[0] = func;
		funcType[1] = type;
		return funcType;
	
	}
	
	public static ArrayList<ProxyTrame> parseTeachIn (long id, int org, int func, int type, long timestamp)
	{
		System.out.println("Parseur : dans parseTeachIn");
		ArrayList<ProxyTrame> listeTramesS = new ArrayList<ProxyTrame>();
		
		switch (func)
		{
		case 8: 
			//eep 07-08-01 -> Light, Temperature & Occupancy Sensor = LTO
			switch(type)
			{
			case 1:
				listeTramesS = TeachInLTO(id, timestamp); 
			}
		
		case 2: 
			//eep 07-02-05 -> temperature sensor, 0-40 deg C	
			switch(type)
			{
			case 5: 
				listeTramesS = TeachInTemp40(id, timestamp);
			
			}
		}
			
		return listeTramesS;
	}
	
	
	public static ArrayList<ProxyTrame> TeachInTemp40 (long id, long timestamp)
	{
		System.out.println("Parseur : dans TeachInTemp40");
		ArrayList<ProxyTrame> listeTramesS = new ArrayList<ProxyTrame>();
		ArrayList<DeviceLogique> listeDevLog = new ArrayList<DeviceLogique>();
		DevicePhysique devPhysique = new DevicePhysique(id, "07-02-05", listeDevLog);
		DeviceLogique tempDevice = new DeviceLogique(EnsembleDevices.getNextIdLogique(), 'T', devPhysique);
		listeDevLog.add(tempDevice);
		
		EnsembleDevices.ajouterDevice(id, devPhysique);
		
		ProxyTrameS proxyTrame1 = new ProxyTrameS();
		proxyTrame1.setTimestamp(timestamp);
		proxyTrame1.setType('S');
		proxyTrame1.setDeviceId(tempDevice.getIdLogique());
		proxyTrame1.setTypeMessage('A'); //add
		proxyTrame1.setTypeDevice('T');
		listeTramesS.add(proxyTrame1);
		
		return listeTramesS;
	}
	
	
	public static ArrayList<ProxyTrame> TeachInLTO (long id, long timestamp)
	{
		System.out.println("Parseur : dans TeachInLTO");
		ArrayList<ProxyTrame> listeTramesS = new ArrayList<ProxyTrame>();
		ArrayList<DeviceLogique> listeDevLog = new ArrayList<DeviceLogique>();
		DevicePhysique devPhysique = new DevicePhysique(id, "07-08-01", listeDevLog);
		//todo voltage ?
		DeviceLogique lightDevice = new DeviceLogique(EnsembleDevices.getNextIdLogique(), 'L', devPhysique);
		DeviceLogique tempDevice = new DeviceLogique(EnsembleDevices.getNextIdLogique(), 'T', devPhysique);
		DeviceLogique presDevice = new DeviceLogique(EnsembleDevices.getNextIdLogique(), 'P', devPhysique);
		
		//l'ordre dans la liste des devices logiques est importante; liste[0]=lightDev etc
		listeDevLog.add(lightDevice);
		listeDevLog.add(tempDevice);
		listeDevLog.add(presDevice);
		EnsembleDevices.ajouterDevice(id, devPhysique);
		
		ProxyTrameS proxyTrame1 = new ProxyTrameS();
		proxyTrame1.setTimestamp(timestamp);
		proxyTrame1.setType('S');
		proxyTrame1.setDeviceId(lightDevice.getIdLogique());
		proxyTrame1.setTypeMessage('A'); //add
		proxyTrame1.setTypeDevice('L');
		listeTramesS.add(proxyTrame1);
		
		ProxyTrameS proxyTrame2 = new ProxyTrameS();
		proxyTrame1.setTimestamp(timestamp);
		proxyTrame1.setType('S');
		proxyTrame1.setDeviceId(tempDevice.getIdLogique());
		proxyTrame1.setTypeMessage('A'); //add
		proxyTrame1.setTypeDevice('T');
		listeTramesS.add(proxyTrame2);
		
		ProxyTrameS proxyTrame3 = new ProxyTrameS();
		proxyTrame1.setTimestamp(timestamp);
		proxyTrame1.setType('S');
		proxyTrame1.setDeviceId(presDevice.getIdLogique());
		proxyTrame1.setTypeMessage('A'); //add
		proxyTrame1.setTypeDevice('P');
		listeTramesS.add(proxyTrame3);
		
		return listeTramesS;
	}
	
	
	public static ArrayList<ProxyTrame> parseTrameD(long idPhysique, char[] trame, long timestamp) {
		
		System.out.println("Parseur : dans parseTrameD");
		
		ArrayList<ProxyTrame> listeProxyTrames = new ArrayList<ProxyTrame>();

		// Chercher id dans la map
		DevicePhysique devPhysique = EnsembleDevices.getDevicePhysiqueByID(idPhysique);
		if(devPhysique == null)
		{
			return null;
		}
		
		String typePhysique = devPhysique.getTypePhysique();
		//can't do a switch on String or Long
		if(typePhysique == "06-00-01")
		{
			listeProxyTrames = parseTypeContact(trame, devPhysique, timestamp);
		}
		
		else if (typePhysique == "05-02-01")
		{
			listeProxyTrames = parseTypeInterrupteur(trame, devPhysique, timestamp);
		}
		else if(typePhysique == "07-08-01") //Light, Temp and Occupancy
		{
			listeProxyTrames = parseTypeLTO (trame, devPhysique, timestamp);
		}
		
		else if(typePhysique == "07-02-05") //Temperature sensor
		{
			//todo parsing for temperature sensor
		}
		
		return listeProxyTrames;
	}
	
	
	//1 Interrupteur physique = 1 capteur logique
	
	public static ArrayList<ProxyTrame> parseTypeInterrupteur (char[] trame, DevicePhysique devPhysique, long timestamp)
	{
		System.out.println("Parseur : dans parseTypeInterrupteur");
		
		ArrayList<ProxyTrame> listeTrames = new ArrayList<ProxyTrame>();
		DeviceLogique devLog = devPhysique.getListeDevicesLogiques().get(0);
		
		ProxyTrameD proxyTrameD1 = new ProxyTrameD(); 
		proxyTrameD1.setTimestamp(timestamp);
		proxyTrameD1.setType('D');
		proxyTrameD1.setDeviceId(devLog.getIdLogique());

		int status1 = Integer.parseInt(""+trame[Constantes.STATUS_1],16);
		int dataByte3_1 = Integer.parseInt(""+trame[Constantes.DATA_BYTE_3_1], 16);
		
		//NU = 1; one or two buttons pressed
		if((status1 & 1) == 1)
		{
			boolean pressed;
			//action on first Button
			if ((dataByte3_1 & 1) == 1)
			{
				pressed = true;
			}
			else
				pressed = false;
				
			if (dataByte3_1 == 0 || dataByte3_1 == 1) //channel A1
			{		
				if(pressed)
					proxyTrameD1.setValeurLue(30);
				else
					proxyTrameD1.setValeurLue(31);
				
			}
			else if ((dataByte3_1 & 14) == 2) //channel A0
			{
				if(pressed)
					proxyTrameD1.setValeurLue(10);
				else
					proxyTrameD1.setValeurLue(11);
			}
			else if ((dataByte3_1 & 14) == 4) //channel B1
			{
				if(pressed)
					proxyTrameD1.setValeurLue(40);
				else
					proxyTrameD1.setValeurLue(41);
			}
			else //channel B0
			{
				if(pressed)
					proxyTrameD1.setValeurLue(20);
				else
					proxyTrameD1.setValeurLue(21);
			}		
			listeTrames.add(proxyTrameD1);
			
			//secondAction
			int dataByte3_2 = Integer.parseInt(""+trame[Constantes.DATA_BYTE_3_2],16);
			if((dataByte3_2 & 1) == 1) //there's a second action
			{
		
				ProxyTrameD proxyTrameD2 = new ProxyTrameD();
				proxyTrameD2.setTimestamp(timestamp);
				proxyTrameD2.setType('D');
				proxyTrameD2.setDeviceId(devLog.getIdLogique());
				
				if (dataByte3_2 == 0 || dataByte3_2 == 1) //channel A1
				{
					if(pressed)
						proxyTrameD1.setValeurLue(30);
					else
						proxyTrameD1.setValeurLue(31);
				}
				else if ((dataByte3_2 & 14) == 2) //channel A0
				{
					if(pressed)
						proxyTrameD1.setValeurLue(10);
					else
						proxyTrameD1.setValeurLue(11);
				}
				else if ((dataByte3_2 & 14) == 4) //channel B1
				{
					if(pressed)
						proxyTrameD1.setValeurLue(40);
					else
						proxyTrameD1.setValeurLue(41);
				}
				else
				{
					if(pressed)
						proxyTrameD1.setValeurLue(20);
					else
						proxyTrameD1.setValeurLue(21);
				}
				listeTrames.add(proxyTrameD2);
			}
		}
		
		else //NU = 0; more than two buttons pressed simultaneously or No button pressed
		{
			if(dataByte3_1 == 1 || dataByte3_1 == 0)
			{
				//no buttons were pressed
			}
			else if ((dataByte3_1 & 14) == 6) // 3 or 4 buttons pressed
			{
				ProxyTrameD proxyTrameErr1 = new ProxyTrameD();
				proxyTrameErr1.setDeviceId(devLog.getIdLogique());
				proxyTrameErr1.setValeurLue(-1);
				proxyTrameErr1.setTimestamp(timestamp);
				proxyTrameErr1.setType(Constantes.TYPE_DONNEES);
				listeTrames.add(proxyTrameErr1);
			}
		}
		return listeTrames;
	}
	
	public static ArrayList<ProxyTrame> parseTypeContact(char[] trame, DevicePhysique devPhysique, long timestamp)
	{
		System.out.println("Parseur : dans parseTypeContact");
		ArrayList<ProxyTrame> listeTrames = new ArrayList<ProxyTrame>();
		ProxyTrameD proxyTrameD = new ProxyTrameD();
		
		int dataByte3_2 = Integer.parseInt(""+trame[Constantes.DATA_BYTE_3_2],16);
		
		proxyTrameD.setValeurLue(dataByte3_2 & 1);
		DeviceLogique devLog = devPhysique.getListeDevicesLogiques().get(0);
		proxyTrameD.setDeviceId(devLog.getIdLogique());
		
		proxyTrameD.setType(Constantes.TYPE_DONNEES);
		proxyTrameD.setTimestamp(timestamp);
		
		listeTrames.add(proxyTrameD);
		return listeTrames;
	}	

	
	public static long parseID(char[] trame){
		long idPhysique = Integer.parseInt(("" + trame[Constantes.ID_BYTE_3_1] + trame[Constantes.ID_BYTE_3_2] + 
				trame[Constantes.ID_BYTE_2_1] + trame[Constantes.ID_BYTE_2_2] + trame[Constantes.ID_BYTE_1_1] + 
				trame[Constantes.ID_BYTE_1_2] + trame[Constantes.ID_BYTE_0_1] + trame[Constantes.ID_BYTE_0_2]), 16);
		return idPhysique;
	}

	public static ArrayList<ProxyTrame> parseTypeLTO (char[] trame, DevicePhysique devPhysique, long timestamp)
	{
		System.out.println("Parseur : dans parseTypeLTO");
		ArrayList<ProxyTrame> listeTrames = new ArrayList<ProxyTrame>();
	
		int luminositeTh = Integer.parseInt(""+trame[Constantes.DATA_BYTE_2_1]+trame[Constantes.DATA_BYTE_2_2],16);
		int temperatureTh = Integer.parseInt(""+trame[Constantes.DATA_BYTE_1_1]+trame[Constantes.DATA_BYTE_1_2],16);
		int db_0 = Integer.parseInt(""+trame[Constantes.DATA_BYTE_0_2], 16);
		int presence;
		if((db_0 & 1) == 1)
		{
			presence = 0; //button released
		}
		else
		{
			presence = 1;
		}
		
		int luminosite = luminositeTh*510/255;
		int temperature = temperatureTh*51/255;
		
		ProxyTrameD proxyTrameD1 = new ProxyTrameD();
		proxyTrameD1.setType('D');
		proxyTrameD1.setTimestamp(timestamp);
		proxyTrameD1.setDeviceId(devPhysique.getListeDevicesLogiques().get(0).getIdLogique());
		proxyTrameD1.setValeurLue(luminosite);
		listeTrames.add(proxyTrameD1);
		
		ProxyTrameD proxyTrameD2 = new ProxyTrameD();
		proxyTrameD2.setType('D');
		proxyTrameD2.setTimestamp(timestamp);
		proxyTrameD2.setDeviceId(devPhysique.getListeDevicesLogiques().get(1).getIdLogique());
		proxyTrameD2.setValeurLue(temperature);
		listeTrames.add(proxyTrameD2);
		
		ProxyTrameD proxyTrameD3 = new ProxyTrameD();
		proxyTrameD3.setType('D');
		proxyTrameD3.setTimestamp(timestamp);
		proxyTrameD3.setDeviceId(devPhysique.getListeDevicesLogiques().get(2).getIdLogique());
		proxyTrameD3.setValeurLue(presence);
		listeTrames.add(proxyTrameD3);
		
		return listeTrames;
	}
	
	
	
	@Override
	public void run() {
		while(continuer){
			try {
				parseurThread.sleep(500);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.out.print("Parseur : dans le while");
			char[] trame;
			synchronized (listeTrames) {
				if(listeTrames.isEmpty()){
					try {
						listeTrames.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				trame = listeTrames.remove(0);
				listeTrames.notify();
			}
			ArrayList<ProxyTrame> listeProxyTrames = parseTrame(trame);
			if((listeProxyTrames != null) && (listeProxyTrames.size() != 0)){
				for(int i=0; i<listeProxyTrames.size(); i++) {
					// Mettre la trame dans la liste des trames à envoyer
					ClientEnvoiGHome.addProxyTrame(listeProxyTrames.get(i));
				}
			}
		}
		
	}

}
