import info.*;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeMap;

public class Client {

    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_RESET = "\u001B[0m";
    private static final ArrayList<UserInfo> clients = new ArrayList<>();
    private static final TreeMap<Integer, Quotation> cache = new TreeMap<>();

    public static void main(String[] args) {

        displayLogo();

        System.out.println("Welcome to Distributed Security Management!\n");
        String check = "Y";

        while (true) {
            do {
                System.out.println("\nPlease choose one of the following options in order to proceed: \n");
                System.out.println("1) To create a new user profile enter: CREATE");
                System.out.println("2) To create a new order enter:        ORDER");
                System.out.println("3) To track an existing order enter:   TRACK");
                System.out.println("4) To quit the application enter:      QUIT\n");
                Scanner sc = new Scanner(System.in);
                String input = sc.nextLine().toUpperCase();

                if (input.equals("CREATE")) {
                    createUser();
                    displayProfiles(clients);
                } else if (clients.isEmpty() && !input.equals("QUIT")) {
                    System.out.println(ANSI_RED+"\n------------------ No users exist, you must create one first ---------------------"+ANSI_RESET);
                    createUser();
                    displayProfiles(clients);
                } else if (input.equals("TRACK")) {
                    getTracking();
                } else if (input.equals("ORDER")) {
                    System.out.println("\nWhich user would you like to order as? The available user accounts are: ");
                    for (UserInfo client : clients) {
                        System.out.println("-> " + client.getName());
                    }
                    
                    UserInfo clientToUse = null;
                    boolean clientExists=false;
                    while(!clientExists) {
                        String user = sc.nextLine().toUpperCase();
                        for (UserInfo client : clients) {
                            if (client.getName().equals(user)) {
                                clientExists = true;
                                clientToUse=client;
                                break;
                            }
                        }
                        if(!clientExists){
                            System.out.println(ANSI_RED + "------------- Please enter a valid name -------------" + ANSI_RESET);
                        }
                    }
                    getQuotes(clientToUse);
                    order();
                } else if(input.equals("QUIT")) {
                    killApp();
                } else {
                    check = "N";
                    System.out.println(ANSI_RED+"\n*** Wrong input given, please try again. ***\n"+ANSI_RESET);
                }
            } while (check.equalsIgnoreCase("N"));
        }
    }



    public static void createUser() {
        String output = "";
        String name = "";
        String urgency;
        String location;
        do {
            Scanner sc = new Scanner(System.in);
            System.out.println("\nEnter your name: ");
            if(clients.isEmpty()){
                name = sc.nextLine().toUpperCase();
            }
            else {
                boolean clientExists = false;
                while (!clientExists) {
                    name = sc.nextLine().toUpperCase();
                    for (UserInfo client : clients) {
                        if (!client.getName().equals(name)) {
                            clientExists = true;
                        } else {
                            System.out.println(ANSI_RED + "------------ This user exists, please use a different name ------------------" + ANSI_RESET);
                        }
                    }
                }
            }
            System.out.println("Enter the urgency which the job requires (ASAP/SOON/WHENEVER): ");
            urgency = sc.nextLine().toUpperCase();
            System.out.println("Enter the location which the job requires (LAND/SEA/AIR/SPACE): ");
            location = sc.nextLine().toUpperCase();

            System.out.println("\nThe details you entered are:\n");
            System.out.println("Name: " + name);
            System.out.println("Urgency: " + urgency);
            System.out.println("Location: " + location);
            if ((urgency.equals("ASAP") || urgency.equals("SOON") || urgency.equals("WHENEVER")) && (location.equals("AIR") || location.equals("SPACE") || location.equals("SEA") || location.equals("LAND"))) {
                System.out.println("\nAre you satisfied with the details entered? (Y) or (N).");
                boolean correctInput = false;
                while(!correctInput){
                    output = sc.nextLine().toUpperCase();
                    if (!(output.equals("Y") || output.equals("N"))){
                        System.out.println(ANSI_RED+"----------------- Please type either Y or N -------------------"+ANSI_RESET);
                    }
                    else{
                        correctInput=true;
                    }
                }
            } else {
                output = "N";
                System.out.println(ANSI_RED+"\n*** One or more of the inputs given were wrong, try again please. ***\n"+ANSI_RESET);
            }
        } while (output.equalsIgnoreCase("N"));
        clients.add(new UserInfo(name, urgency, location));
        System.out.println(ANSI_BLUE+"\n** New User Created Successfully! **"+ANSI_RESET);
    }


