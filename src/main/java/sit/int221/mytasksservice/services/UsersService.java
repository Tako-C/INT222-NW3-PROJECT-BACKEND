package sit.int221.mytasksservice.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.mytasksservice.models.secondary.Users;
import sit.int221.mytasksservice.repositories.secondary.UsersRepository;

import java.util.List;

@Service
public class UsersService {

    @Autowired
    private UsersRepository userRepository;
    private final Argon2PasswordEncoder passwordEncoder = new Argon2PasswordEncoder(16,  // saltLength
            32,  // hashLength
            1,   // parallelism
            65536,  // memory
            4);

    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }

    public Users login(String username, String rawPassword) {
        if (username.trim().isEmpty() || rawPassword.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if (username.length() > 50 || rawPassword.length() < 8 || rawPassword.length() > 14) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        Users user = userRepository.findByUsername(username);
        if (user == null || !passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or Password is incorrect");
        }
        return user;
    }
}

