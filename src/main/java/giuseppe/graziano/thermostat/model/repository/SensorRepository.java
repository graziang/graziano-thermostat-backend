package giuseppe.graziano.thermostat.model.repository;


import giuseppe.graziano.thermostat.model.data.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SensorRepository extends JpaRepository<Sensor, Integer> {
    Sensor findSensorById(Long id);
    List<Sensor> findSensorsByThermostatId(Long id);

}