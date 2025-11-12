package fr.hockey.models;

import java.time.LocalDate;

public class License {
    private int id;
    private int playerId;
    private boolean paid;
    private LocalDate expirationDate;
    private double amount;

    public License() {
    }

    public License(int id, int playerId, boolean paid, LocalDate expirationDate, double amount) {
        this.id = id;
        this.playerId = playerId;
        this.paid = paid;
        this.expirationDate = expirationDate;
        this.amount = amount;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}