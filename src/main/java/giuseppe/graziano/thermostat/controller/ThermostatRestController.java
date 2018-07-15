package giuseppe.graziano.thermostat.controller;



import giuseppe.graziano.thermostat.exception.NotFoundException;
import giuseppe.graziano.thermostat.model.data.*;
import giuseppe.graziano.thermostat.service.ThermostatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
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


    @GetMapping("test")
    public ResponseEntity<Object> test(Principal user){
        return new ResponseEntity<>("ciao", HttpStatus.OK);
    }


    @PostMapping("user/create")
    public ResponseEntity<Object> createUser(@RequestBody User user){
        try {
            user = this.thermostatService.createUser(user);
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
        catch (Exception e){
            return getError(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("user/thermostat")
    public ResponseEntity<Object> createUser( @RequestParam(value = "thermostat_id") Long id,  @RequestParam(value = "username") String username){
        try {
            User user = this.thermostatService.addThermostatToUser(username, id);
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
        catch (Exception e){
            return getError(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("initialize")
    public Thermostat initializeThermostat(){
        return this.thermostatService.initialize();
    }

    @PreAuthorize("hasAuthority(#id)")
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

    @PreAuthorize("hasAuthority(#id)")
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

    @PostMapping("thermostat/state")
    public ResponseEntity<Object> setThermostatState(@RequestParam(value = "thermostat_id") Long id, @RequestParam(value = "state") boolean state){
        try {
            Thermostat thermostat = this.thermostatService.setThermostatState(id, state);
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

    @PostMapping("sensor/state")
    public ResponseEntity<Object> postSensorState (@RequestParam(value = "sensor_id") Long id, @RequestParam(value = "state") boolean state){

        try {
            Sensor sensor = this.thermostatService.setSensorState(id, state);
            return new ResponseEntity<>(sensor, HttpStatus.OK);
        }
        catch (NotFoundException e){
            return getError(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    @PostMapping("thermostat/on")
    public ResponseEntity<Object> postThermostatOnOff (@RequestParam(value = "thermostat_id") Long id, @RequestParam(value = "on") boolean state){


        try {
            Thermostat thermostat = this.thermostatService.turnThermostatOnOff(id, state);
            return new ResponseEntity<>(thermostat, HttpStatus.OK);
        }
        catch (NotFoundException e){
            return getError(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("thermostat/on")
    public ResponseEntity<Object> getThermostatOnOff (@RequestParam(value = "thermostat_id") Long id){


        try {
            Thermostat thermostat = this.thermostatService.getThermostat(id);
            return new ResponseEntity<>(thermostat.isStateOn(), HttpStatus.OK);
        }
        catch (NotFoundException e){
            return getError(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("thermostat/temperature")
    public ResponseEntity<Object> postThermostatTemperature(@RequestParam(value = "thermostat_id") Long id, @RequestParam(value = "temperature") float temperature){


        try {
            Thermostat thermostat = this.thermostatService.setThermostatTemperature(id, temperature);
            return new ResponseEntity<>(thermostat, HttpStatus.OK);
        }
        catch (NotFoundException e){
            return getError(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("thermostat/mode")
    public ResponseEntity<Object> postThermostatMode (@RequestParam(value = "thermostat_id") Long id, @RequestParam(value = "mode") String mode){


        try {
            Thermostat thermostat = this.thermostatService.setThermostatMode(id, mode);
            return new ResponseEntity<>(thermostat, HttpStatus.OK);
        }
        catch (NotFoundException e){
            return getError(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("thermostat/mode/manual")
    public ResponseEntity<Object> postThermostatModeManualCalculateSensor(@RequestParam(value = "thermostat_id") Long id, @RequestParam(value = "avg", required = false) boolean avg, @RequestParam(value = "sensor_id") Long sensorId){


        try {
            Thermostat thermostat = this.thermostatService.setThermostatCalulateSensor(id, avg, sensorId);
            return new ResponseEntity<>(thermostat, HttpStatus.OK);
        }
        catch (NotFoundException e){
            return getError(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("measurements/last")
    public ResponseEntity<Object> getMeasurements(@RequestParam(value = "thermostat_id") Long id) {

        try {
            List<Measurement> measurements = this.thermostatService.getLastMeasurements(id);
            return new ResponseEntity<>(measurements, HttpStatus.OK);
        } catch (NotFoundException e) {
            return getError(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("measurements/stats")
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


    @PostMapping("measurements")
    public ResponseEntity<List<Measurement>> postMeasurement(@RequestParam(value = "thermostat_id") Long id, @RequestBody Map<String, Float> measurement) {

        try {
            List<Measurement> measurements = this.thermostatService.addMeasurement(id, measurement);
            return new ResponseEntity<>(measurements, HttpStatus.OK);
        }
        catch (NotFoundException e){
            return getError(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("measurements/drop")
    public  ResponseEntity<Object> deleteMeasurement(){

        return new ResponseEntity<>(this.thermostatService.cleanMeasurements(), HttpStatus.OK);
    }

    @DeleteMapping("drop_all")
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
