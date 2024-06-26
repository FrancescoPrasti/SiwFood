package it.uniroma3.siw.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Recensione {
    
    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Recipe recipe;

    @ManyToOne(fetch = FetchType.LAZY)
    private Chef chef;

    private int valutazione;

    private String contenutoRecensione;

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public int getValutazione() {
        return this.valutazione;
    }

    public void setValutazione(int valutazione) {
        this.valutazione = valutazione;
    }

    public String getContenutoRecensione() {
        return this.contenutoRecensione;
    }

    public void setContenutoRecensione(String contenutoRecensione) {
        this.contenutoRecensione = contenutoRecensione;
    }

    public Chef getChef() {
        return this.chef;
    }

    public void setChef(Chef chef) {
        this.chef = chef;
    }

}
