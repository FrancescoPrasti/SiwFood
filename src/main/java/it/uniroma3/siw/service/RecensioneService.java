package it.uniroma3.siw.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Chef;
import it.uniroma3.siw.model.Recensione;
import it.uniroma3.siw.model.Recipe;
import it.uniroma3.siw.repository.RecensioneRepository;

@Service
public class RecensioneService {
    
    @Autowired
    private RecensioneRepository recensioneRepository;

    public Recensione findByRecipeAndChef(Recipe recipe, Chef chef){
        return this.recensioneRepository.findByRecipeAndChef(recipe, chef);
    }

    public List<Recensione> findByRecipe(Recipe recipe){
        return this.recensioneRepository.findByRecipe(recipe);
    }

    public void save(Recensione recensione){
        this.recensioneRepository.save(recensione);
    }

}
