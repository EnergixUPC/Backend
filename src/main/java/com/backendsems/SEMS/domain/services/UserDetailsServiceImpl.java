package com.backendsems.SEMS.domain.services;

import com.backendsems.SEMS.domain.model.entities.User;
import com.backendsems.SEMS.infrastructure.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    
    private final UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("========================================");
        System.out.println("UserDetailsService.loadUserByUsername called");
        System.out.println("Looking for username/email: " + username);
        
        User user = userRepository.findByEmailOrUsername(username)
            .orElseThrow(() -> {
                System.out.println("USER NOT FOUND: " + username);
                System.out.println("========================================");
                return new UsernameNotFoundException("User not found: " + username);
            });
        
        System.out.println("User found!");
        System.out.println("User ID: " + user.getId());
        System.out.println("User email: " + user.getEmail());
        System.out.println("User username: " + user.getUsername());
        System.out.println("Password hash (first 20 chars): " + (user.getPassword() != null ? user.getPassword().substring(0, Math.min(20, user.getPassword().length())) : "null"));
        System.out.println("========================================");
        
        return new UserPrincipal(user);
    }
    
    public static class UserPrincipal implements UserDetails {
        private final User user;
        
        public UserPrincipal(User user) {
            this.user = user;
        }
        
        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
        }
        
        @Override
        public String getPassword() {
            return user.getPassword();
        }
        
        @Override
        public String getUsername() {
            return user.getEmail();
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
        
        public User getUser() {
            return user;
        }
    }
}