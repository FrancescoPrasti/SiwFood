package it.uniroma3.siw.controller;

import java.io.IOException;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Ingredient;
import it.uniroma3.siw.model.Recipe;
import it.uniroma3.siw.model.Chef;
import it.uniroma3.siw.repository.CredentialsRepository;
import it.uniroma3.siw.repository.ChefRepository;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.ChefService;

@Controller
public class ChefController {

    @Autowired
    private ChefService chefService;

    @Autowired
    private ChefRepository chefRepository;

    @Autowired
    private CredentialsService credentialsService;

    //@Autowired
    //private CredentialsRepository credentialsRepository;
	
	@GetMapping("/chef/{id}")
	public String getChef(@PathVariable("id") Long id, Model model) {
		model.addAttribute("chef", this.chefService.findById(id));
        model.addAttribute("username", this.credentialsService.getCredentials(this.chefService.findById(id)).getUsername());

        if(model.getAttribute("userId")==null)
            model.addAttribute("role", "DEFAULT");
        else
            model.addAttribute("role", credentialsService.getCredentials((Long) model.getAttribute("userId")).getRole());

		return "chef.html";
	}

	@GetMapping("/chefs")
	public String getChefs(Model model) {
        List<Chef> chefs = (List<Chef>) chefService.findAll();
        chefs.sort(Comparator.comparing(Chef::getName));
        
		model.addAttribute("chefs", chefs);

        if(model.getAttribute("userId")==null)
            model.addAttribute("role", "DEFAULT");
        else
            model.addAttribute("role", credentialsService.getCredentials((Long) model.getAttribute("userId")).getRole());

		return "chefs.html";
	}

    @GetMapping("/admin/modificaChef")
    public String getModificaChefPage(Model model) {
        model.addAttribute("chefs", this.chefService.findAll());
        return "modificaChef.html";
    }

    @GetMapping("/editChef/{id}")
    public String showEditChefForm(@PathVariable("id") Long id, Model model) {
        Chef chef = chefService.findById(id);
        if (chef == null) {
            return "error"; // pagina di errore se la ricetta non esiste
        }
        model.addAttribute("chef", chef);
        model.addAttribute("username", this.credentialsService.getCredentials(chef).getUsername());
        model.addAttribute("password", this.credentialsService.getCredentials(chef).getPassword());
        //model.addAttribute("ingredientCount", recipe.getIngredients().size());
        return "editChef.html"; // nome della pagina HTML di modifica
    }

    // Metodo per gestire la modifica della ricetta
    @PostMapping("/updateChef")
    public String updateChef(@ModelAttribute Chef updatedChef, @RequestParam("image") MultipartFile imageFile) {
        // Recupera lo chef originale dal repository
        Chef originalChef = chefRepository.findById(updatedChef.getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid chef Id:" + updatedChef.getId()));

        // Aggiorna i campi dello chef originale con quelli dello chef modificato
        originalChef.setName(updatedChef.getName());
        originalChef.setSurname(updatedChef.getSurname());
        originalChef.setEmail(updatedChef.getEmail());
        originalChef.setDateOfBirth(updatedChef.getDateOfBirth());

        // Carica una nuova immagine dello chef, se fornita
        if (!imageFile.isEmpty()) {
            try {
                //byte[] imageData = imageFile.getBytes();
                String base64Image = Base64.getEncoder().encodeToString(imageFile.getBytes());
                // Salva l'immagine nel modello di dati dello chef
                originalChef.setBase64(base64Image);
            } catch (IOException e) {
                // Gestisci eventuali errori di caricamento dell'immagine
                e.printStackTrace();
            }
        }

        // Salva lo chef aggiornato nel repository
        chefRepository.save(originalChef);

        // Redirect alla visualizzazione dello chef modificato
        return "redirect:/chef/" + originalChef.getId();
    }

    @PostMapping("/admin/deleteChef/{id}")
    public String deleteChef(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            System.out.println("Qua siiiii");
            credentialsService.deleteCredentialsById(credentialsService.getCredentials(chefService.findById(id)).getId());
            chefService.deleteChefById(id);

            redirectAttributes.addFlashAttribute("successMessage", "Cuoco eliminato con successo.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Errore durante l'eliminazione del cuoco.");
        }
        return "redirect:/admin/modificaChef";
    }    
    
}