    public static void getQuotes(UserInfo client) {
        int index = 1;
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<UserInfo> request = new HttpEntity<>(client);
        QuotationApplication clientApplication = restTemplate.postForObject("http://localhost:8083/applications", request, QuotationApplication.class);

        displaySingleProfile(client);

        assert clientApplication != null;
        for (Quotation quotation : clientApplication.getQuotations()) {
            if (!quotation.getPossible()) {
                displayNoQuotation(quotation);
            } else {
                System.out.println("\n\n                                                        |===|");
                System.out.println("                                                        | " + index + " |");
                System.out.println("                                                        |===|");
                displayQuotation(quotation);
                cache.put(index, quotation);
                index++;
            }
        }
    }

    public static void order() {

        RestTemplate restTemplate = new RestTemplate();
        Scanner sc = new Scanner(System.in);

        int chosenOrder = 0;
        boolean isQuote = false;
        while(!isQuote) {
            System.out.println("\nEnter the quotation which you would like to order (1/2/3/4): ");
            while(!sc.hasNextInt()) {
                sc.next();
                System.out.println(ANSI_RED+"----------------- Quotation must be a number -------------------"+ANSI_RESET);
            }
            chosenOrder = sc.nextInt();
            if(!cache.descendingKeySet().contains(chosenOrder)){
                System.out.println(ANSI_RED+"----------------- Please choose a valid quote -------------------"+ANSI_RESET);
            }
            else{
                isQuote=true;
            }
        }

        System.out.println(cache.descendingKeySet());

        Quotation quote1 = cache.get(chosenOrder);
        HttpEntity<Quotation> request2 = new HttpEntity<>(quote1);
        OrderApplication orderApplication = restTemplate.postForObject("http://localhost:8084/applications", request2, OrderApplication.class);

        assert orderApplication != null;
        System.out.println(ANSI_BLUE+"\n** New Order Created Successfully! **"+ANSI_RESET);
        for (Order order : orderApplication.getOrders()) {
            displayOrder(order);
        }
    }

    public static void getTracking() {
        Scanner sc = new Scanner(System.in);
        System.out.println("\nEnter the assigned tracking number: ");
        String trackingNumber = sc.nextLine();

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> request = new HttpEntity<>(trackingNumber);
        TrackingApplication trackingApplication = restTemplate.postForObject("http://localhost:8085/applications", request, TrackingApplication.class);
        assert trackingApplication != null;

        if(trackingApplication.getTracking().isEmpty()){
            System.out.println(ANSI_RED +"\n---------------------- Tracking number ("+trackingNumber+") could not be found! --------------------------------"+ANSI_RESET);
        }
        for (TrackingInfo tracking : trackingApplication.getTracking()) {
            if(tracking.getTrackingNumber()!=null) {
                if(tracking.getDistance() > 0 && tracking.getTimeRemaining() > 0) {
                    System.out.println(ANSI_BLUE+"\n** Tracking Reference Found Successfully! **"+ANSI_RESET);
                    displayTracking(tracking);
                }
                else{
                    printArrived(tracking.getTrackingNumber());
                }
            }
        }
    }

    public static void printArrived(String trackingNumber){
        System.out.println(ANSI_GREEN+"\n|=================================================================================================================|"+ANSI_RESET);
        System.out.println(ANSI_GREEN+"|                                  Your order ("+trackingNumber+") has arrived!                                    |"+ANSI_RESET);
        System.out.println(ANSI_GREEN+"|=================================================================================================================|"+ANSI_RESET);
    }

    public static void killApp() {
        System.out.println(ANSI_PURPLE+"\n\nThank you for using Distributed Security Management!"+ANSI_RESET);
        System.out.println(ANSI_PURPLE+"We look forward to seeing you again soon!"+ANSI_RESET);
        displayLogo();
        System.exit(0);
    }

    public static void displayProfiles(ArrayList<UserInfo> info){
        System.out.println("\n|=================================================================================================================|");
        System.out.println("|                                  Distributed Security Management Client Profiles                                |");
        System.out.println("|=================================================================================================================|");
        for (UserInfo userInfo : info) {
            displayProfile(userInfo);
        }
    }

