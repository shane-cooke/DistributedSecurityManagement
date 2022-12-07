package info;

public class Quotation {
    public Quotation(String company, String reference, double price, boolean possible, String urgency) {
        this.company = company;
        this.reference = reference;
        this.price = price;
        this.possible = possible;
        this.urgency = urgency;

    }

    public Quotation(){}

    private String company;
    private String reference;
    private double price;
    private boolean possible;
    private String urgency;


    public boolean isPossible() {
        return possible;
    }

    public void setPossible(boolean possible) {
        this.possible = possible;
    }

    public String getUrgency() {
        return urgency;
    }

    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }

    public boolean getPossible() {
        return possible;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
