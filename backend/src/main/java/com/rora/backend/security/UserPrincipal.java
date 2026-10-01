package com.rora.backend.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rora.backend.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Getter
@AllArgsConstructor
@Builder
public class UserPrincipal implements UserDetails {

    private final String id;
    private final String email;
    private final String name;
    @JsonIgnore
    private final String password;
    private final String status;
    private final Collection<? extends GrantedAuthority> authorities;

    public static UserPrincipal create(User user) {
        Set<GrantedAuthority> authorities = new HashSet<>();

        if (user.getRoles() != null) {
            user.getRoles().forEach(role -> {
                authorities.add(new SimpleGrantedAuthority(role.getName()));
                if (role.getPermissions() != null) {
                    role.getPermissions().forEach(permission ->
                            authorities.add(new SimpleGrantedAuthority(permission.getName())));
                }
            });
        }

        return UserPrincipal.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .password(user.getPasswordHash())
                .status(user.getStatus())
                .authorities(authorities)
                .build();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !"LOCKED".equalsIgnoreCase(status);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return "ACTIVE".equalsIgnoreCase(status);
    }
}