    public static void displayTracking(TrackingInfo tracking) {
        System.out.println("\n\n|=================================================================================================================|");
        System.out.println("|                                  Distributed Security Management Tracking Details                               |");
        System.out.println("|=================================================================================================================|");
        System.out.println("|                                     |                                     |                                     |");
        System.out.println(
                "| Distance (KMs): " + String.format("%1$-20s", tracking.getDistance()) +
                "| Tracking number: " + String.format("%1$-19s", tracking.getTrackingNumber()) +
                "| Time remaining (seconds): " + String.format("%1$-10s", tracking.getTimeRemaining())+"|");
        System.out.println("|                                     |                                     |                                     |");
        System.out.println("|=================================================================================================================|\n\n");
    }


    public static void displayProfile(UserInfo info) {
        System.out.println("|                                     |                                     |                                     |");
        System.out.println(
                "| Name: " + String.format("%1$-29s", info.getName()) +
                        " | Urgency: " + String.format("%1$-26s", (info.getUrgency())) +
                        " | Location: " + String.format("%1$-25s", info.getLocation())+" |");
        System.out.println("|                                     |                                     |                                     |");
        System.out.println("|=================================================================================================================|");
    }

    public static void displaySingleProfile(UserInfo info) {
        System.out.println("\n|=================================================================================================================|");
        System.out.println("|                                  Distributed Security Management Client Profiles                                |");
        System.out.println("|=================================================================================================================|");
        displayProfile(info);
    }

    public static void displayQuotation(Quotation quotation) {
        System.out.println("|=================================================================================================================|");
        System.out.println("|                                    Distributed Security Management Quotation                                    |");
        System.out.println("|=================================================================================================================|");
        System.out.println("|                                     |                                     |                                     |");
        System.out.println(
                "| Company: " + String.format("%1$-26s", quotation.getCompany()) +
                        " | Reference: " + String.format("%1$-24s", quotation.getReference()) +
                        " | Price: " + String.format("%1$-28s", NumberFormat.getCurrencyInstance().format(quotation.getPrice()))+" |");
        System.out.println("|                                     |                                     |                                     |");
        System.out.println("|=================================================================================================================|");
    }

    public static void displayNoQuotation(Quotation quotation) {
        System.out.println("\n\n|=================================================================================================================|");
        System.out.println("|                                       No Quotation Available from: " + String.format("%1$-45s", quotation.getCompany()) + "|");
        System.out.println("|=================================================================================================================|");
    }

    public static void displayOrder(Order order) {
        System.out.println("\n\n|=================================================================================================================|");
        System.out.println("|                                Distributed Security Management Order Reference                                  |");
        System.out.println("|=================================================================================================================|");
        System.out.println("|                       |                                                    |                                    |");
        System.out.println(
                "| Price: " + String.format("%1$-14s", NumberFormat.getCurrencyInstance().format(order.getPrice())) +
                        " | Reference: " + String.format("%1$-39s", order.getReference()) +
                        " | Tracking Number: " + String.format("%1$-17s", order.getTrackingNumber())+" |");
        System.out.println("|                       |                                                    |                                    |");
        System.out.println("|=================================================================================================================|\n\n");
    }

    public static void displayLogo() {
        System.out.println(ANSI_RED + "\n\n ______    "+ANSI_BLUE+"  ______    "+ANSI_GREEN+"____    ____  " + ANSI_RESET);
        System.out.println(ANSI_RED + "|_   _ `.  "+ANSI_BLUE+".' ____ \\  "+ANSI_GREEN+"|_   \\  /   _| " + ANSI_RESET);
        System.out.println(ANSI_RED + "  | | `. \\"+ANSI_BLUE+" | (___ \\_|"+ANSI_GREEN+"   |   \\/   |   " + ANSI_RESET);
        System.out.println(ANSI_RED + "  | |  | | "+ANSI_BLUE+" _.____`.   "+ANSI_GREEN+" | |\\  /| |   " + ANSI_RESET);
        System.out.println(ANSI_RED + " _| |_.' / "+ANSI_BLUE+"| \\____) | "+ANSI_GREEN+" _| |_\\/_| |_  " + ANSI_RESET);
        System.out.println(ANSI_RED + "|______.'  "+ANSI_BLUE+" \\______.' "+ANSI_GREEN+"|_____||_____| " + ANSI_RESET);
        System.out.println(ANSI_PURPLE + "------------------------------------\n\n" + ANSI_RESET);
    }



}

