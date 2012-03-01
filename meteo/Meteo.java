package meteo;

import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/*
 * Fournit les données (température et humidité) récupérées à partir de la source météo externe
 */
public class Meteo {

	private URL url;
	private int temperature;
	private int humidite;
	private String ville; // code postal de la ville

	public Meteo(int ville) {

		String cp = Integer.toString(ville); // Si le cp commence par un 0 il n'apparait pas dans le int donc on le rajoute
		if (cp.length() < 5) {
			cp = "0" + cp;
		}

		try {
			String stringURL = "http://www.google.com/ig/api?weather=" + cp
					+ ",France";
			// On indique l'URL pour chercher le XML de la meteo
			url = new URL(stringURL);

			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();

			XMLReader xmlReader = saxParser.getXMLReader();

			// On applique la classe handler qui va etre parsée
			MeteoHandler meteoHandler = new MeteoHandler();
			xmlReader.setContentHandler(meteoHandler);

			// On ouvre l'URL qui correspond à la meteo en Java
			InputSource input = new InputSource(url.openStream());
			input.setEncoding("ISO-8859-1");
			xmlReader.parse(input);

			// On stocke le contenu parsé
			ParsedMeteoDataSet parsedExampleDataSet = meteoHandler
					.getParsedData();

			this.temperature = parsedExampleDataSet.getTemperatureCelsius();
			this.humidite = parsedExampleDataSet.getHumidity();
			this.ville = parsedExampleDataSet.getVille();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int getTemperature() {
		return this.temperature;
	}

	public int getHumidite() {
		return this.humidite;
	}

	public String getVille() {
		return this.ville;
	}

}
