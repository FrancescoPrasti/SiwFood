package it.uniroma3.siw.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String description;
    
    private int prepTime; // Tempo di preparazione
        
    private int servings; // Porzioni
    
    private String difficulty; // Difficoltà
    
    private String category;

    @Column(length = 100000000)
    private List<String> base64;

    @ManyToOne(fetch = FetchType.LAZY)
    private Chef chef;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})//, fetch = FetchType.EAGER)
    private List<Ingredient> ingredients;

    @ManyToMany(mappedBy = "ricettePreferite")
    private List<Chef> chefsWhoFavorited;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}) //mappedBy = "recipe", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    private List<Recensione> recensioni;

    public List<Recensione> getRecensioni() {
        return recensioni;
    }

    public void setRecensioni(List<Recensione> recensioni) {
        this.recensioni = recensioni;
    }

    public List<Chef> getChefsWhoFavorited() {
        return chefsWhoFavorited;
    }

    public void setChefsWhoFavorited(List<Chef> chefsWhoFavorited) {
        this.chefsWhoFavorited = chefsWhoFavorited;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public void addIngredient(Ingredient ingredient) {
        getIngredients().add(ingredient);
    }

    public List<String> getBase64() {
        return this.base64;
    }

    public void setBase64(List<String> base64) {
        this.base64 = base64;
    }

    public void setId(Long id){
        this.id = id;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public Long getId(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }

    public String getDescription(){
        return this.description;
    }

    @Override
    public boolean equals(Object o){
        Recipe r = (Recipe)o;
        return r.getName().equals(this.getName()) && r.getDescription().equals(this.getDescription());
    }

    @Override
    public int hashCode(){
        return this.getName().hashCode() + this.getDescription().hashCode();
    }

    public Chef getChef() {
        return chef;
    }

    public void setChef(Chef chef) {
        this.chef = chef;
    }

    public int getPrepTime() {
        return prepTime;
    }

    public void setPrepTime(int prepTime) {
        this.prepTime = prepTime;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

}
