package it.uniroma3.siw.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Chef;
import it.uniroma3.siw.model.Recipe;
import it.uniroma3.siw.repository.ChefRepository;

@Service
public class ChefService {
    
    @Autowired
    private ChefRepository chefRepository;

    public Chef findById(Long id){
        return this.chefRepository.findById(id).get();
    }

    public Iterable<Chef> findAll(){
        return this.chefRepository.findAll();
    }

    public void save(Chef chef){
        this.chefRepository.save(chef);
    }

    public List<Chef> findByNameContainingIgnoreCase(String name) {
        return chefRepository.findByNameContainingIgnoreCase(name);
    }

    public void deleteChefById(Long id) {
        chefRepository.deleteById(id);
    }

}
