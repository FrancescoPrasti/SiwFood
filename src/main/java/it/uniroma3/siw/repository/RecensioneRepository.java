package it.uniroma3.siw.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import it.uniroma3.siw.model.Recensione;
import it.uniroma3.siw.model.Recipe;
import it.uniroma3.siw.model.Chef;

@Repository
public interface RecensioneRepository extends CrudRepository<Recensione, Long> {
    //Optional<Recensione> findByRecipeAndChef(Recipe recipe, Chef chef);
    Recensione findByRecipeAndChef(Recipe recipe, Chef chef);

    List<Recensione> findByRecipe(Recipe recipe);
}