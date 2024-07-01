package it.uniroma3.siw.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.uniroma3.siw.model.Ingredient;
import it.uniroma3.siw.model.Recensione;
import it.uniroma3.siw.controller.validator.RecipeValidator;
import it.uniroma3.siw.model.Chef;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Recipe;
import it.uniroma3.siw.service.ChefService;
import it.uniroma3.siw.service.IngredientService;
import it.uniroma3.siw.service.RecensioneService;
import it.uniroma3.siw.repository.RecipeRepository;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.RecipeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
public class RecipeController {
    
    @Autowired
    private RecipeValidator recipeValidator;

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private IngredientService ingredientService;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private ChefService chefService;

    @Autowired
    private CredentialsService credentialService;

    @Autowired
    private RecensioneService recensioneService;

    

    /*GIUSTO PRIMA DELL'INSERIMENTO DELLE RECENSIONI
    @GetMapping("/recipe/{id}")
    public String getRecipe(@PathVariable("id") Long id, Model model, @ModelAttribute("userId") Long userId) {
        Recipe recipe = recipeService.findById(id);
        if(credentialService.getCredentials((Long) model.getAttribute("userId")).getRole().equals("CHEF")){
            Chef chef = chefService.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + userId));
            boolean isFavorite = chef.getRicettePreferite().contains(recipe);
            model.addAttribute("isFavorite", isFavorite);
            model.addAttribute("role", "CHEF");
        }
        else
            model.addAttribute("role", "ADMIN");
        
        model.addAttribute("recipe", recipe);
        model.addAttribute("prepTime", recipe.getPrepTime());
        model.addAttribute("servings", recipe.getServings());
        model.addAttribute("difficulty", recipe.getDifficulty());
        model.addAttribute("category", recipe.getCategory());
        return "recipe.html";
    }*/

    @GetMapping("/recipe/{id}")
    public String getRecipe(@PathVariable("id") Long id, Model model, @ModelAttribute("userId") Long userId) {
        System.out.println("QUA SI1");
        Recipe recipe = recipeService.findById(id);
        System.out.println(recipe.getName());
        //Chef chef = chefService.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + userId));
        System.out.println((Long) model.getAttribute("userId"));
        if((Long) model.getAttribute("userId") == null){
            model.addAttribute("role", "DEFAULT");
            model.addAttribute("recipe", recipe);
            model.addAttribute("recensioni", recensioneService.findByRecipe(recipe));
        }else{
            if(credentialService.getCredentials((Long) model.getAttribute("userId")).getRole().equals("CHEF")){
                Chef chef = chefService.findById(userId);
                boolean isFavorite = chef.getRicettePreferite().contains(recipe);
                model.addAttribute("isFavorite", isFavorite);
                model.addAttribute("recipe", recipe);
                model.addAttribute("role", "CHEF");
    
                /*Optional<Recensione> recensioneOpt = recensioneService.findByRecipeAndChef(recipe, chef);
                if (recensioneOpt.isPresent()) {
                    model.addAttribute("recensione", recensioneOpt.get());
                } else {
                    model.addAttribute("recensione", new Recensione());
                }*/
                Recensione recensioneChef = recensioneService.findByRecipeAndChef(recipe, chef);
                model.addAttribute("recensioneChef", recensioneChef);
                model.addAttribute("recensioni", recensioneService.findByRecipe(recipe));
            }
            else{
                model.addAttribute("role", "ADMIN");
                model.addAttribute("recipe", recipe);
                model.addAttribute("recensioni", recensioneService.findByRecipe(recipe));
            }
        }
        
        return "recipe.html";
    }

