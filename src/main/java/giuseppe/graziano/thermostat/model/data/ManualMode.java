package giuseppe.graziano.thermostat.model.data;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class ManualMode implements Serializable {

    public ManualMode(){
        this.sensorId = -1;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private boolean active;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thermostat_id")
    @JsonIgnore
    private Thermostat thermostat;

    private long sensorId;

    private boolean avg;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Thermostat getThermostat() {
        return thermostat;
    }

    public void setThermostat(Thermostat thermostat) {
        this.thermostat = thermostat;
    }

    public long getSensorId() {
        return sensorId;
    }

    public void setSensorId(long sensorId) {
        this.sensorId = sensorId;
    }

    public boolean isAvg() {
        return avg;
    }

    public void setAvg(boolean avg) {
        this.avg = avg;
    }
}
