package quote;

import info.QuotationApplication;
import info.UserInfo;
import info.Quotation;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
@RestController
public class QuotationBroker {

    public static HashMap<Integer, QuotationApplication> map = new HashMap();
    public static int clientNumber = 0;

    @RequestMapping(value="/applications",method = RequestMethod.POST)
    public QuotationApplication getQuotations(@RequestBody UserInfo info){
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<UserInfo> request = new HttpEntity<>(info);
        ArrayList<Quotation> quotations = new ArrayList<>();

        quotations.add(restTemplate.postForObject("http://air:8080/quotations", request, Quotation.class));
        quotations.add(restTemplate.postForObject("http://sea:8081/quotations", request, Quotation.class));
        quotations.add(restTemplate.postForObject("http://ground:8082/quotations", request, Quotation.class));
        quotations.add(restTemplate.postForObject("http://space:8089/quotations", request, Quotation.class));

        QuotationApplication quotationApplication = new QuotationApplication(clientNumber, info, quotations);
        map.put(clientNumber, quotationApplication);
        clientNumber++;
        return quotationApplication;
    }

    @RequestMapping(value="applications/{clientNumber}",method=RequestMethod.GET)
    public QuotationApplication getResource() {
        if (map == null) throw new NoSuchQuotationException(); return map.get(clientNumber);
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public static class NoSuchQuotationException extends RuntimeException {
        static final long serialVersionUID = -6516152229878843037L;
    }

    @RequestMapping(value="/applications",method=RequestMethod.GET)
    public ArrayList<QuotationApplication> listApplications() {
        return new ArrayList<>(map.values());
    }
}
