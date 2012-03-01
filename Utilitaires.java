import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class Utilitaires {

/*
 * Cette classe fournit des méthodes de conversion de type de donnée et 
 * des méthodes de manipulation des fichiers XML
 * 	
 */
	
	//cast de int vers tableau de byte
	public static byte[] intToByteArray(int data) {
		byte[] byteArray = new byte[4];
		int i = 0;
		int shift = 24;
		for (i = 0; i < 4; i++) {
			byteArray[i] = (byte) (data >> shift & 0xff);
			shift -= 8;
		}
		return byteArray;
	}

	//concaténation de deux tableaux de bytes
	public static byte[] concat(byte[] first, byte[] second) {
		byte[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}

	//cast de long vers tableau de byte
	public static byte[] toByteArray(long l) {
		return new byte[] { (byte) ((l >> 56) & 0xff),
				(byte) ((l >> 48) & 0xff), (byte) ((l >> 40) & 0xff),
				(byte) ((l >> 32) & 0xff), (byte) ((l >> 24) & 0xff),
				(byte) ((l >> 16) & 0xff), (byte) ((l >> 8) & 0xff),
				(byte) ((l >> 0) & 0xff), };
	}

	//cast de String vers String en format hexadécimal
	public static String stringToHexa(String texte) {
		int c;// int's equivalent to char
		char s = ' ';// separator
		// To safe memory - limite gc requests
		StringBuffer buff = new StringBuffer(texte.length());
		for (int i = 0; i < texte.length(); i++) {
			c = texte.charAt(i);
			buff.append(Integer.toHexString(c)).append(s);
		}
		return buff.toString();
	}

	//méthode de chargement d'un fichier XML
	public static Document loadConfiguration(String path) {

		// create a document factory
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = null;
		Document document = null;
		// create a document constructor
		try {
			docBuilder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File xml = new File(path);
		try {
			document = docBuilder.parse(xml);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return document;

	}

	// méthode de modification d'un fichier XML
	public static void transformerXml(Document document, String fichier) {
		try {
			// Création de la source DOM
			Source source = new DOMSource(document);

			// Création du fichier de sortie
			Result resultat = new StreamResult(fichier);

			// Configuration du transformer
			TransformerFactory fabrique = TransformerFactory.newInstance();
			Transformer transformer = fabrique.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "utf8");
			transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,
					Constantes.dtdFile);

			// Transformation
			transformer.transform(source, resultat);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
