package sit.int221.mytasksservice.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sit.int221.mytasksservice.config.AuthUser;
import sit.int221.mytasksservice.dtos.response.response.ItemNotFoundException;
import sit.int221.mytasksservice.models.secondary.Users;
import sit.int221.mytasksservice.repositories.secondary.UsersRepository;

@Service
public class JwtUserDetailsService implements  UserDetailsService {

    @Autowired
    private UsersRepository usersRepository;


    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Users user = usersRepository.findByUsername(userName).orElse(usersRepository.findByOid(userName));
        if (user == null) {
            throw new ItemNotFoundException(userName + " does not exist");
        }
        return new AuthUser(user.getOid(), user.getName(), user.getUsername(), user.getPassword(), user.getEmail(), user.getRole());
    }

}