package meteo;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/*
 * Permet de parser le code xml contenant les données de la météo
 */
public class MeteoHandler extends DefaultHandler {

	// ===========================================================
	// Fields
	// ===========================================================

	private boolean in_tempC = false;
	private boolean in_humidity = false;
	private boolean in_city = false;

	private ParsedMeteoDataSet meteoDataSet = new ParsedMeteoDataSet();

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public ParsedMeteoDataSet getParsedData() {
		return this.meteoDataSet;
	}

	// ===========================================================
	// Methods
	// ===========================================================
	@Override
	public void startDocument() throws SAXException {
		this.meteoDataSet = new ParsedMeteoDataSet();
	}

	@Override
	public void endDocument() throws SAXException {
		// Nothing to do
	}

	/**
	 * Gets be called on opening tags like: <tag> Can provide attribute(s), when
	 * xml was like: <tag attribute="attributeValue">
	 */
	@Override
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {
		if (qName.equals("temp_c")) {
			this.in_tempC = true;
			meteoDataSet.setTemperatureCelsius(Integer.parseInt(atts
					.getValue("data")));
		} else if (qName.equals("humidity")) {
			this.in_humidity = true;
			String[] strings = atts.getValue("data").split(" ");

			for (int i = 0; i < strings.length; i++) {
				System.out.println("Tableau strings " + i + " : " + strings[i]);
			}

			String stringHumidity = strings[1].split("%")[0];
			int humidity = Integer.parseInt(stringHumidity);
			meteoDataSet.setHumidity(humidity);
		} else if (qName.equals("city")) {
			this.in_city = true;
			meteoDataSet.setVille(atts.getValue("data"));
		}
	}

	/**
	 * Gets be called on closing tags like: </tag>
	 */
	@Override
	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {

		if (qName.equals("temp_c")) {
			this.in_tempC = false;
		} else if (qName.equals("humidity")) {
			this.in_humidity = false;
		} else if (qName.equals("city")) {
			this.in_city = false;
		}
	}

	/**
	 * Gets be called on the following structure: <tag>characters</tag>
	 */
	@Override
	public void characters(char ch[], int start, int length) {

	}
}