    @PostMapping("/recipe/{id}/recensione")
    public String addRecensione(@PathVariable("id") Long id, @RequestParam int valutazione, 
                                @RequestParam String contenutoRecensione, @ModelAttribute("userId") Long userId, Model model) {
        Recipe recipe = recipeService.findById(id);
        Chef chef = chefService.findById(userId);
        Recensione recensione = new Recensione();
        recensione.setChef(chef);
        recensione.setRecipe(recipe);
        recensione.setValutazione(valutazione);
        recensione.setContenutoRecensione(contenutoRecensione);
        recensioneService.save(recensione);
        recipe.getRecensioni().add(recensione);
        /*if (recensioneOpt.isPresent()) {
            // Aggiorna la recensione esistente
            Recensione recensione = recensioneOpt.get();
            recensione.setValutazione(valutazione);
            recensione.setContenutoRecensione(contenutoRecensione);
            recensioneService.save(recensione);
        } else {
            // Crea una nuova recensione
            Recensione recensione = new Recensione();
            recensione.setRecipe(recipe);
            recensione.setChef(chef);
            recensione.setValutazione(valutazione);
            recensione.setContenutoRecensione(contenutoRecensione);
            recensioneService.save(recensione);
        }*/

        return "redirect:/recipe/" + id; ///" + id;
    }

    @GetMapping("/recipes")
    public String showRecipes(Model model) {
        List<Recipe> recipes = (List<Recipe>) recipeService.findAll();
        recipes.sort(Comparator.comparing(Recipe::getName));

        model.addAttribute("recipes", recipes);

        if(model.getAttribute("userId")==null)
            model.addAttribute("role", "DEFAULT");
        else
            model.addAttribute("role", credentialService.getCredentials((Long) model.getAttribute("userId")).getRole());

        return "recipes.html";
    }

    @PostMapping("/searchAll")
	public String searchRecipes(Model model, @RequestParam String name) {
		String searchQuery = name.toLowerCase(); // Converte la query di ricerca in minuscolo per una ricerca case-insensitive
        List<Recipe> recipes = this.recipeService.findByNameContainingIgnoreCase(searchQuery);
        List<Chef> chefs = this.chefService.findByNameContainingIgnoreCase(searchQuery);
        List<Ingredient> allIngredients = this.ingredientService.findByNameContainingIgnoreCase(searchQuery);
        Set<Ingredient> uniqueIngredients = new HashSet<>(allIngredients);
        
        model.addAttribute("recipes", recipes);
        model.addAttribute("ingredients", uniqueIngredients);
        model.addAttribute("chefs", chefs);

        if(model.getAttribute("userId")==null)
            model.addAttribute("role", "DEFAULT");
        else
            model.addAttribute("role", credentialService.getCredentials((Long) model.getAttribute("userId")).getRole());

        return "foundResults.html";
	}

    @GetMapping("/formNewRecipe")
   public String getNewRecipeForm(Model model) {
       model.addAttribute("recipe", new Recipe());
       return "formNewRecipe.html";
   }

   @PostMapping("/formNewRecipe")
    public String addNewRecipe(@Valid@ModelAttribute Recipe recipe, @RequestParam("images") MultipartFile[] files, Model model, BindingResult bindingResult) {
        try {
            List<Ingredient> ingredients = recipe.getIngredients();
            for (Ingredient ingredient : ingredients) {
                if(ingredientService.findByName(ingredient.getName()).size() != 0){
                    //c'è almeno un ingrediente nel repository col nome del nuovo ingrediente
                    for (Ingredient i : ingredientService.findByName(ingredient.getName())){
                        if(i.getListaImmagini() != null){
                            List<String> lista = i.getListaImmagini();
                            ingredient.setListaImmagini(lista);
                        }
                    }
                }
                ingredientService.save(ingredient);
            }

            recipe.setChef(this.chefService.findById((Long) model.getAttribute("userId")));

            List<String> imagePaths = new ArrayList<>();
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    byte[] byteFoto = file.getBytes();
                    String encodedImage = Base64.getEncoder().encodeToString(byteFoto);
                    imagePaths.add(encodedImage);
                }
            }

            recipe.setBase64(imagePaths);

            this.recipeValidator.validate(recipe, bindingResult);
            if(bindingResult.hasErrors()){
                model.addAttribute("error","Esiste gia' una ricetta con questo nome!");
                model.addAttribute("recipe", new Recipe());
                return "formNewRecipe";
            }

            recipeRepository.save(recipe);
            model.addAttribute("message", "Recipe uploaded successfully!");

