import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Classe permettant la gestion des différents capteurs/actionneurs enregistrés
 */
public class EnsembleDevices {
	public static HashMap<Long, DevicePhysique> mapDevicesPhysiques = new HashMap<Long, DevicePhysique>();
	public static HashMap<Integer, DeviceLogique> mapDevicesLogiques = new HashMap<Integer, DeviceLogique>();
	public static int nextIDLogique = 0;
	
	
	public static void ajouterDevice(long nouveauIDPysique, DevicePhysique nouveauDevicePhysique)
	{
		mapDevicesPhysiques.put(nouveauIDPysique, nouveauDevicePhysique);
		
		// Ajout des capteurs logiques correspondants
		List<DeviceLogique> v = nouveauDevicePhysique.getListeDevicesLogiques();
		for(int i=0; i<v.size(); i++){
			DeviceLogique dl = v.get(i);
			mapDevicesLogiques.put(dl.getIdLogique(), dl);
		}
		
		// Démarrer le timer du device
		nouveauDevicePhysique.demarrerTimer();
	}
	
	public static void supprimerDevice(int idLogique)
	{
		DeviceLogique dl = mapDevicesLogiques.get(idLogique);
		mapDevicesLogiques.remove(idLogique);
		
		DevicePhysique dp = dl.getDevicePhysique();
		List<DeviceLogique> vDL= dp.getListeDevicesLogiques();
		
		for (int i = 0; i < vDL.size(); i++)
		{
			mapDevicesLogiques.remove(vDL.get(i).getIdLogique());
		}
		mapDevicesPhysiques.remove(dp.getIdPhysique());	
	}
	
	public static DevicePhysique getDevicePhysiqueByID(long idPhysique) {
		return mapDevicesPhysiques.get(idPhysique);
	}

	
	public static DeviceLogique getDeviceLogiquebyID(int idLogique)
	{
		return mapDevicesLogiques.get(idLogique);
	}
	
	public  static int getNextIdLogique()
	{
		nextIDLogique++;
		return nextIDLogique;
	}
	
	
	public static void parseDeviceFile(String path)
	{
		String tag = "Dev";
		Element el = null;
		Element root = Utilitaires.loadConfiguration(path);
		if(root.getNodeName().equals("Devices"))
		{
			NodeList listOfDev = root.getElementsByTagName(tag);
			if(listOfDev != null && listOfDev.getLength() > 0)
			{
				int i = 0;
				int length = listOfDev.getLength();
				for(i = 0; i < length; i++)
				{
					 el = (Element) listOfDev.item(i);
					 addDeviceStatically((Long.parseLong(el.getAttribute("id"))),el.getAttribute("eep"));	 
				}
			}
		}
	}

	public static void addDeviceStatically (long id, String type)
	{
		if(type.equals("06-00-01")) //contact 
		{
			DevicePhysique contact = new DevicePhysique(id,Constantes.TYPE_P_CONTACT, null);
			DeviceLogique contactLogique = new DeviceLogique(EnsembleDevices.getNextIdLogique(), Constantes.TYPE_L_CONTACT, contact);
			List<DeviceLogique> listeDevLogContact = new ArrayList<DeviceLogique>();
			listeDevLogContact.add(contactLogique);
			contact.setListeDevicesLogiques(listeDevLogContact);
			EnsembleDevices.ajouterDevice(id, contact);
			
			System.out.println("adding a contact device");
			
		}
		else if (type.equals("")) //temperature
		{
			//TODO finish this 
			//System.out.println("adding a temperature device");
		}
		else if (type.equals("07-08-01")) //light, temperature and presence
		{
			 DevicePhysique presence = new DevicePhysique(id,Constantes.TYPE_P_PRESENCE, null);
			 List<DeviceLogique> listeDevLogPre = new ArrayList<DeviceLogique>();
			 
			 DeviceLogique lumLogique = new DeviceLogique(EnsembleDevices.getNextIdLogique(),Constantes.TYPE_L_LUMINOSITE, presence);
			 DeviceLogique tempLogique = new DeviceLogique(EnsembleDevices.getNextIdLogique(),Constantes.TYPE_L_TEMPERATURE, presence); 
			 DeviceLogique presenceLogique = new DeviceLogique(EnsembleDevices.getNextIdLogique(),Constantes.TYPE_L_PRESENCE, presence);
			 
			 listeDevLogPre.add(lumLogique);
			 listeDevLogPre.add(tempLogique);
			 listeDevLogPre.add(presenceLogique);
			 
			 presence.setListeDevicesLogiques(listeDevLogPre);
			 EnsembleDevices.ajouterDevice(id, presence);
			 
			 System.out.println("adding a LTO device");
			
		}
		else if (type.equals("05-02-01")) //switch
		{
			DevicePhysique interrupteur = new DevicePhysique(id, Constantes.TYPE_P_INTERRUPTEUR_4,null);
			List<DeviceLogique> listeDevLogInt = new ArrayList<DeviceLogique>();
			
			DeviceLogique interrupteurLogique = new DeviceLogique(EnsembleDevices.getNextIdLogique(), Constantes.TYPE_L_INTERRUPTEUR,interrupteur);
			listeDevLogInt.add(interrupteurLogique);
			interrupteur.setListeDevicesLogiques(listeDevLogInt);
			EnsembleDevices.ajouterDevice(id,interrupteur);
			
			System.out.println("adding a Rocker Switch device");
		}
		
		else if(type.equals("prise")) 
		{
			DevicePhysique prise = new DevicePhysique(id, Constantes.TYPE_P_PRISE, null);
			ArrayList<DeviceLogique> listeDevLogPrise = new ArrayList<DeviceLogique>();
			DeviceLogique priseLogique = new DeviceLogique(EnsembleDevices.getNextIdLogique(), Constantes.TYPE_L_ACTIONNEUR, prise);
			listeDevLogPrise.add(priseLogique);
			prise.setListeDevicesLogiques(listeDevLogPrise);
			EnsembleDevices.ajouterDevice(id, prise);
			
			System.out.println("adding a contact (prise) device");
		}
	}
}
