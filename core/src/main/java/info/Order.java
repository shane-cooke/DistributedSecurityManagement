package info;

public class Order {
    public Order(String reference, String trackingNumber, double price) {
        this.reference = reference;
        this.trackingNumber = trackingNumber;
        this.price = price;
    }

    public Order(){}

    private String reference;
    private String trackingNumber;
    private double price;

    public String getReference() {
        return reference;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public double getPrice() {
        return price;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public void setPrice(double price) {
        this.price = price;
    }

}
