package nuklear.io.users.service;

import nuklear.io.users.entities.Role;
import nuklear.io.users.entities.User;
import nuklear.io.users.register.RegistationRequest;

import java.util.List;

public interface UserService {

    List<User> findAllUsers();

    User saveUser(User user);

    User findUserByUsername(String username);

    Role addRole(Role role);

    User addRoleToUser(String username, String rolename);

    User registerUser(RegistationRequest request);
    User validateToken(String code);

    void sendEmailUser(User u, String code);

}
