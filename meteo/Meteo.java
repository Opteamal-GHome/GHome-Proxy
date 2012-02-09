package meteo;
import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class Meteo {
	
	private URL url;
	private int temperature;
	
	public Meteo () {

		try {
			// On indique l'URL pour chercher le XML de la meteo : Lyon, France
			url = new URL("http://www.google.com/ig/api?weather=Lyon,France");
			
			
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			
			XMLReader xmlReader = saxParser.getXMLReader();
			
			// On applique la classe handler qui va etre parsee
			MeteoHandler meteoHandler = new MeteoHandler();
			xmlReader.setContentHandler(meteoHandler);
						
			// On ouvre l'URL qui correspond à la meteo en Java
			InputSource input = new InputSource(url.openStream());
			input.setEncoding("ISO-8859-1");
			xmlReader.parse(input);
			
			// On stocke le contenu parse
			ParsedMeteoDataSet parsedExampleDataSet = meteoHandler.getParsedData();
			
			this.temperature = parsedExampleDataSet.getTemperatureCelsius();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public int getTemperature() {
		return this.temperature;
	}

}
