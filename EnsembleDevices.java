import java.util.HashMap;
import java.util.List;


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

}
