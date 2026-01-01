package com.sangraj.carrental.service;

import com.sangraj.carrental.repository.UserRepository;
import com.sangraj.carrental.repository.VarificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import com.sangraj.carrental.entity.AppUser;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private final UserRepository userRepo;

    public CustomUserDetailsService(UserRepository userRepo) {
        this.userRepo = userRepo;

    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        AppUser user = userRepo.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with email: " + email));
        System.out.println("LOGIN → enabled = " + user.isEnabled());
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.isEnabled(),
                true,
                true,
                true,
                List.of(() -> user.getRole().name())
        );
    }

    public UserDetails save(AppUser user){
        AppUser savedUser = userRepo.save(user);
        return new org.springframework.security.core.userdetails.User(
                savedUser.getEmail(),
                savedUser.getPassword(),
                List.of(() -> savedUser.getRole().name())
        );
    }

}
