package bgu.spl.mics.application;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import java.io.*;
import java.sql.SQLOutput;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner
{


    public static void main(String[]args)
    {


        String inputFilePath =  args[0]; //im done !!
        Gson gson = new Gson();
        HashMap<Integer,Customer> customersMap = null;

        try
        {
            JsonReader read = new JsonReader(new FileReader(inputFilePath));
            JsonObject obj = gson.fromJson(read, JsonObject.class);
            Inventory inv = Inventory.getInstance();
            BookInventoryInfo[] books = makeInventory(obj);
            inv.load(books);

            //Making ResourceHolder
            ResourcesHolder resourcesHolder = ResourcesHolder.getInstance();
            DeliveryVehicle[] vehicles = makeVehicles(obj);
            resourcesHolder.load(vehicles);

            //Making MicroServices
            BlockingQueue<MicroService> microServicesThreads = makeMicroServices(obj);
            BlockingQueue<Thread> microServicesTryJoin = new LinkedBlockingQueue<>();
            //Making Customers
            customersMap = makingCustomerAndApi(obj, microServicesThreads);

            while(!microServicesThreads.isEmpty())
            {
                MicroService microService = microServicesThreads.poll();
                Thread thread = new Thread (microService);
                microServicesTryJoin.add(thread);
                thread.start();
            }


            startTimeService(obj);

            for(Thread t : microServicesTryJoin)
            {

                t.join();

            }






        }

        catch(FileNotFoundException exc) {

        }
        catch (InterruptedException exc){ }


        //print customers map:
        try
        {

            FileOutputStream fO = new FileOutputStream(args[1]);
            ObjectOutputStream out = new ObjectOutputStream(fO);
            out.writeObject(customersMap);
            out.close();
            fO.close();
        }
        catch (IOException ex)
        {

        }
        //prints books
        Inventory.getInstance().printInventoryToFile(args[2]);

        //print receipts
        MoneyRegister.getInstance().printOrderReceipts(args[3]);


        //print MoneyRegister

        try
        {
            FileOutputStream fileOut = new FileOutputStream(args[4]);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(MoneyRegister.getInstance());
            out.close();
            fileOut.close();
        }
        catch (IOException i)
        {

        }
    }







    private static BookInventoryInfo[] makeInventory(JsonObject obj)
    {
        JsonArray inventoryFromJson = obj.getAsJsonArray("initialInventory");
        BookInventoryInfo[] books = new BookInventoryInfo[inventoryFromJson.size()];

        int counter = 0;
        for(JsonElement elem : inventoryFromJson)
        {
            String bookTitle = elem.getAsJsonObject().get("bookTitle").getAsString();
            int amountInInventory = elem.getAsJsonObject().get("amount").getAsInt();
            int priceOfBook = elem.getAsJsonObject().get("price").getAsInt();
            books[counter] = new BookInventoryInfo(bookTitle, priceOfBook, amountInInventory );
            counter = counter + 1;
        }
        return books;
    }

    private static DeliveryVehicle[] makeVehicles(JsonObject obj)
    {
        final int firstElement = 0;
        JsonArray resourcesFromJson = obj.getAsJsonArray("initialResources");
        JsonObject jsonVehiclesArr = (JsonObject) resourcesFromJson.get(firstElement);
        JsonArray vehiclesAsJson = jsonVehiclesArr.get("vehicles").getAsJsonArray();
        DeliveryVehicle[] vehicles = new DeliveryVehicle[vehiclesAsJson.size()];
        int counter = 0;
        for(JsonElement elem : vehiclesAsJson)
        {
            int license = elem.getAsJsonObject().get("license").getAsInt();
            int speed = elem.getAsJsonObject().get("speed").getAsInt();
            vehicles[counter] = new DeliveryVehicle(license, speed);
            counter = counter + 1;
        }

        return vehicles;
    }

    private static BlockingQueue<MicroService> makeMicroServices(JsonObject obj)
    {
        BlockingQueue<MicroService> microServicesThreads = new LinkedBlockingQueue<>();
        JsonObject microServicesFromJson = obj.get("services").getAsJsonObject();


        //InventoryServices
        int amountOfInvServices = microServicesFromJson.get("inventoryService").getAsInt();

        for(int i = 0; i < amountOfInvServices; i = i + 1)
        {
            String name = String.valueOf(i);
            InventoryService inventoryService = new InventoryService(name);
            microServicesThreads.add(inventoryService);

        }

        //SellingServices
        int amountOfSellingServices = microServicesFromJson.get("selling").getAsInt();

        for(int i = 0; i < amountOfSellingServices; i = i + 1)
        {
            String name = String.valueOf(i);
            SellingService sellingService = new SellingService(name);
            microServicesThreads.add(sellingService);
        }

        //ResourcesServices
        int amountOfResourcesServices = microServicesFromJson.get("resourcesService").getAsInt();

        for(int i = 0; i < amountOfResourcesServices; i = i + 1)
        {
            String name = String.valueOf(i);
            ResourceService resourceService = new ResourceService(name);
            microServicesThreads.add(resourceService);
        }

        //LogisticsServices
        int amountOfLogisticsServices = microServicesFromJson.get("logistics").getAsInt();

        for(int i = 0; i < amountOfLogisticsServices; i = i + 1)
        {
            String name = String.valueOf(i);
            LogisticsService logisticService = new LogisticsService(name);
            microServicesThreads.add(logisticService);
        }


        return microServicesThreads;
    }

    private static HashMap<Integer, Customer> makingCustomerAndApi(JsonObject object, BlockingQueue<MicroService> microServicesThreads)
    {

        JsonObject microServicesFromJson = object.get("services").getAsJsonObject();
        HashMap<Integer, Customer> customersMap = new HashMap<>();
        BlockingQueue<MicroService> microServicesThreadsWithApi = microServicesThreads;
        JsonArray customersFromJson = microServicesFromJson.get("customers").getAsJsonArray();

        for(JsonElement elem : customersFromJson)
        {
            int customerId = elem.getAsJsonObject().get("id").getAsInt();
            String customerName = elem.getAsJsonObject().get("name").getAsString();
            String customerAdress = elem.getAsJsonObject().get("address").getAsString();
            int customerDistanceFromStore = elem.getAsJsonObject().get("distance").getAsInt();
            JsonObject customersCreditCard = elem.getAsJsonObject().get("creditCard").getAsJsonObject();
            int customersCerditCardNumber = customersCreditCard.get("number").getAsInt();
            int customersAmount = customersCreditCard.get("amount").getAsInt();



            ConcurrentHashMap<Integer, Vector<String>> customersOrderScheduele = new ConcurrentHashMap<>();

            JsonArray booksToOrder = elem.getAsJsonObject().get("orderSchedule").getAsJsonArray();

            for(JsonElement orderSchedueleElement: booksToOrder)
            {
                int tick = orderSchedueleElement.getAsJsonObject().get("tick").getAsInt();

                if(!customersOrderScheduele.containsKey(tick))
                {
                    Vector<String> 	booksForCustomersOrderScheduele = new Vector<>();
                    String bookTitle = orderSchedueleElement.getAsJsonObject().get("bookTitle").getAsString();
                    booksForCustomersOrderScheduele.add(bookTitle);
                    customersOrderScheduele.put(tick, booksForCustomersOrderScheduele);


                }
                else
                {

                    String bookTitle = orderSchedueleElement.getAsJsonObject().get("bookTitle").getAsString();
                    customersOrderScheduele.get(tick).add(bookTitle);

                }

            }

            Customer customer = new Customer(customerId,customerName,customerAdress,customerDistanceFromStore,customersCerditCardNumber,customersAmount,customersOrderScheduele);
            customersMap.put(customerId, customer);

            APIService apiService = new APIService(customer);
            microServicesThreadsWithApi.add(apiService);
        }
        return customersMap;
    }

    private static void startTimeService(JsonObject object)
    {
        JsonObject microServicesFromJson = object.get("services").getAsJsonObject();
        JsonObject timeServiceAsJson = microServicesFromJson.getAsJsonObject().get("time").getAsJsonObject();
        int speedTime = timeServiceAsJson.get("speed").getAsInt();
        int durationTime = timeServiceAsJson.get("duration").getAsInt();
        TimeService timeService = new TimeService (durationTime, speedTime);
        Thread timeServiceThread = new Thread(timeService);
        timeServiceThread.start();

    }

}

