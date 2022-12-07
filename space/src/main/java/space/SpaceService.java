package space;

import info.UserInfo;
import info.Order;
import info.Quotation;
import info.TrackingInfo;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.util.*;

@RestController
public class SpaceService {
    public static final String PREFIX = "SPC";
    public static final String COMPANY = "Space Force";
    private final ArrayList<TrackingInfo> trackings = new ArrayList<>();
    private final Map<String, Quotation> quotations = new HashMap<>();
    private final Map<String, Order> orders = new HashMap<>();


    public Quotation generateQuotation(UserInfo info) {
        double price = 15000000;
        int urgency_charge = 0;
        if (info.getUrgency().equals("ASAP")){
            urgency_charge = 10000000;
        }
        else if (info.getUrgency().equals("SOON")){
            urgency_charge = 5000000;
        }
        return new Quotation(COMPANY, generateQuotationReference(), (price + urgency_charge), true, info.getUrgency());
    }

    int counter = 0;
    protected String generateQuotationReference() {
        StringBuilder ref = new StringBuilder(SpaceService.PREFIX);
        int length = 100000;
        while (length > 1000) {
            if (counter / length == 0) ref.append("0");
            length = length / 10;
        }
        return ref.toString() + counter++;
    }


    @RequestMapping(value="/quotations",method= RequestMethod.POST)
    public ResponseEntity<Quotation> createQuotation(@RequestBody UserInfo info){
        Quotation quotation = generateQuotation(info);
        quotations.put(quotation.getReference(), quotation);
        String path = ServletUriComponentsBuilder.fromCurrentContextPath().
                build().toUriString()+ "/quotations/"+quotation.getReference(); HttpHeaders headers = new HttpHeaders();
        try {
            headers.setLocation(new URI(path));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(quotation, headers, HttpStatus.CREATED);
    }

    protected String generateOrderReference() {
        String ref = SpaceService.PREFIX;
        String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder(8);
        for(int i = 0; i < 8; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        ref += sb.toString();
        return ref;
    }

    int count2 = 1000;
    protected String generateTrackingNumber() {
        StringBuilder ref = new StringBuilder(SpaceService.PREFIX);
        ref.append("TRACK");
        int length = 11111111;
        while (length > 1000) {
            if (count2 / length == 0) ref.append("0");
            length = length / 10;
        }
        return ref.toString() + count2++;
    }

    protected Order generateOrder(Quotation quote) {
        String trackingNumber = generateTrackingNumber();
        startTracking(trackingNumber, quote.getUrgency());
        return new Order(generateOrderReference(), trackingNumber, quote.getPrice());
    }

    @RequestMapping(value="/ordering",method= RequestMethod.POST)
    public ResponseEntity<Order> createOrder(@RequestBody Quotation quote){
        Order order = generateOrder(quote);
        orders.put(order.getReference(), order);
        String path = ServletUriComponentsBuilder.fromCurrentContextPath().
                build().toUriString()+ "/ordering/"+order.getReference(); HttpHeaders headers = new HttpHeaders();
        try {
            headers.setLocation(new URI(path));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(order, headers, HttpStatus.CREATED);
    }

    protected void startTracking(String trackingNumber,String urgency) {
        Random r = new Random();

        int time;
        if(urgency.equals("ASAP")) {
            time = r.nextInt(60 - 45) + 45;
        }
        else if(urgency.equals("SOON")){
            time = r.nextInt(120 - 60) + 60;
        }
        else{
            time = r.nextInt(180 - 120) + 120;
        }

        int distance = time*8759872;

        TrackingInfo info = new TrackingInfo(trackingNumber, distance, time);
        trackings.add(info);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                info.setDistance(info.getDistance()-8759872);
                info.setTimeRemaining(info.getTimeRemaining()-1);
            }
        }, 0, 1000);
    }


    @RequestMapping(value="/tracking",method= RequestMethod.POST)
    public ResponseEntity<TrackingInfo> getTrackingInfo(@RequestBody String trackingNumber){

        TrackingInfo infoToReturn = new TrackingInfo();
        for(TrackingInfo info : trackings) {
            if(info.getTrackingNumber().equals(trackingNumber)) {
                infoToReturn=info;
            }
        }
        String path = ServletUriComponentsBuilder.fromCurrentContextPath().
                build().toUriString()+ "/tracking/"+infoToReturn.getTrackingNumber();
        HttpHeaders headers = new HttpHeaders();
        try {
            headers.setLocation(new URI(path));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(infoToReturn, headers, HttpStatus.CREATED);
    }

    @RequestMapping(value="/quotations/{reference}",method=RequestMethod.GET)
    public Quotation getResourceQuoting(@PathVariable("reference") String reference) {
        Quotation quotation = quotations.get(reference);
        if (quotation == null) throw new NoSuchQuotationException();
        return quotation;
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public static class NoSuchQuotationException extends RuntimeException {
        static final long serialVersionUID = -6516152229878843037L;
    }

    @RequestMapping(value="/ordering/{reference}",method=RequestMethod.GET)
    public Order getResourceOrdering(@PathVariable("reference") String reference) {
        Order order = orders.get(reference);
        if (order == null) throw new NoSuchOrderException();
        return order;
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public static class NoSuchOrderException extends RuntimeException {
        static final long serialVersionUID = -6516152229878843037L;
    }

    @RequestMapping(value="/tracking/{reference}",method=RequestMethod.GET)
    public TrackingInfo getResourceTracking(@PathVariable("reference") int reference) {
        TrackingInfo track = trackings.get(reference);
        if (track == null) throw new NoSuchTrackingException();
        return track;
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public static class NoSuchTrackingException extends RuntimeException {
        static final long serialVersionUID = -6516152229878843037L;
    }

}
