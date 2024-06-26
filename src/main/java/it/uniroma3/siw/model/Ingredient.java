package it.uniroma3.siw.model;

import jakarta.persistence.*;

import java.util.List;
import java.util.Map;


@Entity
public class Ingredient {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String name;

    private Integer quantity;

    private String unitOfMeasure;

    @Column(length = 100000000)
    private String base64;

    public String getBase64() {
        return base64;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }

    @Column(length = 100000000)
    private List<String> listaImmagini;

    public List<String> getListaImmagini() {
        return this.listaImmagini;
    }

    public void setListaImmagini(List<String> listaImmagini) {
        this.listaImmagini = listaImmagini;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    private Chef chef;

    /*@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Recipe> recipes;*/

    public Chef getChef() {
        return chef;
    }

    public void setChef(Chef chef) {
        this.chef = chef;
    }

    public Long getId() {
        return id;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ingredient that = (Ingredient) o;
        return this.getName().equals(that.getName());
    }

    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }    

}
