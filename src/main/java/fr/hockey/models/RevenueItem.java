package fr.hockey.models;

public class RevenueItem {
    private String category;
    private int paidCount;
    private double paidTotal;
    private int unpaidCount;
    private double unpaidTotal;

    public RevenueItem(String category, int paidCount, double paidTotal, int unpaidCount, double unpaidTotal) {
        this.category = category;
        this.paidCount = paidCount;
        this.paidTotal = paidTotal;
        this.unpaidCount = unpaidCount;
        this.unpaidTotal = unpaidTotal;
    }

    public String getCategory() { return category; }
    public int getPaidCount() { return paidCount; }
    public double getPaidTotal() { return paidTotal; }
    public int getUnpaidCount() { return unpaidCount; }
    public double getUnpaidTotal() { return unpaidTotal; }
    public double getExpectedTotal() { return paidTotal + unpaidTotal; }
}