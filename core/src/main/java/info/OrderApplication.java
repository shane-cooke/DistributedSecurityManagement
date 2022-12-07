package info;

import java.util.ArrayList;

public class OrderApplication implements java.io.Serializable{

    public OrderApplication(long clientID, UserInfo userInfo, ArrayList<Order> orders){
        this.clientID = clientID;
        this.userInfo = userInfo;
        this.orders=orders;
    }

    public OrderApplication(){}

    private long clientID;
    private UserInfo userInfo;
    private ArrayList<Order> orders = new ArrayList<>();

    public long getClientID() {
        return clientID;
    }

    public void setClientID(long clientID) {
        this.clientID = clientID;
    }

    public UserInfo getClientInfo() {
        return userInfo;
    }

    public void setClientInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public void setOrders(ArrayList<Order> orders) {
        this.orders = orders;
    }

    public void addOrders(Order order){
        this.orders.add(order);
    }
    public ArrayList<Order> getOrders() {
        return orders;
    }

}
