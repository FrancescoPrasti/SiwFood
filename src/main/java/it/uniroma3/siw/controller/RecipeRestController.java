package it.uniroma3.siw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import it.uniroma3.siw.model.Recipe;
import it.uniroma3.siw.service.RecipeService;

@RestController
public class RecipeRestController {

	@Autowired
	public RecipeService recipeService;

	@GetMapping (value="/rest/recipes")
	public List<Recipe> showRestRecipes(){
		List<Recipe> recipes = (List<Recipe>) this.recipeService.findAll();
		return recipes;
	}
	
	@GetMapping (value="/rest/recipe/{id}")
	public Recipe showRestRecipe(@PathVariable("id") Long id) {
		return this.recipeService.findById(id);
	}
}
