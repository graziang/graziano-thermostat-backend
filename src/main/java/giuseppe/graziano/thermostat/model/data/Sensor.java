package giuseppe.graziano.thermostat.model.data;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Sensor {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;

    private String description;

    private boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thermostat_id")
    @JsonIgnore
    private Thermostat thermostat;

    @OneToMany(mappedBy = "sensor", cascade = CascadeType.ALL,  fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Measurement> measurements = new HashSet<>();

    public Sensor() {
    }

    public Sensor(String name, String description) {
        this.name = name;
        this.description = description;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Thermostat getThermostat() {
        return thermostat;
    }

    public void setThermostat(Thermostat thermostat) {
        this.thermostat = thermostat;
    }

    public Set<Measurement> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(Set<Measurement> measurements) {
        this.measurements = measurements;
    }
}