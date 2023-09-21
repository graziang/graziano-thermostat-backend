package giuseppe.graziano.thermostat.model.data;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class ProgramMode {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private boolean active;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thermostat_id")
    @JsonIgnore
    private Thermostat thermostat;

    @OneToMany(mappedBy = "programMode", cascade = CascadeType.ALL,  fetch = FetchType.EAGER)
    private Set<Program> programs = new HashSet<>();


}
