package fr.hockey.models;

public class CategoryFee {
    private int id;
    private String category;
    private double fee;

    public CategoryFee() {
    }

    public CategoryFee(int id, String category, double fee) {
        this.id = id;
        this.category = category;
        this.fee = fee;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }
}