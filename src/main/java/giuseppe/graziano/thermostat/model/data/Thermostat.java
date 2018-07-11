package giuseppe.graziano.thermostat.model.data;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Thermostat {

    @JsonIgnore
    public static String MANUAL_MODE = "manual_mode";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;

    private String description;

    private boolean active;

    private boolean stateOn;

    private Date lastMeasurement;

    private float temperature;

    private String mode;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manual_mode_id")
    private ManualMode manualMode;


    @OneToMany(mappedBy = "thermostat", cascade = CascadeType.ALL,  fetch = FetchType.LAZY)
    private Set<Sensor> sensors = new HashSet<>();


    public Thermostat() {
    }

    public Thermostat(String name, String description) {
        this.name = name;
        this.description = description;
        this.mode = Thermostat.MANUAL_MODE;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isStateOn() {
        return stateOn;
    }

    public void setStateOn(boolean stateOn) {
        this.stateOn = stateOn;
    }

    public Date getLastMeasurement() {
        return lastMeasurement;
    }

    public void setLastMeasurement(Date lastMeasurement) {
        this.lastMeasurement = lastMeasurement;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public Set<Sensor> getSensors() {
        return sensors;
    }

    public void setSensors(Set<Sensor> sensors) {
        this.sensors = sensors;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public ManualMode getManualMode() {
        return manualMode;
    }

    public void setManualMode(ManualMode manualMode) {
        this.manualMode = manualMode;
    }
}


