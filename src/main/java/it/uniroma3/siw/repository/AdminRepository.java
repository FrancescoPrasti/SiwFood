package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Admin;

public interface AdminRepository extends CrudRepository<Admin,Long>{
    
    public List<Admin> findByName(String name);

}
