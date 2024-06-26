package it.uniroma3.siw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import it.uniroma3.siw.model.Chef;
import it.uniroma3.siw.service.ChefService;

@RestController
public class ChefRestController {

	@Autowired
	public ChefService chefService;

	@GetMapping (value="/rest/chefs")
	public List<Chef> showRestChefs(){
		List<Chef> Chefs = (List<Chef>) this.chefService.findAll();
		return Chefs;
	}
	
	@GetMapping (value="/rest/chef/{id}")
	public Chef showRestChef(@PathVariable("id") Long id) {
		return this.chefService.findById(id);
	}
}
