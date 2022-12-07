package info;


public class TrackingInfo implements java.io.Serializable{
    private String trackingNumber;
    private int distance;
    private int timeRemaining;
    public TrackingInfo(String trackingNumber, int distance, int timeRemaining){
        this.trackingNumber = trackingNumber;
        this.distance = distance;
        this.timeRemaining=timeRemaining;
    }

    public TrackingInfo(){}

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getTimeRemaining() {
        return timeRemaining;
    }

    public void setTimeRemaining(int timeRemaining) {
        this.timeRemaining = timeRemaining;
    }
}