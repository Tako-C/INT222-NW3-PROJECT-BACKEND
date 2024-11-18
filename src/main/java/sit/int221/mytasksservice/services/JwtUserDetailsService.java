package sit.int221.mytasksservice.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.mytasksservice.config.AuthUser;
import sit.int221.mytasksservice.dtos.response.response.ItemNotFoundException;
import sit.int221.mytasksservice.models.secondary.Users;
import sit.int221.mytasksservice.repositories.secondary.UsersRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class JwtUserDetailsService implements  UserDetailsService {

    @Autowired
    private UsersRepository usersRepository;


    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Users user = usersRepository.findByUsername(userName).orElseThrow(() -> new ItemNotFoundException("Inviter not found"));

        if (user == null) {
            user = usersRepository.findByOid(userName);
        }

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, userName + " does not exist !!");
        }

        List<GrantedAuthority> roles = List.of(new SimpleGrantedAuthority(user.getRole()));

        return new AuthUser(user.getOid(), user.getName(), user.getUsername(), user.getPassword(), user.getEmail(), user.getRole(), roles);
    }

}