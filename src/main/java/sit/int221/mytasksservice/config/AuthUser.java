package sit.int221.mytasksservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import sit.int221.mytasksservice.models.secondary.Users;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
public class AuthUser extends Users implements UserDetails, Serializable {

//    public AuthUser() {
//        super("anonymous", "", new ArrayList<GrantedAuthority>());
//    }
//
//    public AuthUser(String userName, String password) {
//        super(userName, password, new ArrayList<GrantedAuthority>());
//    }

    public AuthUser(String oid, String name, String userName, String password, String email, String role, Collection<? extends GrantedAuthority> authorities) {
        super(userName, password, authorities);
        this.oid = oid;
        this.name = name;
        this.email = email;
        this.role = role;
    }

//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return super.getAuthorities();
//    }

    @Override
    public String getPassword() {
        return super.getPassword();
    }

    @Override
    public String getUsername() {
        return super.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}