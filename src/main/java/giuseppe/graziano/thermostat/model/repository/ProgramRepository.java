package giuseppe.graziano.thermostat.model.repository;


import giuseppe.graziano.thermostat.model.data.Program;
import giuseppe.graziano.thermostat.model.data.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProgramRepository extends JpaRepository<Program, Integer> {


}