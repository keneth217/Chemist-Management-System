package com.chemist.system.security.security.userServices;

import com.chemist.system.master.model.MasterUser;
import com.chemist.system.tenant.model.Chemist;
import com.chemist.system.tenant.model.User;
import com.chemist.system.tenant.model.UserRole;
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
    private String userType;
    private UserRole userRole;
    private Collection<? extends GrantedAuthority> authorities;

    public static UserDetailsImpl build(User user, Chemist chemist) {
        List<GrantedAuthority> authorities = user.getUserTypes().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());

        return UserDetailsImpl.builder()
                .id(user.getId())
                .username(user.getUsername())
                .phoneNo(user.getPhoneNo())
                .password(user.getPassword())
                .chemist(chemist)
                .chemistId(String.valueOf(user.getChemistId()))
                .authorities(authorities)
                .userRole((UserRole) user.getRoles())
                .build();
    }

    public static UserDetailsImpl buildForMasterUser(MasterUser user) {
        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole())
        );

        return UserDetailsImpl.builder()
                .id(user.getId())
                .username(user.getUsername())
                .phoneNo(user.getPhoneNo())
                .password(user.getPassword())
                .chemist(null)
                .chemistId(null)
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