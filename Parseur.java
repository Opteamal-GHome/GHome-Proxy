import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Thread récupérant les tâches reçues, les parsant, puis les envoyant au serveur GHome
 *
 */
public class Parseur implements Runnable {


	private static List<char[]> listeTrames;
	private Thread parseurThread = null;
	public static boolean continuer = true;

	public Parseur()
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

	public List<ProxyTrame> parseTrame(char[] trame) {

		System.out.println("Parseur : dans parseTrame");

		int dataByte0_1 = Integer.parseInt((""+trame[Constantes.DATA_BYTE_0_1]), 16);
		int dataByte0_2 = Integer.parseInt((""+trame[Constantes.DATA_BYTE_0_2]), 16);

		long id = parseID(trame);

		List<ProxyTrame> listeProxyTrames = new ArrayList<ProxyTrame>();
		long timestamp = System.currentTimeMillis();

		// teach-in frame and unknown device dataByte0.bit3 = 0; dataByte0.bit7 = 1
		if (EnsembleDevices.getDevicePhysiqueByID(id) == null && (((dataByte0_1 & 8) == 1) && ((dataByte0_2 & 8) == 0)))
		{
			System.out.println("---------Parseur : trame TEACH IN reconnue---------");
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
			System.out.println("Parseur : dans parseTrameData");
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
		System.out.println("---------In parse FUNC&TYPE; func = "+funcType[0]+" type = "+funcType[1]+"-------");
		
		return funcType;

	}

	public static List<ProxyTrame> parseTeachIn (long id, int org, int func, int type, long timestamp)
	{
		System.out.println("Parseur : dans PARSE-TEACH-IN");
		List<ProxyTrame> listeTramesS = new ArrayList<ProxyTrame>();

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


	public static List<ProxyTrame> TeachInTemp40 (long id, long timestamp)
	{
		System.out.println("########### Parseur : dans TeachInTemp40 ############ id = "+id);
		List<ProxyTrame> listeTramesS = new ArrayList<ProxyTrame>();
		List<DeviceLogique> listeDevLog = new ArrayList<DeviceLogique>();
		DevicePhysique devPhysique = new DevicePhysique(id, "07-02-05", listeDevLog);
		DeviceLogique tempDevice = new DeviceLogique(EnsembleDevices.getNextIdLogique(), 'T', devPhysique);
		listeDevLog.add(tempDevice);

		EnsembleDevices.ajouterDevice(id, devPhysique);

		ProxyTrameS proxyTrame1 = new ProxyTrameS(timestamp, 'S', 'A', tempDevice.getIdLogique(), 'T');
		listeTramesS.add(proxyTrame1);

		return listeTramesS;
	}


	public static List<ProxyTrame> TeachInLTO (long id, long timestamp)
	{
		System.out.println("@@@@@@@@@@@@@@ Parseur : dans TeachInLTO @@@@@@@@@@@@@ id= " + id);
		List<ProxyTrame> listeTramesS = new ArrayList<ProxyTrame>();
		List<DeviceLogique> listeDevLog = new ArrayList<DeviceLogique>();
		DevicePhysique devPhysique = new DevicePhysique(id, "07-08-01", listeDevLog);
		//todo voltage ?
		DeviceLogique lightDevice = new DeviceLogique(EnsembleDevices.getNextIdLogique(), Constantes.TYPE_L_LUMINOSITE, devPhysique);
		DeviceLogique tempDevice = new DeviceLogique(EnsembleDevices.getNextIdLogique(), Constantes.TYPE_L_TEMPERATURE, devPhysique);
		DeviceLogique presDevice = new DeviceLogique(EnsembleDevices.getNextIdLogique(), Constantes.TYPE_L_PRESENCE, devPhysique);

		//l'ordre dans la liste des devices logiques est importante; liste[0]=lightDev etc
		listeDevLog.add(lightDevice);
		listeDevLog.add(tempDevice);
		listeDevLog.add(presDevice);
		EnsembleDevices.ajouterDevice(id, devPhysique);

		ProxyTrameS proxyTrame1 = new ProxyTrameS(timestamp, Constantes.TYPE_STATUS, Constantes.TYPE_AJOUT, lightDevice.getIdLogique(), Constantes.TYPE_L_LUMINOSITE);
		listeTramesS.add(proxyTrame1);

		ProxyTrameS proxyTrame2 = new ProxyTrameS(timestamp, Constantes.TYPE_STATUS, Constantes.TYPE_AJOUT, tempDevice.getIdLogique(), Constantes.TYPE_L_TEMPERATURE);
		listeTramesS.add(proxyTrame2);

		ProxyTrameS proxyTrame3 = new ProxyTrameS(timestamp, Constantes.TYPE_STATUS, Constantes.TYPE_AJOUT, presDevice.getIdLogique(), Constantes.TYPE_L_PRESENCE);
		listeTramesS.add(proxyTrame3);

		return listeTramesS;
	}


	public static List<ProxyTrame> parseTrameD(long idPhysique, char[] trame, long timestamp) {

		System.out.println("Parseur : dans parseTrameD");

		List<ProxyTrame> listeProxyTrames = new ArrayList<ProxyTrame>();

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
			System.out.println("dans parse contact");
			listeProxyTrames = parseTypeContact(trame, devPhysique, timestamp);
		}

		else if (typePhysique == "05-02-01")
		{
			listeProxyTrames = parseTypeInterrupteur(trame, devPhysique, timestamp);
		}
		else if(typePhysique == "07-08-01") //Light, Temp and Occupancy
		{
			listeProxyTrames = parseTypeLTO(trame, devPhysique, timestamp);
		}

		else if(typePhysique == "07-02-05") //Temperature sensor
		{
			listeProxyTrames = parseTypeTemperature(trame, devPhysique, timestamp);
		}

		return listeProxyTrames;
	}

	//1 capteur logique correspondant 
	public static List<ProxyTrame> parseTypeTemperature (char[] trame, DevicePhysique devPhysique, long timestamp)
	{
		System.out.println("Parseur : dans parseTypeTemperature");

		List<ProxyTrame> listeTrames = new ArrayList<ProxyTrame>();
		DeviceLogique devLog = devPhysique.getListeDevicesLogiques().get(0);
		ProxyTrameD proxyTrameD = new ProxyTrameD(timestamp, Constantes.TYPE_DONNEES, devLog.getIdLogique()); 
		
		int temperatureTh = Integer.parseInt(""+trame[Constantes.DATA_BYTE_1_1]+trame[Constantes.DATA_BYTE_1_2], 16);
		int temperatureReelle = temperatureTh*40/255;
		proxyTrameD.setValeurLue(temperatureReelle);
		listeTrames.add(proxyTrameD);
		return listeTrames;
	}
	
	//1 Interrupteur physique = 1 capteur logique
	public static List<ProxyTrame> parseTypeInterrupteur (char[] trame, DevicePhysique devPhysique, long timestamp)
	{
		System.out.println("Parseur : dans parseTypeInterrupteur");

		List<ProxyTrame> listeTrames = new ArrayList<ProxyTrame>();
		DeviceLogique devLog = devPhysique.getListeDevicesLogiques().get(0);

		ProxyTrameD proxyTrameD1 = new ProxyTrameD(timestamp, Constantes.TYPE_DONNEES, devLog.getIdLogique()); 

		int status1 = Integer.parseInt(""+trame[Constantes.STATUS_1],16);
		int dataByte3_1 = Integer.parseInt(""+trame[Constantes.DATA_BYTE_3_1], 16);

		//NU = 1; one or two buttons pressed
		if((status1 & 1) == 1)
		{
			boolean pressed = false;
			//action on first Button
			if ((dataByte3_1 & 1) == 1)
			{
				pressed = true;
				System.out.println("parseur: button PRESSED !");
			}
			else{
				pressed = false;
				System.out.println("parseur: button RELEASED !");
			}	

			if (dataByte3_1 == 0 || dataByte3_1 == 1) //channel A1
			{		
				if(pressed){
					proxyTrameD1.setValeurLue(30);
				}
				else{
					proxyTrameD1.setValeurLue(31);
				}
			}
			else if ((dataByte3_1 & 14) == 2) //channel A0
			{
				if(pressed){
					proxyTrameD1.setValeurLue(10);
				}
				else{
					proxyTrameD1.setValeurLue(11);
				}		
			}
			else if ((dataByte3_1 & 14) == 4) //channel B1
			{
				if(pressed){
					proxyTrameD1.setValeurLue(40);
				}	
				else{
					proxyTrameD1.setValeurLue(41);
				}			
			}
			else //channel B0
			{
				if(pressed){
					proxyTrameD1.setValeurLue(20);
				}	
				else{
					proxyTrameD1.setValeurLue(21);
				}		
			}		
			listeTrames.add(proxyTrameD1);

			//secondAction
			int dataByte3_2 = Integer.parseInt(""+trame[Constantes.DATA_BYTE_3_2],16);
			if((dataByte3_2 & 1) == 1) //there's a second action
			{

				ProxyTrameD proxyTrameD2 = new ProxyTrameD(timestamp, 'D', devLog.getIdLogique());

				if (dataByte3_2 == 0 || dataByte3_2 == 1) //channel A1
				{
					if(pressed){
						proxyTrameD1.setValeurLue(30);
					}	
					else{
						proxyTrameD1.setValeurLue(31);
					}		
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
				ProxyTrameD proxyTrameErr1 = new ProxyTrameD(timestamp, Constantes.TYPE_DONNEES, devLog.getIdLogique());
				proxyTrameErr1.setValeurLue(-1);
				listeTrames.add(proxyTrameErr1);
			}
		}
		return listeTrames;
	}

	public static List<ProxyTrame> parseTypeContact(char[] trame, DevicePhysique devPhysique, long timestamp)
	{
		System.out.println("Parseur : dans parseTypeContact");
		List<ProxyTrame> listeTrames = new ArrayList<ProxyTrame>();

		int dataByte0_2 = Integer.parseInt(""+trame[Constantes.DATA_BYTE_0_2],16);

		DeviceLogique devLog = devPhysique.getListeDevicesLogiques().get(0);
		ProxyTrameD proxyTrameD = new ProxyTrameD(timestamp, Constantes.TYPE_DONNEES, devLog.getIdLogique());
		int val = dataByte0_2 & 1;
		System.out.println("état du contact "+ val);
		System.out.println("**************************dataByte3_2 "+dataByte0_2+"byte 3-2 = "+ trame[Constantes.DATA_BYTE_3_2]);
		proxyTrameD.setValeurLue(dataByte0_2 & 1);

		listeTrames.add(proxyTrameD);
		return listeTrames;
	}	


	public static long parseID(char[] trame){
		long idPhysique = Integer.parseInt(("" + trame[Constantes.ID_BYTE_3_1] + trame[Constantes.ID_BYTE_3_2] + 
				trame[Constantes.ID_BYTE_2_1] + trame[Constantes.ID_BYTE_2_2] + trame[Constantes.ID_BYTE_1_1] + 
				trame[Constantes.ID_BYTE_1_2] + trame[Constantes.ID_BYTE_0_1] + trame[Constantes.ID_BYTE_0_2]), 16);
		return idPhysique;
	}

	public static List<ProxyTrame> parseTypeLTO (char[] trame, DevicePhysique devPhysique, long timestamp)
	{
		System.out.println("Parseur : dans parseTypeLTO");
		List<ProxyTrame> listeTrames = new ArrayList<ProxyTrame>();

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

		ProxyTrameD proxyTrameD1 = new ProxyTrameD(timestamp, Constantes.TYPE_DONNEES, devPhysique.getListeDevicesLogiques().get(0).getIdLogique());
		proxyTrameD1.setValeurLue(luminosite);
		listeTrames.add(proxyTrameD1);

		ProxyTrameD proxyTrameD2 = new ProxyTrameD(timestamp, Constantes.TYPE_DONNEES, devPhysique.getListeDevicesLogiques().get(1).getIdLogique());
		proxyTrameD2.setValeurLue(temperature);
		listeTrames.add(proxyTrameD2);

		ProxyTrameD proxyTrameD3 = new ProxyTrameD(timestamp, Constantes.TYPE_DONNEES, devPhysique.getListeDevicesLogiques().get(2).getIdLogique());
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
						System.out.print("Entree");
						listeTrames.wait();
						System.out.print("Sortie");
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				trame = listeTrames.remove(0);

				listeTrames.notify();
			}
			System.out.println("before parseTrame");
			List<ProxyTrame> listeProxyTrames = parseTrame(trame);
			if((listeProxyTrames != null) && (listeProxyTrames.size() != 0)){
				System.out.println("adding frame to clientEnvoiGHome");
				for(int i=0; i<listeProxyTrames.size(); i++) {
					// Mettre la trame dans la liste des trames à envoyer
					ClientEnvoiGHome.addProxyTrame(listeProxyTrames.get(i));
				}
			}
		}

	}

}
