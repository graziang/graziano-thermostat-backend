package giuseppe.graziano.thermostat.model.data;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
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

    public Set<Program> getPrograms() {
        return programs;
    }

    public void setPrograms(Set<Program> programs) {
        this.programs = programs;
    }
}
