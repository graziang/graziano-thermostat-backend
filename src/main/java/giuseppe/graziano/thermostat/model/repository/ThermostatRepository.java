package giuseppe.graziano.thermostat.model.repository;

import giuseppe.graziano.thermostat.model.data.Thermostat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThermostatRepository  extends JpaRepository<Thermostat, Integer> {

    Thermostat findThermostatById(Long id);

}