package order;

import info.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
@RestController
public class OrderService {

    public static HashMap<Integer, OrderApplication> map = new HashMap();
    public static int clientNumber = 0;

    @RequestMapping(value="/applications",method = RequestMethod.POST)
    public OrderApplication makeOrder(@RequestBody Quotation quote, UserInfo info){
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<Quotation> request = new HttpEntity<>(quote);
        ArrayList<Order> orders = new ArrayList<>();

        if(quote.getCompany().equals("Royal Air Force")){
            orders.add(restTemplate.postForObject("http://air:8080/ordering", request, Order.class));
        }
        if(quote.getCompany().equals("Navy Seals")){
            orders.add(restTemplate.postForObject("http://sea:8081/ordering", request, Order.class));
        }
        if(quote.getCompany().equals("Army")){
            orders.add(restTemplate.postForObject("http://ground:8082/ordering", request, Order.class));
        }
        if(quote.getCompany().equals("Space Force")){
            orders.add(restTemplate.postForObject("http://space:8089/ordering", request, Order.class));
        }
        OrderApplication orderApplication = new OrderApplication(clientNumber, info, orders);
        map.put(clientNumber, orderApplication);
        return orderApplication;
    }

    @RequestMapping(value="applications/{clientNumber}",method=RequestMethod.GET)
    public OrderApplication getResource() {
        if (map == null) throw new NoSuchOrderException(); return map.get(clientNumber);
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public static class NoSuchOrderException extends RuntimeException {
        static final long serialVersionUID = -6516152229878843037L;
    }

    @RequestMapping(value="/applications",method=RequestMethod.GET)
    public ArrayList<OrderApplication> listApplications() {
        return new ArrayList<>(map.values());
    }
}
