package com.example.cod_sursa_proiect.service;

import com.example.cod_sursa_proiect.entity.UserAccount;
import com.example.cod_sursa_proiect.repository.UserAccountRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserAccountService implements UserDetailsService {

    private final UserAccountRepository repository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserAccountService(UserAccountRepository repository) {
        this.repository = repository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount account = repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilizatorul nu a fost găsit."));

        return new User(account.getUsername(), account.getPassword(), new ArrayList<>());
    }

    public boolean registerUser(String username, String password) {
        if (repository.existsByUsername(username)) {
            return false;
        }
        String hashedNewPassword = passwordEncoder.encode(password);
        repository.save(new UserAccount(username, hashedNewPassword));
        return true;
    }
}