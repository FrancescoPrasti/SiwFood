package it.uniroma3.siw.controller;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Ingredient;
import it.uniroma3.siw.model.Admin;
import it.uniroma3.siw.model.Chef;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.AdminService;
import it.uniroma3.siw.service.ChefService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


@Controller
public class AuthenticationController {

   @Autowired
   private CredentialsService credentialsService;

   @Autowired
   private ChefService chefService;

   @Autowired
   private AdminService adminService;

   @GetMapping("/")
    public String getIndex(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication instanceof AnonymousAuthenticationToken) {
	        return "index.html";
		}else{
            UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
			if (credentials.getRole().equals("ADMIN")) {
				return "adminHome.html";
			}else{
                model.addAttribute("chefId", (Long) model.getAttribute("userId"));
                return "chefHome.html";
            }

        }
    }
   
   @GetMapping("/login")
   public String getLoginForm() {
       return "login.html";
   }   

   @GetMapping("/registrationChef")
   public String getRegistrationForm(Model model) {
       model.addAttribute("credentials", new Credentials());
       model.addAttribute("chef", new Chef());
       return "registrationChef.html";
   }

   @GetMapping("/admin/registration")
   public String getRegistrationFormAdmin(Model model) {
       model.addAttribute("credentials", new Credentials());
       model.addAttribute("chef", new Chef());
       return "registration.html";
   }

   @PostMapping("/admin/registration")
   public String userRegistration(@ModelAttribute("chef") Chef chef, @ModelAttribute("credentials") Credentials credentials, @RequestParam("image") MultipartFile file, Model model){
        try {

            System.out.println(credentials.getRole());

            if(credentials.getRole().equals("ADMIN")){

                System.out.println("ao qui ci sono entrato!");
                //assegno i valori di "chef" a un nuovo oggetto Admin e poi lo salvo
                Admin admin = new Admin();
                admin.setName(chef.getName());
                admin.setSurname(chef.getSurname());
                admin.setDateOfBirth(chef.getDateOfBirth());
                admin.setEmail(chef.getEmail());
                admin.setId(chef.getId());
                credentials.setAdmin(admin);
                adminService.save(admin);

            }else{
                
                byte[] byteFoto = file.isEmpty() ? new byte[0] : file.getBytes();
                //byte[] byteFoto = file.getBytes();
                chef.setBase64(Base64.getEncoder().encodeToString(byteFoto));

                //credentials.setRole("CHEF");
                chefService.save(chef);
                credentials.setChef(chef);

            }

            credentialsService.saveCredentials(credentials);
            model.addAttribute("message", "Chef uploaded successfully!");

            //System.out.println(chefService.findById(chef.getId()).getName());
            return "redirect:/";
        } catch (IOException e) {
            model.addAttribute("message", "Chef upload failed!");
            return "registration.html";
        }
   }


   @PostMapping("/registrationChef")
   public String chefRegistration(@ModelAttribute("chef") Chef chef, @ModelAttribute("credentials") Credentials credentials, @RequestParam("image") MultipartFile file, Model model){
        try {
    
            byte[] byteFoto = file.getBytes();
            chef.setBase64(Base64.getEncoder().encodeToString(byteFoto));

            credentials.setRole("CHEF");
            chefService.save(chef);
            credentials.setChef(chef);

            credentialsService.saveCredentials(credentials);
            model.addAttribute("message", "Chef uploaded successfully!");

            return "redirect:/login";
        } catch (IOException e) {
            model.addAttribute("message", "Chef upload failed!");
            return "registrationChef.html";
        }
   }
   
    
}
