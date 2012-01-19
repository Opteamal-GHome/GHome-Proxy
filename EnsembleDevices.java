import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;


/**
 * Classe permettant la gestion des différents capteurs/actionneurs enregistrés
 */
public class EnsembleDevices {
	public static HashMap<Integer, DevicePhysique> mapDevicesPhysiques = new HashMap<Integer, DevicePhysique>();
	public static HashMap<Integer, DeviceLogique> mapDevicesLogiques = new HashMap<Integer, DeviceLogique>();
	public static int nextIDLogique = -1;
	
	
	public static void ajouterDevice(Integer nouveauIDPysique, DevicePhysique nouveauDevicePhysique)
	{
		mapDevicesPhysiques.put(nouveauIDPysique, nouveauDevicePhysique);
		
		// Ajout des capteurs logiques correspondants
		ArrayList<DeviceLogique> v = nouveauDevicePhysique.getListeDevicesLogiques();
		for(int i=0; i<v.size(); i++){
			DeviceLogique dl = v.get(i);
			mapDevicesLogiques.put(new Integer(dl.getIdLogique()), dl);
		}
	}
	
	public static void supprimerDevice(int idLogique)
	{
		DeviceLogique dl = mapDevicesLogiques.get(new Integer(idLogique));
		mapDevicesLogiques.remove(new Integer(idLogique));
		
		DevicePhysique dp = dl.getDevicePhysique();
		ArrayList<DeviceLogique> vDL= dp.getListeDevicesLogiques();
		
		for (int i = 0; i < vDL.size(); i++)
		{
			mapDevicesLogiques.remove(new Integer(vDL.get(i).getIdLogique()));
		}
		mapDevicesPhysiques.remove(new Integer(dp.getIdPhysique()));	
	}

	public  static int getNextIdLogique()
	{
		nextIDLogique++;
		return nextIDLogique;
	}

}
