public class Constantes {

	// Adresses du fichier xml et de la dtd
	public final static String pathToDeviceFile = "/home/caucagne1/workspace/PLDreseaux10/GHomeProxy/src/capteurs.xml";
	public final static String dtdFile = "capteurs.dtd";

	public final static int TAILLE_TRAME_ENOCEAN = 28;
	
	// Adresse de la base des capteurs
	public final static String IP_BASE = "134.214.105.28";
	public final static int PORT_BASE = 5000;

	// Adresse du serveur GHome
	public final static String IP_GHOME = "134.214.59.214";
	public final static int PORT_GHOME = 443;
	// Pour le serveur simulé
	public final static int PORT_SERV_ENVOI = 8000;

	// Type de trame (envoyée au serveur)
	public final static char TYPE_STATUS = 'S';
	public final static char TYPE_DONNEES = 'D';

	// Type de trame (envoyée par serveur)
	public final static char TYPE_LANCEMENT = 'L';
	public final static char TYPE_FIN = 'F';
	public final static char TYPE_ORDRE = 'O';
	public final static char TYPE_UPDATE_VILLE = 'V';

	// Type de trame status
	public final static char TYPE_AJOUT = 'A';
	public final static char TYPE_RETRAIT = 'R'; // Capteur plus détecté

	// Type capteur logique
	public final static char TYPE_L_LUMINOSITE = 'L';
	public final static char TYPE_L_TEMPERATURE = 'T';
	public final static char TYPE_L_HUMIDITE = 'H';
	public final static char TYPE_L_PRESENCE = 'P';
	public final static char TYPE_L_CONTACT = 'C'; // Contact fenêtre ou porte fermée
	public final static char TYPE_L_INTERRUPTEUR = 'I';
	public final static char TYPE_L_ACTIONNEUR = 'E';
	public final static char TYPE_L_METEO_TEMP = 'U';
	public final static char TYPE_L_METEO_HUM = 'V';

	// Type physique des capteurs
	public final static String TYPE_P_INTERRUPTEUR_4 = "05-02-01";
	public final static String TYPE_P_PRISE = "prise"; // actionneur
	public final static String TYPE_P_PRESENCE = "07-08-01"; // light, temp & occupancy
	public final static String TYPE_P_CONTACT = "06-00-01";
	public final static String TYPE_P_TEMPERATURE = "07-02-05";
	public final static String TYPE_P_METEO = "meteo";

	// Correspondance Index dans le tableau de caractères / byte EnOcean
	public final static int INDEX_SYNC_BYTE_1_1 = 0;
	public final static int INDEX_SYNC_BYTE_1_2 = 1;
	public final static int INDEX_SYNC_BYTE_2_1 = 2;
	public final static int INDEX_SYNC_BYTE_2_2 = 3;
	public final static int HSEQ_LENGTH_1 = 4;
	public final static int HSEQ_LENGTH_2 = 5;
	public final static int ORG_1 = 6;
	public final static int ORG_2 = 7;
	public final static int DATA_BYTE_3_1 = 8;
	public final static int DATA_BYTE_3_2 = 9;
	public final static int DATA_BYTE_2_1 = 10;
	public final static int DATA_BYTE_2_2 = 11;
	public final static int DATA_BYTE_1_1 = 12;
	public final static int DATA_BYTE_1_2 = 13;
	public final static int DATA_BYTE_0_1 = 14;
	public final static int DATA_BYTE_0_2 = 15;
	public final static int ID_BYTE_3_1 = 16;
	public final static int ID_BYTE_3_2 = 17;
	public final static int ID_BYTE_2_1 = 18;
	public final static int ID_BYTE_2_2 = 19;
	public final static int ID_BYTE_1_1 = 20;
	public final static int ID_BYTE_1_2 = 21;
	public final static int ID_BYTE_0_1 = 22;
	public final static int ID_BYTE_0_2 = 23;
	public final static int STATUS_1 = 24;
	public final static int STATUS_2 = 25;
	public final static int CHECKSUM_1 = 26;
	public final static int CHECKSUM_2 = 27;

	// Temps au bout duquel l'alerte enlèvement est déclenchée (ou la météo
	// envoyée)
	public final static int DELAI_DECLENCHEMENT_TIMER = 60000;
	//public final static int DELAI_DECLENCHEMENT_TIMER = 9000000;

	// Code postal de la ville dont on regarde la meteo (variable globale plutôt
	// que constante)
	public static int VILLE = 69008;
}
