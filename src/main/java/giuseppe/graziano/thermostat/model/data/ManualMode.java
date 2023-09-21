package giuseppe.graziano.thermostat.model.data;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Entity
@Data
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

    private long sourceId;

    private long sensorId;

    private boolean avg;

}
