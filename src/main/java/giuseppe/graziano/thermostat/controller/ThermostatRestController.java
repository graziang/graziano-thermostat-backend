package giuseppe.graziano.thermostat.controller;



import giuseppe.graziano.thermostat.model.data.SensorStats;
import giuseppe.graziano.thermostat.model.data.Measurement;
import giuseppe.graziano.thermostat.model.data.Sensor;
import giuseppe.graziano.thermostat.model.data.Thermostat;
import giuseppe.graziano.thermostat.service.ThermostatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(value = "thermostat")
public class ThermostatRestController {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ThermostatRestController.class);

   /* private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ThermostatRestController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }*/


    @Autowired
    ThermostatService thermostatService;


    @PostMapping("/initialize")
    public Thermostat initializeThermostat(){
        return this.thermostatService.initialize();
    }

    @PostMapping("addSensor")
    public ResponseEntity<Object> addSensor(@RequestBody Sensor s, @RequestParam(value = "thermostat_id", required = true) Long id) {

        Sensor sensor = this.thermostatService.addSensor(id, s);

        if(sensor == null){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(sensor, HttpStatus.OK);
    }


    @GetMapping("thermostats")
    public ResponseEntity<Object> getThermostats(){
        return  new ResponseEntity<>(this.thermostatService.getThermostats(), HttpStatus.OK);
    }

    @GetMapping("sensors")
    public ResponseEntity<Object>  getSensors (@RequestParam(value = "thermostat_id", required = false) Long id){
        return  new ResponseEntity<>(this.thermostatService.getSensors(id), HttpStatus.OK);
    }

    @PostMapping("sensorState")
    public ResponseEntity<Object> postSensorState (@RequestParam(value = "sensor_id", required = true) Long id, @RequestParam(value = "state", required = true) boolean state){

        Sensor sensor = this.thermostatService.setSensorState(id, state);

        if(sensor == null){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(sensor, HttpStatus.OK);

    }

    @PostMapping("thermostatOnOff")
    public ResponseEntity<Object> postThermostatOnOff (@RequestParam(value = "thermostat_id", required = true) Long id, @RequestParam(value = "ok", required = true) boolean state){

        Thermostat thermostat = this.thermostatService.tournThermostatOnOff(id, state);

        if(thermostat == null){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(thermostat, HttpStatus.OK);

    }


    @GetMapping("lastMeasurements")
    public ResponseEntity<Object> getMeasurements(@RequestParam(value = "sensor_id", required = true) Long id) {

        Measurement measurement = this.thermostatService.getLastMeasurements(id);

        if(measurement == null){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(measurement, HttpStatus.OK);
    }


    @GetMapping("measurementsStats")
    public ResponseEntity<Object> getMeasurementsStats(@RequestParam(value = "dateStart", required = false) String dateStart, @RequestParam(value = "dateEnd", required = false) String dateEnd, @RequestParam(value = "sensor_id", required = true) Long id) {

        SensorStats sensorStats = this.thermostatService.getMeasurementsStats(dateStart, dateEnd, id);

        if(sensorStats == null){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(sensorStats, HttpStatus.OK);
    }

    @GetMapping("measurements")
    public ResponseEntity<List<Measurement>> getMeasurements (@RequestParam(value = "dateStart", required = false) String dateStart, @RequestParam(value = "dateEnd", required = false) String dateEnd) {

        List<Measurement> measurements = this.thermostatService.getMeasurements(dateStart, dateEnd);

        return new ResponseEntity<>(measurements, HttpStatus.OK);
    }


    @PostMapping( "measurements")
    public ResponseEntity<List<Measurement>> postMeasurement(@RequestBody Map<String, Float> measurement) {

        if(measurement == null){
            return new ResponseEntity<>((List<Measurement>) null, HttpStatus.BAD_REQUEST);
        }

        List<Measurement> measurements = this.thermostatService.addMeasurement(measurement);

        return new ResponseEntity<>(measurements, HttpStatus.OK);

    }

    @DeleteMapping("dropMeasurement")
    public  ResponseEntity<Object> deleteMeasurement(){

        return new ResponseEntity<>(this.thermostatService.cleanMeasurements(), HttpStatus.OK);
    }

    @DeleteMapping("/dropAll")
    public  ResponseEntity<Object> deleteAll(){

        return new ResponseEntity<>(this.thermostatService.cleanAllTable(), HttpStatus.OK);
    }


}
