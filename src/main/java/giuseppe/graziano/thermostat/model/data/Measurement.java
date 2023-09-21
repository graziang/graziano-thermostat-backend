package giuseppe.graziano.thermostat.model.data;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@Entity
public class Measurement implements Serializable {

    private static final long serialVersionUID = -3009157732242241606L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    private float temperature;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor_id")
    private Sensor sensor;

    public Measurement() {
    }

    public Measurement(Sensor sensor, float temperature) {
        this.temperature = temperature;
        this.sensor = sensor;
    }
}