            return "redirect:/";
        } catch (IOException e) {
            model.addAttribute("message", "Recipe upload failed!");
            return "formNewRecipe";
        }
    }

   /*@PostMapping("/formNewRecipe")
    public String addNewRecipe(@ModelAttribute Recipe recipe, @RequestParam("images") MultipartFile[] files, Model model) {
        try {
            List<Ingredient> ingredients = recipe.getIngredients();
            for (Ingredient ingredient : ingredients) {
                //ingredient.setChef(this.chefService.findById((Long) model.getAttribute("userId")).get());
                ingredientService.save(ingredient);
            }

            recipe.setChef(this.chefService.findById((Long) model.getAttribute("userId")).get());

            List<String> imagePaths = new ArrayList<>();
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    byte[] byteFoto = file.getBytes();
                    String encodedImage = Base64.getEncoder().encodeToString(byteFoto);
                    imagePaths.add(encodedImage);
                }
            }

            recipe.setBase64(imagePaths);
            recipeRepository.save(recipe);
            model.addAttribute("message", "Recipe uploaded successfully!");

            return "redirect:/";
        } catch (IOException e) {
            model.addAttribute("message", "Recipe upload failed!");
            return "formNewRecipe";
        }
    }*/

    @GetMapping("/admin/formNewRecipeAdmin")
   public String getNewRecipeFormAdmin(Model model) {
       model.addAttribute("recipe", new Recipe());
       model.addAttribute("chefs", this.chefService.findAll());
       return "formNewRecipeAdmin.html";
   }

   @PostMapping("/admin/formNewRecipeAdmin")
    public String addNewRecipeAdmin(@Valid @ModelAttribute Recipe recipe, @RequestParam("chef.id") Long chefId, @RequestParam("images") MultipartFile[] files, Model model, BindingResult bindingResult) {
        try {
            Chef chef = chefService.findById(chefId);
            recipe.setChef(chef);

            List<Ingredient> ingredients = recipe.getIngredients();
            for (Ingredient ingredient : ingredients) {
                if(ingredientService.findByName(ingredient.getName()).size() != 0){
                    //c'è almeno un ingrediente nel repository col nome del nuovo ingrediente
                    for (Ingredient i : ingredientService.findByName(ingredient.getName())){
                        if(i.getListaImmagini() != null){
                            List<String> lista = i.getListaImmagini();
                            ingredient.setListaImmagini(lista);
                        }
                    }
                }
                ingredientService.save(ingredient);
            }

            List<String> imagePaths = new ArrayList<>();
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    byte[] byteFoto = file.getBytes();
                    String encodedImage = Base64.getEncoder().encodeToString(byteFoto);
                    imagePaths.add(encodedImage);
                }
            }

            recipe.setBase64(imagePaths);

            this.recipeValidator.validate(recipe, bindingResult);
            if(bindingResult.hasErrors()){
                model.addAttribute("error","Esiste gia' una ricetta con questo nome!");
                model.addAttribute("recipe", new Recipe());
                model.addAttribute("chefs", this.chefService.findAll());
                return "formNewRecipeAdmin";
            }

            recipeRepository.save(recipe);
            model.addAttribute("message", "Recipe uploaded successfully!");

            return "redirect:/";
        } catch (IOException e) {
            model.addAttribute("message", "Recipe upload failed!");
            return "formNewRecipeAdmin";
        }
    }

    @GetMapping("/admin/gestisciRicetta")
    public String getRecipesDelete(Model model) {
        model.addAttribute("recipes", this.recipeService.findAll());
        if(model.getAttribute("userId")==null)
            model.addAttribute("role", "DEFAULT");
        else
            model.addAttribute("role", credentialService.getCredentials((Long) model.getAttribute("userId")).getRole());
        return "gestisciRicette.html";
    }

   @PostMapping("/gestisciRicetta/{id}")
   public String deleteRecipe(@PathVariable Long id, RedirectAttributes redirectAttributes, Model model) {
       try {
           recipeService.deleteRecipeById(id);
           redirectAttributes.addFlashAttribute("successMessage", "Ricetta eliminata con successo.");
       } catch (Exception e) {
           redirectAttributes.addFlashAttribute("errorMessage", "Errore durante l'eliminazione della ricetta.");
       }

       if(credentialService.getCredentials((Long) model.getAttribute("userId")).getRole().equals("ADMIN"))
        return "redirect:/admin/gestisciRicetta";
       else
        return "redirect:/gestisciRicettaChef";
   }

   @GetMapping("/gestisciRicettaChef")
    public String getRecipesDeleteChef(Model model) {
        model.addAttribute("recipes", this.chefService.findById((Long) model.getAttribute("userId")).getRecipesCreated());
        if(model.getAttribute("userId")==null)
            model.addAttribute("role", "DEFAULT");
        else
            model.addAttribute("role", credentialService.getCredentials((Long) model.getAttribute("userId")).getRole());
        return "gestisciRicette.html";
    }

    @GetMapping("/admin/modificaRecipe")
    public String getModificaRecipePage(Model model) {
        model.addAttribute("recipes", this.recipeService.findAll());
        return "modificaRecipe.html";
    }

    @GetMapping("/modificaRecipeChef")
    public String getModificaRecipeChefPage(Model model) {
        model.addAttribute("recipes", this.chefService.findById((Long) model.getAttribute("userId")).getRecipesCreated());
        return "modificaRecipe.html";
    }

    @GetMapping("/editRecipe/{id}")
    public String showEditRecipeForm(@PathVariable("id") Long id, Model model) {
        Recipe recipe = recipeService.findById(id);
        if (recipe == null) {
            return "error"; // pagina di errore se la ricetta non esiste
        }
        model.addAttribute("recipe", recipe);
        model.addAttribute("ingredientCount", recipe.getIngredients().size());
        //UserDetails userDetails = (UserDetails)model.getAttribute("userDetails");
        if(this.credentialService.getCredentials((Long) model.getAttribute("userId")).getRole().equals("ADMIN")){
            model.addAttribute("chefs", this.chefService.findAll());
            return "editRecipe.html";
        }
        else
            return "editRecipeChef.html";
    }

    @PostMapping("/admin/updateRecipe")
    public String updateRecipe(@ModelAttribute Recipe updatedRecipe,
                           @RequestParam("newImages") MultipartFile[] newImages,
                           @RequestParam(value = "removeImageIndexes", required = false) List<Integer> removeImageIndexes,
                           @RequestParam("chef.id") Long chefId,
                           Model model) {
        // Recupera la ricetta originale dal repository
        Recipe originalRecipe = recipeRepository.findById(updatedRecipe.getId())
            .orElseThrow(() -> new IllegalArgumentException("Invalid recipe Id:" + updatedRecipe.getId()));

        Chef chef = chefService.findById(chefId);
        originalRecipe.setChef(chef);

        // Aggiorna i campi della ricetta originale con quelli della ricetta modificata
        originalRecipe.setName(updatedRecipe.getName());
        originalRecipe.setDescription(updatedRecipe.getDescription());
        originalRecipe.setCategory(updatedRecipe.getCategory());
        originalRecipe.setDifficulty(updatedRecipe.getDifficulty());
        originalRecipe.setPrepTime(updatedRecipe.getPrepTime());
        originalRecipe.setServings(updatedRecipe.getServings());

        //Setto lo chef che ha creato gli ingredienti
        for (Ingredient ingredient : updatedRecipe.getIngredients()) {
            if(ingredientService.findByName(ingredient.getName()).size() != 0){
                //c'è almeno un ingrediente nel repository col nome del nuovo ingrediente
                for (Ingredient i : ingredientService.findByName(ingredient.getName())){
                    if(i.getListaImmagini() != null){
                        List<String> lista = i.getListaImmagini();
                        ingredient.setListaImmagini(lista);
                    }
                }
            }
        }

        originalRecipe.setIngredients(updatedRecipe.getIngredients());
        //parte nuova
        //List<Ingredient> l = updatedRecipe.getIngredients();
        

        // Rimuovi le immagini specificate
        if (removeImageIndexes != null) {
            for (int index : removeImageIndexes) {
                originalRecipe.getBase64().remove(index);
            }
        }

        // Aggiungi le nuove immagini
        if (newImages != null) {
            for (MultipartFile newImage : newImages) {
                if (!newImage.isEmpty()) {
                    try {
                        // Converti l'immagine in base64 e aggiungila alla lista delle immagini
                        String base64Image = Base64.getEncoder().encodeToString(newImage.getBytes());
                        originalRecipe.getBase64().add(base64Image);
                    } catch (IOException e) {
                        e.printStackTrace();
                        model.addAttribute("errorMessage", "An error occurred while uploading one of the images.");
                    }
                }
            }
        }

        // Salva la ricetta aggiornata nel repository
        recipeRepository.save(originalRecipe);

        // Redirect alla visualizzazione della ricetta modificata
        return "redirect:/recipe/" + originalRecipe.getId();
    }

    @PostMapping("/updateRecipeChef")
    public String updateRecipeChef(@ModelAttribute Recipe updatedRecipe,
                           @RequestParam("newImages") MultipartFile[] newImages,
                           @RequestParam(value = "removeImageIndexes", required = false) List<Integer> removeImageIndexes,
                           Model model) {
        // Recupera la ricetta originale dal repository
        Recipe originalRecipe = recipeRepository.findById(updatedRecipe.getId())
            .orElseThrow(() -> new IllegalArgumentException("Invalid recipe Id:" + updatedRecipe.getId()));

        // Aggiorna i campi della ricetta originale con quelli della ricetta modificata
        originalRecipe.setName(updatedRecipe.getName());
        originalRecipe.setDescription(updatedRecipe.getDescription());
        originalRecipe.setCategory(updatedRecipe.getCategory());
        originalRecipe.setDifficulty(updatedRecipe.getDifficulty());
        originalRecipe.setPrepTime(updatedRecipe.getPrepTime());
        originalRecipe.setServings(updatedRecipe.getServings());

        //Setto lo chef che ha creato gli ingredienti
        for (Ingredient ingredient : updatedRecipe.getIngredients()) {
            if(ingredientService.findByName(ingredient.getName()).size() != 0){
                //c'è almeno un ingrediente nel repository col nome del nuovo ingrediente
                for (Ingredient i : ingredientService.findByName(ingredient.getName())){
                    if(i.getListaImmagini() != null){
                        List<String> lista = i.getListaImmagini();
                        ingredient.setListaImmagini(lista);
                    }
                }
            }
        }

        originalRecipe.setIngredients(updatedRecipe.getIngredients());

        // Rimuovi le immagini specificate
        if (removeImageIndexes != null) {
            for (int index : removeImageIndexes) {
                originalRecipe.getBase64().remove(index);
            }
        }

        // Aggiungi le nuove immagini
        if (newImages != null) {
            for (MultipartFile newImage : newImages) {
                if (!newImage.isEmpty()) {
                    try {
                        // Converti l'immagine in base64 e aggiungila alla lista delle immagini
                        String base64Image = Base64.getEncoder().encodeToString(newImage.getBytes());
                        originalRecipe.getBase64().add(base64Image);
                    } catch (IOException e) {
                        e.printStackTrace();
                        model.addAttribute("errorMessage", "An error occurred while uploading one of the images.");
                    }
                }
            }
        }

        // Salva la ricetta aggiornata nel repository
        recipeRepository.save(originalRecipe);

        // Redirect alla visualizzazione della ricetta modificata
        return "redirect:/recipe/" + originalRecipe.getId();
    }

    @PostMapping("/favorite/{id}")
    public String toggleFavorite(@PathVariable("id") Long id, @ModelAttribute("userId") Long userId, RedirectAttributes redirectAttributes) {
        Recipe recipe = recipeService.findById(id);
        Chef chef = chefService.findById(userId);
        
        if (chef.getRicettePreferite().contains(recipe)) {
            chef.getRicettePreferite().remove(recipe);
        } else {
            chef.getRicettePreferite().add(recipe);
        }
        
        chefService.save(chef);
        redirectAttributes.addFlashAttribute("userId", userId);
        return "redirect:/recipe/" + id;
    }

    @GetMapping("/ricettePreferite")
    public String getFavoriteRecipes(Model model, @ModelAttribute("userId") Long userId) {
        Chef chef = chefService.findById(userId);
        model.addAttribute("favoriteRecipes", chef.getRicettePreferite());
        return "ricettePreferite.html";
    }

}
