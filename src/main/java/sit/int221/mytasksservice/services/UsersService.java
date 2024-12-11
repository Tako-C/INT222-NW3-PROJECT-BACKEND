package sit.int221.mytasksservice.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.mytasksservice.dtos.response.response.ItemNotFoundException;
import sit.int221.mytasksservice.models.primary.PrimaryUsers;
import sit.int221.mytasksservice.models.secondary.Users;
import sit.int221.mytasksservice.repositories.primary.PrimaryUsersRepository;
import sit.int221.mytasksservice.repositories.secondary.UsersRepository;

import java.util.List;

@Service
public class UsersService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PrimaryUsersRepository primaryUsersRepository;

    private final Argon2PasswordEncoder passwordEncoder = new Argon2PasswordEncoder(16,
            32,
            1,
            65536,
            4);

    public void saveUserToPrimary(Users user) {
        if (!primaryUsersRepository.existsByOid(user.getOid())) {
            PrimaryUsers primaryUser = new PrimaryUsers();
            primaryUser.setOid(user.getOid());
            primaryUser.setName(user.getName());
            primaryUser.setUsername(user.getUsername());
            primaryUser.setEmail(user.getEmail());
            primaryUsersRepository.save(primaryUser);
        }
    }

    public List<Users> getAllUsers() {
        return usersRepository.findAll();
    }

    public Users login(String username, String rawPassword) {
        if (username.trim().isEmpty() || rawPassword.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if (username.length() > 50 || rawPassword.length() < 8 || rawPassword.length() > 14) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        Users user = usersRepository.findByUsername(username).orElseThrow(() -> new ItemNotFoundException("User not found"));
        if (user == null || !passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or Password is incorrect");
        }
        return user;
    }

}