package giuseppe.graziano.thermostat.model.data;

public class SensorStats {


    private String dateStart;

    private String dateEnd;

    private Sensor sensor;

    private Measurement maxMeasurement;

    private Measurement minMeasurement;


    private float maxTemperature;

    private float minTemperature;

    private float avgTemperature;





    public String getDateStart() {
        return dateStart;
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }

    public String getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    public Measurement getMaxMeasurement() {
        return maxMeasurement;
    }

    public void setMaxMeasurement(Measurement maxMeasurement) {
        this.maxMeasurement = maxMeasurement;
    }

    public Measurement getMinMeasurement() {
        return minMeasurement;
    }

    public void setMinMeasurement(Measurement minMeasurement) {
        this.minMeasurement = minMeasurement;
    }


    public float getMaxTemperature() {
        return maxTemperature;
    }

    public void setMaxTemperature(float maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    public float getMinTemperature() {
        return minTemperature;
    }

    public void setMinTemperature(float minTemperature) {
        this.minTemperature = minTemperature;
    }

    public float getAvgTemperature() {
        return avgTemperature;
    }

    public void setAvgTemperature(float avgTemperature) {
        this.avgTemperature = avgTemperature;
    }
}
