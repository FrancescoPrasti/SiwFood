package it.uniroma3.siw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import it.uniroma3.siw.model.Ingredient;
import it.uniroma3.siw.service.IngredientService;

@RestController
public class IngredientRestController {

	@Autowired
	public IngredientService ingredientService;

	@GetMapping (value="/rest/ingredients")
	public List<Ingredient> showRestIngredients(){
		List<Ingredient> ingredients = (List<Ingredient>) this.ingredientService.findAll();
		return ingredients;
	}
	
	@GetMapping (value="/rest/ingredient/{id}")
	public Ingredient showRestIngredient(@PathVariable("id") Long id) {
		return this.ingredientService.findById(id);
	}
}