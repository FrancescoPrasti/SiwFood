package it.uniroma3.siw.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Admin;
import it.uniroma3.siw.model.Chef;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.repository.CredentialsRepository;
import jakarta.transaction.Transactional;

@Service
public class CredentialsService {

    @Autowired
    protected PasswordEncoder passwordEncoder;
    
    @Autowired
    private CredentialsRepository credentialsRepository;

    @Transactional
    public Credentials getCredentials(Long id) {
        Optional<Credentials> result = this.credentialsRepository.findById(id);
        return result.orElse(null);
    }
    
    @Transactional
    public Credentials getCredentials(Chef chef) {
        Optional<Credentials> result = this.credentialsRepository.findByChef(chef);
        return result.orElse(null);
    }

    @Transactional
    public Credentials getCredentials(Admin admin) {
        Optional<Credentials> result = this.credentialsRepository.findByAdmin(admin);
        return result.orElse(null);
    }

    @Transactional
    public Credentials getCredentials(String username) {
        Optional<Credentials> result = this.credentialsRepository.findByUsername(username);
        return result.orElse(null);
    }

    @Transactional
    public Credentials saveCredentials(Credentials credentials) {
        credentials.setPassword(this.passwordEncoder.encode(credentials.getPassword()));
        return this.credentialsRepository.save(credentials);
    }

    @Transactional
    public void deleteCredentialsById(Long id) {
        credentialsRepository.deleteById(id);
    }

}
