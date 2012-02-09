package meteo;
public class ParsedMeteoDataSet {
    private int temperatureCelsius;

	public int getTemperatureCelsius() {
		return temperatureCelsius;
	}

	public void setTemperatureCelsius(int temperatureCelsius) {
		this.temperatureCelsius = temperatureCelsius;
	}
	
	@Override
	public String toString() {
		return "ParsedMeteoDataSet [temperatureCelsius=" + temperatureCelsius + "]";
	}
    
}