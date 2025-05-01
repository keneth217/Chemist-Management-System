package com.chemist.system.security.security.userServices;

import com.chemist.system.models.Chemist;
import com.chemist.system.models.MasterUser;
import com.chemist.system.models.User;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.*;
import java.util.stream.Collectors;

@Data
@Builder
public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private String phoneNo;
    private String password;
    private Chemist chemist;
    private String chemistId;
    private Collection<? extends GrantedAuthority> authorities;

    public static UserDetailsImpl build(User user, Chemist chemist) {
        Set<String> roles = new HashSet<>(user.getUserTypes());

        List<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());

        return UserDetailsImpl.builder()
                .id(user.getId())
                .username(user.getUsername())
                .phoneNo(user.getPhoneNo())
                .password(user.getPassword())
                .chemist(chemist)
                .chemistId(user.getChemistId())
                .authorities(authorities)
                .build();
    }

    public static UserDetailsImpl buildForMasterUser(MasterUser user) {
        Set<String> roles = Collections.singleton(user.getUserTypes().toString());

        List<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());

        return UserDetailsImpl.builder()
                .id(user.getId())
                .username(user.getFirstName())
                .phoneNo(user.getPhoneNo())
                .password(user.getPassword())
                .chemist(null)
                .chemistId(String.valueOf(user.getId()))
                .authorities(authorities)
                .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
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