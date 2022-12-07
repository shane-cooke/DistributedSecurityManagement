package info;

import java.util.ArrayList;

public class QuotationApplication implements java.io.Serializable{
    public long clientID;
    public UserInfo userInfo;
    public ArrayList<Quotation> quotations = new ArrayList<>();
    public QuotationApplication(long clientID, UserInfo userInfo, ArrayList<Quotation> quotations){
        this.clientID = clientID;
        this.userInfo = userInfo;
        this.quotations=quotations;
    }

    public QuotationApplication(){}

    public void addQuotation(Quotation quotation){
        this.quotations.add(quotation);
    }

    public ArrayList<Quotation> getQuotations() {
        return quotations;
    }
}
