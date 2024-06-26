package it.uniroma3.siw.model;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
public class Chef {

    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
    private String name;
	private String surname;
    private LocalDate dateOfBirth;
    private String email;
    
    @Column(length = 100000000)
    private String base64;

    @OneToMany(mappedBy = "chef", cascade = CascadeType.REMOVE)
    private List<Recipe> recipes;

    @OneToMany(mappedBy = "chef", cascade = CascadeType.REMOVE)
    private List<Ingredient> ingredients;

    @ManyToMany
    private List<Recipe> ricettePreferite;

    public List<Recipe> getRicettePreferite() {
        return ricettePreferite;
    }

    public void setRicettePreferite(List<Recipe> ricettePreferite) {
        this.ricettePreferite = ricettePreferite;
    }

    public Long getId() {
		return this.id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBase64() {
        return this.base64;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }

    public List<Recipe> getRecipesCreated() {
        return recipes;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }
	
	@Override
	public boolean equals(Object o) {
		Chef a = (Chef)o;
		return this.email.equals(a.getEmail());
	}
	
	@Override
	public int hashCode() {
		return this.email.hashCode();
	}    
}
