import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.JFileChooser;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class Utilitaires {
	
	public static byte[] intToByteArray(int data)
	{
		byte[] byteArray = new byte[4];
		int i = 0;
		int shift = 24;
		for(i = 0; i < 4; i++)
		{
			byteArray[i] = (byte)(data >> shift & 0xff); 
			shift -= 8;
		}
		return byteArray;
	}
	
	public static byte[] concat(byte[] first, byte[] second) {
		byte[] result = Arrays.copyOf(first, first.length + second.length);
		  System.arraycopy(second, 0, result, first.length, second.length);
		  return result;
		}
	
	public static byte[] toByteArray(long l) {
	     return new byte[] { 
	        (byte)((l >> 56) & 0xff),
	         (byte)((l >> 48) & 0xff),
	         (byte)((l >> 40) & 0xff),
	         (byte)((l >> 32) & 0xff),
	         (byte)((l >> 24) & 0xff),
	         (byte)((l >> 16) & 0xff),
	         (byte)((l >> 8) & 0xff),
	         (byte)((l >> 0) & 0xff),
	     };
	 }
	
	public static String stringToHexa(String texte) { 
        int c;//int's equivalent to char 
        char s=' ';//separator 
        //To safe memory - limite gc requests 
        StringBuffer buff = new StringBuffer(texte.length()); 
        for (int i = 0; i < texte.length(); i++) { 
            c=texte.charAt(i); 
            buff.append(Integer.toHexString(c)).append(s); 
        } 
        return buff.toString(); 
    }

    public static Element loadConfiguration(String path) 
    {
    	
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
			  return document.getDocumentElement();
      
    }
	
	
	
	
}
