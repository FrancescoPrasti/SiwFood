package it.uniroma3.siw.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Ingredient;
import it.uniroma3.siw.model.Recipe;
import it.uniroma3.siw.repository.RecipeRepository;

@Service
public class RecipeService {
    
    @Autowired
	private RecipeRepository recipeRepository;

	public Recipe findById(Long id) {
		return recipeRepository.findById(id).get();
	}

	public Iterable<Recipe> findAll() {
		return recipeRepository.findAll();
	}

	public Recipe save(Recipe recipe){
		return recipeRepository.save(recipe);
	}

	public List<Recipe> findByName(String name){
		return recipeRepository.findByName(name);
	}

	public Iterable<Ingredient> findAllIngredients(Recipe recipe) {
		return recipe.getIngredients();
	}

	public void deleteRecipeById(Long id) {
        recipeRepository.deleteById(id);
    }

	public List<Recipe> findByNameContainingIgnoreCase(String name) {
		return recipeRepository.findByNameContainingIgnoreCase(name);
	}

	public boolean existsByName(String name){
		return recipeRepository.existsByName(name);
	}

}