package nuklear.io.users;

import jakarta.annotation.PostConstruct;
import nuklear.io.users.entities.Role;
import nuklear.io.users.entities.User;
import nuklear.io.users.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class UsersMicroserviceApplication {

	@Autowired
	UserService userService;

	public static void main(String[] args) {
		SpringApplication.run(UsersMicroserviceApplication.class, args);
	}

	@PostConstruct
	void init_users() {
		//ajouter les rôles
		userService.addRole(new Role(null,"ADMIN"));
		userService.addRole(new Role(null,"USER"));
		//ajouter les users
		userService.saveUser(new User(null,"admin","123",true,null));
		userService.saveUser(new User(null,"fitiavana","123",true,null));
		userService.saveUser(new User(null,"emmanuelon","123",true,null));
		//ajouter les rôles aux users
		userService.addRoleToUser("admin", "ADMIN");
		userService.addRoleToUser("admin", "USER");
		userService.addRoleToUser("fitiavana", "USER");
		userService.addRoleToUser("emmanuelon", "USER");
	}

	@Bean
	BCryptPasswordEncoder getBCE() {
		return new BCryptPasswordEncoder();
	}

}
