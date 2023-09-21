package giuseppe.graziano.thermostat.model.data;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

@Entity
@Data
public class Program {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;
    private String description;
    private boolean active;
    private boolean repete;
    private DayOfWeek weekDay;
    private LocalTime startTime;
    private LocalTime endTime;
    private long sourceId;
    private boolean sourceOn;
    private float temperature;


    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_mode_id")
    private ProgramMode programMode;

}
