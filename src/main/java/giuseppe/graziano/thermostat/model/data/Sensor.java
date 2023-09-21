package giuseppe.graziano.thermostat.model.data;


import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Data
public class Sensor implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String deviceId;

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

}