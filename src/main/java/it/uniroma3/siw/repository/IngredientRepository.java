package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import it.uniroma3.siw.model.Ingredient;

public interface IngredientRepository extends CrudRepository<Ingredient, Long>{
    
    public List<Ingredient> findByName(String name);

    List<Ingredient> findByNameContainingIgnoreCase(String name);

}
