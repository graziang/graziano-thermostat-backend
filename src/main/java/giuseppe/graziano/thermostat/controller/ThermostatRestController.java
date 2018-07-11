package giuseppe.graziano.thermostat.controller;



import giuseppe.graziano.thermostat.exception.NotFoundException;
import giuseppe.graziano.thermostat.model.data.SensorStats;
import giuseppe.graziano.thermostat.model.data.Measurement;
import giuseppe.graziano.thermostat.model.data.Sensor;
import giuseppe.graziano.thermostat.model.data.Thermostat;
import giuseppe.graziano.thermostat.service.ThermostatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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


    @PostMapping("initialize")
    public Thermostat initializeThermostat(){
        return this.thermostatService.initialize();
    }

    @PostMapping("sensor")
    public ResponseEntity<Object> addSensor(@RequestBody Sensor s, @RequestParam(value = "thermostat_id") Long id) {

        try {
            Sensor sensor = this.thermostatService.addSensor(id, s);
            return new ResponseEntity<>(sensor, HttpStatus.OK);
        }
        catch (NotFoundException e){
            return getError(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }


    @GetMapping("thermostats")
    public ResponseEntity<Object> getThermostats(){
        return  new ResponseEntity<>(this.thermostatService.getThermostats(), HttpStatus.OK);
    }

    @GetMapping("thermostat")
    public ResponseEntity<Object> getThermostat(@RequestParam(value = "thermostat_id") Long id){
        try {
            Thermostat thermostat = this.thermostatService.getThermostat(id);
            return new ResponseEntity<>(thermostat, HttpStatus.OK);
        }
        catch (NotFoundException e){
            return getError(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("sensors")
    public ResponseEntity<Object>  getSensors (@RequestParam(value = "thermostat_id", required = false) Long id){
        return  new ResponseEntity<>(this.thermostatService.getSensors(id), HttpStatus.OK);
    }

    @PostMapping("sensor_state")
    public ResponseEntity<Object> postSensorState (@RequestParam(value = "sensor_id") Long id, @RequestParam(value = "state") boolean state){

        try {
            Sensor sensor = this.thermostatService.setSensorState(id, state);
            return new ResponseEntity<>(sensor, HttpStatus.OK);
        }
        catch (NotFoundException e){
            return getError(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    @PostMapping("thermostat_on")
    public ResponseEntity<Object> postThermostatOnOff (@RequestParam(value = "thermostat_id") Long id, @RequestParam(value = "on") boolean state){


        try {
            Thermostat thermostat = this.thermostatService.turnThermostatOnOff(id, state);
            return new ResponseEntity<>(thermostat, HttpStatus.OK);
        }
        catch (NotFoundException e){
            return getError(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("thermostat_on")
    public ResponseEntity<Object> getThermostatOnOff (@RequestParam(value = "thermostat_id") Long id){


        try {
            Thermostat thermostat = this.thermostatService.getThermostat(id);
            return new ResponseEntity<>(thermostat.isStateOn(), HttpStatus.OK);
        }
        catch (NotFoundException e){
            return getError(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("thermostat_temperature")
    public ResponseEntity<Object> postThermostatTemperature(@RequestParam(value = "thermostat_id") Long id, @RequestParam(value = "temperature") float temperature){


        try {
            Thermostat thermostat = this.thermostatService.setThermostatTemperature(id, temperature);
            return new ResponseEntity<>(thermostat, HttpStatus.OK);
        }
        catch (NotFoundException e){
            return getError(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("thermostat_mode")
    public ResponseEntity<Object> postThermostatMode (@RequestParam(value = "thermostat_id") Long id, @RequestParam(value = "mode") String mode){


        try {
            Thermostat thermostat = this.thermostatService.setThermostatMode(id, mode);
            return new ResponseEntity<>(thermostat, HttpStatus.OK);
        }
        catch (NotFoundException e){
            return getError(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("thermostat_mode/manual")
    public ResponseEntity<Object> postThermostatModeManualCalculateSensor(@RequestParam(value = "thermostat_id") Long id, @RequestParam(value = "avg", required = false) boolean avg, @RequestParam(value = "mode") Long sensorId){


        try {
            Thermostat thermostat = this.thermostatService.setThermostatCalulateSensor(id, avg, sensorId);
            return new ResponseEntity<>(thermostat, HttpStatus.OK);
        }
        catch (NotFoundException e){
            return getError(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("last_measurements")
    public ResponseEntity<Object> getMeasurements(@RequestParam(value = "sensor_id") Long id) {

        try {
            Measurement measurement = this.thermostatService.getLastMeasurements(id);
            return new ResponseEntity<>(measurement, HttpStatus.OK);
        } catch (NotFoundException e) {
            return getError(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("measurements_stats")
    public ResponseEntity<Object> getMeasurementsStats(@RequestParam(value = "date_start", required = false) String dateStart, @RequestParam(value = "date_end", required = false) String dateEnd, @RequestParam(value = "sensor_id") Long id) {

        SensorStats sensorStats = this.thermostatService.getMeasurementsStats(dateStart, dateEnd, id);

        if(sensorStats == null){
            getError("", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(sensorStats, HttpStatus.OK);
    }

    @GetMapping("measurements")
    public ResponseEntity<List<Measurement>> getMeasurements(@RequestParam(value = "date_start", required = false) String dateStart, @RequestParam(value = "date_end", required = false) String dateEnd) {

        List<Measurement> measurements = this.thermostatService.getMeasurements(dateStart, dateEnd);

        return new ResponseEntity<>(measurements, HttpStatus.OK);
    }


    @PostMapping( "measurements")
    public ResponseEntity<List<Measurement>> postMeasurement(@RequestBody Map<String, Float> measurement) {

        List<Measurement> measurements = this.thermostatService.addMeasurement(measurement);

        return new ResponseEntity<>(measurements, HttpStatus.OK);

    }

    @DeleteMapping("drop_measurement")
    public  ResponseEntity<Object> deleteMeasurement(){

        return new ResponseEntity<>(this.thermostatService.cleanMeasurements(), HttpStatus.OK);
    }

    @DeleteMapping("/drop_all")
    public  ResponseEntity<Object> deleteAll(){

        return new ResponseEntity<>(this.thermostatService.cleanAllTable(), HttpStatus.OK);
    }

    public ResponseEntity getError(String error, HttpStatus status){

        HttpHeaders responseHeaders = new HttpHeaders();
        //responseHeaders.set("classname", className);

        Map responsError = new HashMap();
        responsError.put("error", error);
        return new ResponseEntity<>(responsError, responseHeaders, status);
    }


}
