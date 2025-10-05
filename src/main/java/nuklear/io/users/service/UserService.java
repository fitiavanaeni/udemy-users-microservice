package nuklear.io.users.service;

import nuklear.io.users.entities.Role;
import nuklear.io.users.entities.User;

public interface UserService {

    User saveUser(User user);
    User findUserByUsername (String username);
    Role addRole(Role role);
    User addRoleToUser(String username, String rolename);
}
