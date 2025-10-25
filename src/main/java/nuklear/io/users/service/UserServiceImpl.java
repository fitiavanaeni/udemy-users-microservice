package nuklear.io.users.service;

import nuklear.io.users.entities.Role;
import nuklear.io.users.entities.User;
import nuklear.io.users.exceptions.EmailAlreadyExistsException;
import nuklear.io.users.exceptions.ExpiredTokenException;
import nuklear.io.users.exceptions.InvalidTokenException;
import nuklear.io.users.register.RegistationRequest;
import nuklear.io.users.register.VerificationToken;
import nuklear.io.users.register.VerificationTokenRepository;
import nuklear.io.users.repos.RoleRepository;
import nuklear.io.users.repos.UserRepository;
import nuklear.io.users.util.EmailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Transactional
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    VerificationTokenRepository verificationTokenRepository;

    @Autowired
    EmailSender emailSender;

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User saveUser(User user) {

        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Role addRole(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public User addRoleToUser(String username, String rolename) {

        User usr = userRepository.findByUsername(username);
        Role r = roleRepository.findByRole(rolename);

        usr.getRoles().add(r);

        return usr;
    }

    @Override
    public User registerUser(RegistationRequest request) {

        Optional<User> optionaluser = userRepository.findByEmail(request.getEmail());
        if(optionaluser.isPresent())
            throw new EmailAlreadyExistsException("Email déjà existant!");

        User newUser = new User();

        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));
        newUser.setEnabled(false);

        userRepository.save(newUser);

        //ajouter à newUser le role par défaut USER
        Role r = roleRepository.findByRole("USER");
        List<Role> roles = new ArrayList<>();
        roles.add(r);
        newUser.setRoles(roles);
        userRepository.save(newUser);

        //génére le code secret
        String code = this.generateCode();
        VerificationToken token = new VerificationToken(code, newUser);
        verificationTokenRepository.save(token);

        //envoyer par email pour valider l'email de l'utilisateur
        sendEmailUser(newUser,token.getToken());

        return newUser;
    }

    public String generateCode() {
        Random random = new Random();
        Integer code = 100000 + random.nextInt(900000);
        return code.toString();
    }

    @Override
    public void sendEmailUser(User u, String code) {
        String emailBody ="Bonjour "+ "<h1>"+u.getUsername() +"</h1>" +
                " Votre code de validation est "+"<h1>"+code+"</h1>";
        emailSender.sendEmail(u.getEmail(), emailBody);
    }

    @Override
    public User validateToken(String code) {
        VerificationToken token = verificationTokenRepository.findByToken(code);
        if(token == null){
            throw new InvalidTokenException("Invalid Token");
        }
        User user = token.getUser();
        Calendar calendar = Calendar.getInstance();
        if ((token.getExpirationTime().getTime() - calendar.getTime().getTime()) <= 0){
            verificationTokenRepository.delete(token); throw new ExpiredTokenException("expired Token");
        }
        user.setEnabled(true);
        userRepository.save(user);
        return user;
    }
}
