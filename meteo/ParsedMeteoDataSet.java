package meteo;

/*
 * Contient les informations parsées dans MeteoHandler
 */
public class ParsedMeteoDataSet {
	private int temperatureCelsius;
	private int humidity;
	private String ville;

	public int getTemperatureCelsius() {
		return temperatureCelsius;
	}

	public void setTemperatureCelsius(int temperatureCelsius) {
		this.temperatureCelsius = temperatureCelsius;
	}

	public void setHumidity(int humidity) {
		this.humidity = humidity;
	}

	public int getHumidity() {
		return humidity;
	}

	@Override
	public String toString() {
		return "ParsedMeteoDataSet [temperatureCelsius=" + temperatureCelsius
				+ "]";
	}

	public void setVille(String ville) {
		this.ville = ville;
	}

	public String getVille() {
		return ville;
	}

}