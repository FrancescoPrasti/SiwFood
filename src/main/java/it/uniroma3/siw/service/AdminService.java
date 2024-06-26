package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Admin;
import it.uniroma3.siw.repository.AdminRepository;

@Service
public class AdminService {
    
    @Autowired
    private AdminRepository adminRepository;

    public Admin findById(Long id){
        return this.adminRepository.findById(id).get();
    }

    public Iterable<Admin> findAll(){
        return this.adminRepository.findAll();
    }

    public void save(Admin Admin){
        this.adminRepository.save(Admin);
    }

}
