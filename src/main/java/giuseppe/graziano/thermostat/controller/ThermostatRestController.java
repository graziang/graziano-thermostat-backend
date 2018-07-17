package giuseppe.graziano.thermostat.controller;



import giuseppe.graziano.thermostat.exception.NotFoundException;
import giuseppe.graziano.thermostat.model.data.*;
import giuseppe.graziano.thermostat.security.MyUserPrincipal;
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


    @GetMapping("test/login")
    public ResponseEntity<Object> test(Principal user){
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
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

    @PreAuthorize("hasRole('ADMIN')")
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

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("initialize")
    public Thermostat initializeThermostat(){
        return this.thermostatService.initialize();
    }

    @PreAuthorize("hasRole('ADMIN')")
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
    public ResponseEntity<Object> getThermostats(Principal principal){
        return  new ResponseEntity<>(this.thermostatService.getThermostatsByUser(principal.getName()), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority(#id)")
    @PostMapping("thermostat/select")
    public ResponseEntity<Object> setUserThermostatSelected(Principal principal, @RequestParam(value = "thermostat_id") Long id) {
        try {
            User user = this.thermostatService.selectUserThermostat(principal.getName(), id);
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
        catch (NotFoundException e){
            return getError(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        
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

    @PreAuthorize("hasAuthority(#id)")
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

    @PreAuthorize("hasAuthority(#id)")
    @GetMapping("sensors")
    public ResponseEntity<Object>  getSensors (@RequestParam(value = "thermostat_id", required = false) Long id){
        try {
            return  new ResponseEntity<>(this.thermostatService.getSensors(id), HttpStatus.OK);

        }
        catch (NotFoundException e){
            return getError(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAuthority(#id)")
    @PostMapping("sensor/state")
    public ResponseEntity<Object> postSensorState (@RequestParam(value = "thermostat_id") Long id, @RequestParam(value = "sensor_id") Long sensor_id, @RequestParam(value = "state") boolean state){

        try {
            Sensor sensor = this.thermostatService.setSensorState(id,sensor_id, state);
            return new ResponseEntity<>(sensor, HttpStatus.OK);
        }
        catch (NotFoundException e){
            return getError(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    @PreAuthorize("hasAuthority(#id)")
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

    @PreAuthorize("hasAuthority(#id)")
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

    @PreAuthorize("hasAuthority(#id)")
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

    @PreAuthorize("hasAuthority(#id)")
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

    @PreAuthorize("hasAuthority(#id)")
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


    @PreAuthorize("hasAuthority(#id)")
    @GetMapping("measurements/last")
    public ResponseEntity<Object> getMeasurements(@RequestParam(value = "thermostat_id") Long id) {

        try {
            List<Measurement> measurements = this.thermostatService.getLastMeasurements(id);
            return new ResponseEntity<>(measurements, HttpStatus.OK);
        } catch (NotFoundException e) {
            return getError(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @PreAuthorize("hasAuthority(#id)")
    @GetMapping("measurements/stats")
    public ResponseEntity<Object> getMeasurementsStats(@RequestParam(value = "thermostat_id") Long id, @RequestParam(value = "date_start", required = false) String dateStart, @RequestParam(value = "date_end", required = false) String dateEnd, @RequestParam(value = "sensor_id") Long sensor_id) {

        try {
            SensorStats sensorStats = this.thermostatService.getMeasurementsStats(id, sensor_id, dateStart, dateEnd);
            if(sensorStats == null){
                return getError("", HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(sensorStats, HttpStatus.OK);
        }
        catch (NotFoundException e) {
            return getError(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    @PreAuthorize("hasAuthority(#id)")
    @GetMapping("measurements")
    public ResponseEntity<List<Measurement>> getMeasurements(@RequestParam(value = "thermostat_id") Long id, @RequestParam(value = "date_start", required = false) String dateStart, @RequestParam(value = "date_end", required = false) String dateEnd, @RequestParam(value = "sensor_id") Long sensor_id) {

        List<Measurement> measurements = null;
        try {
            measurements = this.thermostatService.getMeasurements(id, sensor_id, dateStart, dateEnd);
            return new ResponseEntity<>(measurements, HttpStatus.OK);
        } catch (NotFoundException e) {
            return getError(e.getMessage(), HttpStatus.BAD_REQUEST);
        }


    }


    @PreAuthorize("hasAuthority(#id)")
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

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("measurements/drop")
    public  ResponseEntity<Object> deleteMeasurement(){

        return new ResponseEntity<>(this.thermostatService.cleanMeasurements(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
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
