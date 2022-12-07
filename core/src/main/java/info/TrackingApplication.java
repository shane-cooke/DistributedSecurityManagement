package info;

import java.util.ArrayList;

public class TrackingApplication implements java.io.Serializable{
    public ArrayList<TrackingInfo> tracking = new ArrayList<>();
    public TrackingApplication(ArrayList<TrackingInfo> tracking){
        this.tracking=tracking;
    }

    public TrackingApplication(){}

    public void addTracking(TrackingInfo trackingInfo){
        this.tracking.add(trackingInfo);
    }

    public ArrayList<TrackingInfo> getTracking() {
        return tracking;
    }
}