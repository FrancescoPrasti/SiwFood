package it.uniroma3.siw.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ldap.embedded.EmbeddedLdapProperties.Credential;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.uniroma3.siw.model.Ingredient;
import it.uniroma3.siw.model.Recipe;
import it.uniroma3.siw.repository.IngredientRepository;
import it.uniroma3.siw.repository.RecipeRepository;
import it.uniroma3.siw.repository.ChefRepository;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.IngredientService;
import it.uniroma3.siw.service.RecipeService;
import jakarta.validation.Valid;

@Controller
public class IngredientController {
    
    @Autowired
    private IngredientService ingredientService;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private CredentialsService credentialService;

    @GetMapping("/ingredient/{id}")
        public String getIngredient(@PathVariable("id") Long id, Model model) {
        model.addAttribute("ingredient", this.ingredientService.findById(id));

        if(model.getAttribute("userId")==null)
            model.addAttribute("role", "DEFAULT");
        else
            model.addAttribute("role", credentialService.getCredentials((Long) model.getAttribute("userId")).getRole());

        return "ingredient.html";
    }

    @GetMapping("/ingredients")
    public String showIngredients(Model model) {
    List<Ingredient> allIngredients = (List<Ingredient>) ingredientService.findAll();
    // Set<Ingredient> uniqueIngredients = new HashSet<>(allIngredients);
    Set<Ingredient> uniqueIngredients = new TreeSet<>(Comparator.comparing(Ingredient::getName));
    uniqueIngredients.addAll(allIngredients);
    
    model.addAttribute("ingredients", uniqueIngredients);

    if(model.getAttribute("userId")==null)
            model.addAttribute("role", "DEFAULT");
        else
            model.addAttribute("role", credentialService.getCredentials((Long) model.getAttribute("userId")).getRole());
    
    return "ingredients.html";
    }

    @PostMapping("/searchIngredients")
	public String searchIngredients(Model model, @RequestParam String name) {
		model.addAttribute("ingredients", this.ingredientService.findByName(name));
		return "foundIngredients.html";
	}

    @GetMapping("/formNewIngredient")
   public String getNewIngredientForm(Model model) {
       model.addAttribute("ingredient", new Ingredient());
       return "formNewIngredient.html";
   }

   @PostMapping("/formNewIngredient")
    public String addNewIngredient(@ModelAttribute Ingredient ingredient, @RequestParam("image") MultipartFile file, Model model) {
        try {
    
            byte[] byteFoto = file.getBytes();
            ingredient.setBase64(Base64.getEncoder().encodeToString(byteFoto));
            List<String> l = new ArrayList<>();
            l.add(Base64.getEncoder().encodeToString(byteFoto));
            ingredient.setListaImmagini(l);

            //parte nuova
            /*if(ingredientRepository.findByName(ingredient.getName()).size() != 0){
                //c'è almeno un ingrediente nel repository col nome del nuovo ingrediente
                System.out.println(ingredientRepository.findByName(ingredient.getName()).get(0));
                Ingredient first = ingredientRepository.findByName(ingredient.getName()).get(0);
                if(first.getListaImmagini() == null)
                    first.setListaImmagini(new ArrayList<>());
                first.getListaImmagini().add(Base64.getEncoder().encodeToString(byteFoto));
                ingredientRepository.save(first);
            }
            else{
                //Non ci sono ingredienti nel repository col nome del nuovo ingrediente
                List<String> list = new ArrayList<>();
                list.add(Base64.getEncoder().encodeToString(byteFoto));
                ingredient.setListaImmagini(list);
            }*/

            if(ingredientRepository.findByName(ingredient.getName()).size() != 0){
                //c'è almeno un ingrediente nel repository col nome del nuovo ingrediente
                for (Ingredient i : ingredientRepository.findByName(ingredient.getName())){
                    if(i.getListaImmagini() == null){
                        List<String> lista = new ArrayList<>();
                        i.setListaImmagini(lista);
                    }else{
                        List<String> lista = i.getListaImmagini();
                        // lista.add(Base64.getEncoder().encodeToString(byteFoto));
                        ingredient.setListaImmagini(lista);
                    }
                    i.getListaImmagini().add(Base64.getEncoder().encodeToString(byteFoto));
                    ingredientRepository.save(i);
                }
            }

            ingredientRepository.save(ingredient);
            model.addAttribute("message", "Ingredient uploaded successfully!");
            return "redirect:/";

        } catch (IOException e) {

            model.addAttribute("message", "Ingredient upload failed!");
            return "registrationChef.html";

        }
    }

