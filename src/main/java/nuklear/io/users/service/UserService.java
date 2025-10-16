package nuklear.io.users.service;

import nuklear.io.users.entities.Role;
import nuklear.io.users.entities.User;

import java.util.List;

public interface UserService {

    List<User> getAllUsers();

    User saveUser(User user);

    User findUserByUsername(String username);

    Role addRole(Role role);

    User addRoleToUser(String username, String rolename);

}
