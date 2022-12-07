package track;

import info.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
@RestController
public class TrackingService {

    public static HashMap<Integer, TrackingApplication> map = new HashMap();
    public static int clientNumber = 0;

    @RequestMapping(value="/applications",method = RequestMethod.POST)
    public TrackingApplication getTrackingInfo(@RequestBody String trackingNumber){
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> request = new HttpEntity<>(trackingNumber);
        ArrayList<TrackingInfo> trackingInfos = new ArrayList<>();

        trackingInfos.add(restTemplate.postForObject("http://air:8080/tracking", request, TrackingInfo.class));
        trackingInfos.add(restTemplate.postForObject("http://sea:8081/tracking", request, TrackingInfo.class));
        trackingInfos.add(restTemplate.postForObject("http://ground:8082/tracking", request, TrackingInfo.class));
        trackingInfos.add(restTemplate.postForObject("http://space:8089/tracking", request, TrackingInfo.class));

        TrackingApplication trackingApplication = new TrackingApplication(trackingInfos);
        map.put(clientNumber, trackingApplication);
        return trackingApplication;
    }

    @RequestMapping(value="applications/{clientNumber}",method=RequestMethod.GET)
    public TrackingApplication getResource() {
        if (map == null) throw new NoSuchTrackingException(); return map.get(clientNumber);
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public static class NoSuchTrackingException extends RuntimeException {
        static final long serialVersionUID = -6516152229878843037L;
    }

    @RequestMapping(value="/applications",method=RequestMethod.GET)
    public ArrayList<TrackingApplication> listApplications() {
        return new ArrayList<>(map.values());
    }
}
