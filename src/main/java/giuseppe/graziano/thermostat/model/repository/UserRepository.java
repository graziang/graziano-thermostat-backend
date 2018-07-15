package giuseppe.graziano.thermostat.model.repository;

import giuseppe.graziano.thermostat.model.data.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);
}