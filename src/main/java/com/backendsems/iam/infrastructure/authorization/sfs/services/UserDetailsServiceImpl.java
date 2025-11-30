package com.backendsems.iam.infrastructure.authorization.sfs.services;

import com.backendsems.iam.domain.model.aggregates.User;
import com.backendsems.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import com.backendsems.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * User Details Service Implementation.
 * <p>
 * This class implements the UserDetailsService interface for Spring Security.
 * It loads user details by username (email).
 * </p>
 */
@Service
@Qualifier("defaultUserDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
        return new UserDetailsImpl(user);
    }
}