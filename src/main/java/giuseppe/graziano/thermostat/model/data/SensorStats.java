package giuseppe.graziano.thermostat.model.data;

import lombok.Data;

@Data
public class SensorStats {


    private String dateStart;

    private String dateEnd;

    private Sensor sensor;

    private Measurement maxMeasurement;

    private Measurement minMeasurement;


    private float maxTemperature;

    private float minTemperature;

    private float avgTemperature;
}
