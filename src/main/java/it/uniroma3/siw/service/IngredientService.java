package it.uniroma3.siw.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Ingredient;
import it.uniroma3.siw.model.Recipe;
import it.uniroma3.siw.repository.IngredientRepository;

@Service
public class IngredientService {
    
    @Autowired
	private IngredientRepository ingredientRepository;

	public Ingredient findById(Long id) {
		return ingredientRepository.findById(id).get();
	}

	public Iterable<Ingredient> findAll() {
		return ingredientRepository.findAll();
	}

	public void save(Ingredient ingredient){
		ingredientRepository.save(ingredient);
	}

	public List<Ingredient> findByName(String name){
		return ingredientRepository.findByName(name);
	}

	public void deleteIngredientById(Long id) {
        ingredientRepository.deleteById(id);
    }

	public List<Ingredient> findByNameContainingIgnoreCase(String name) {
    	return ingredientRepository.findByNameContainingIgnoreCase(name);
	}
	
}
