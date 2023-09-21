package giuseppe.graziano.thermostat.model.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.Hibernate;

import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Data
public class Thermostat {

    @JsonIgnore
    public static String MANUAL_MODE = "manual_mode";

    @JsonIgnore
    public static String PROGRAM_MODE = "program_mode";

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

    private boolean cold;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "manual_mode_id")
    private ManualMode manualMode;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "program_mode_id")
    private ProgramMode programMode;


    @OneToMany(mappedBy = "thermostat", cascade = CascadeType.ALL,  fetch = FetchType.LAZY)
    private Set<Sensor> sensors = new HashSet<>();

    @OneToMany(mappedBy = "thermostat", cascade = CascadeType.ALL,  fetch = FetchType.LAZY)
    private Set<Source> sources = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id")
    private Source source;

    public Thermostat() {
    }

    public Thermostat(String name, String description) {
        this.name = name;
        this.description = description;
        this.mode = Thermostat.MANUAL_MODE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Thermostat that = (Thermostat) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}