    @GetMapping("/admin/gestisciIngredienti")
    public String getGestioneIngredienti(Model model) {
        List<Ingredient> allIngredients = (List<Ingredient>) ingredientService.findAll();
        Set<Ingredient> uniqueIngredients = new HashSet<>(allIngredients);
        
        model.addAttribute("ingredients", uniqueIngredients);
        return "gestisciIngredienti.html";
    }

   @PostMapping("/admin/deleteIngredient/{id}")
   public String deleteIngredient(@PathVariable Long id, RedirectAttributes redirectAttributes) {
       try {
        //    List<Ingredient> l = ingredientService.findByName(ingredientService.findById(id).getName());
        //    for (Recipe ricetta : recipeService.findAll()) {
        //         for (Ingredient ingredient : ricetta.getIngredients()) {
        //             if(ingredient.getName().equals(ingredientService.findById(id).getName())){
        //                 ricetta.getIngredients().remove(ingredient);
        //             }
        //         }  
        //     }
        //    for (Ingredient  i : l) {
        //         ingredientService.deleteIngredientById(i.getId());
        //    }
        String ingredientName = ingredientService.findById(id).getName();
        List<Ingredient> ingredientList = ingredientService.findByName(ingredientName);
        
        // Rimuovi l'ingrediente da tutte le ricette
        for (Recipe recipe : recipeService.findAll()) {
            // Usa un iteratore per evitare ConcurrentModificationException
            Iterator<Ingredient> iterator = recipe.getIngredients().iterator();
            while (iterator.hasNext()) {
                Ingredient ingredient = iterator.next();
                if (ingredient.getName().equals(ingredientName)) {
                    iterator.remove();
                }
            }
            // Aggiorna la ricetta nel database
            recipeService.save(recipe);
        }
        
        // Elimina tutti gli ingredienti trovati per nome
        for (Ingredient ingredient : ingredientList) {
            ingredientService.deleteIngredientById(ingredient.getId());
        }

           redirectAttributes.addFlashAttribute("successMessage", "Ingrediente eliminato con successo.");
       } catch (Exception e) {
           redirectAttributes.addFlashAttribute("errorMessage", "Errore durante l'eliminazione dell'ingrediente.");
       }
       return "redirect:/admin/gestisciIngredienti";
   }

    @GetMapping("/admin/modificaIngredient")
    public String getModificaIngredientPage(Model model) {
        List<Ingredient> allIngredients = (List<Ingredient>) ingredientService.findAll();
        Set<Ingredient> uniqueIngredients = new HashSet<>(allIngredients);
        model.addAttribute("ingredients", uniqueIngredients);
        return "modificaIngredient.html";
    }

    @GetMapping("/admin/editIngredient/{id}")
    public String showEditIngredientForm(@PathVariable("id") Long id, Model model) {
        Ingredient ingredient = ingredientService.findById(id);
        if (ingredient == null) {
            return "error"; // pagina di errore se la ricetta non esiste
        }
        model.addAttribute("ingredient", ingredient);
        //model.addAttribute("ingredientCount", recipe.getIngredients().size());
        return "editIngredient.html"; // nome della pagina HTML di modifica
    }

    @PostMapping("/admin/updateIngredient")
    public String updateingredient(@ModelAttribute Ingredient updatedIngredient, Model model) {
        // Recupera la ricetta originale dal repository
        Ingredient originalIngredient = ingredientRepository.findById(updatedIngredient.getId())
            .orElseThrow(() -> new IllegalArgumentException("Invalid ingredient Id:" + updatedIngredient.getId()));

        // Aggiorna i campi della ricetta originale con quelli della ricetta modificata
        //originalIngredient.setName(updatedIngredient.getName());
        originalIngredient.setQuantity(updatedIngredient.getQuantity());
        originalIngredient.setUnitOfMeasure(updatedIngredient.getUnitOfMeasure());

        //parte nuova
        List<Ingredient> l = ingredientService.findByName(originalIngredient.getName());
        for (Ingredient i : l) {
            i.setName(updatedIngredient.getName());
            ingredientRepository.save(i);
        }

        // Salva la ricetta aggiornata nel repository
        ingredientRepository.save(originalIngredient);

        // Redirect alla visualizzazione della ricetta modificata
        return "redirect:/ingredient/" + originalIngredient.getId();
    }

}
