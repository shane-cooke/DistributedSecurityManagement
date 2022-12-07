package info;

public class UserInfo {

    public UserInfo(String name, String urgency, String location) {
        this.name = name;
        this.urgency = urgency;
        this.location = location;
    }

    public UserInfo() {}

    private String name;
    private String urgency;
    private String location;

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public String getUrgency() {return urgency;}

    public void setUrgency(String urgency) {this.urgency = urgency;}

    public String getLocation() {return location;}

    public void setLocation(String location) {this.location = location;}

}
