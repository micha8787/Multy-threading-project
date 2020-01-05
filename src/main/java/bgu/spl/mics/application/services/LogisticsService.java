package bgu.spl.mics.application.services;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import bgu.spl.mics.Future;
import bgu.spl.mics.Message;
import bgu.spl.mics.MicroService;

import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;
//import javafx.util.Pair;

/**
 * Logistic service in charge of delivering books that have been purchased to customers.
 * Handles {@link DeliveryEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LogisticsService extends MicroService
{
    BlockingQueue<Future<DeliveryVehicle>> vehicles = new LinkedBlockingQueue<>();
   // ConcurrentHashMap<DeliveryEvent, Future<Future<DeliveryVehicle>>> deliveryEventsByFutures = new ConcurrentHashMap<>();



    int currentTick;

    public LogisticsService(String name)
    {
        super(name);

    }


    @Override
    protected void initialize()
    {
        this.subscribeBroadcast(TickBroadcast.class, tickBroadcast ->
        {
            this.currentTick = tickBroadcast.getCurrentTick();



            /*Iterator it = deliveryEventsByFutures.entrySet().iterator();
            while(it.hasNext())
            {
                Map.Entry pair = (Map.Entry)it.next();
                if(((Future<Future<DeliveryVehicle>>) pair.getValue()).get().isDone())
                {
                    DeliveryVehicle vehicle = ((Future<Future<DeliveryVehicle>>) pair.getValue()).get().get();
                    String adress =((DeliveryEvent) pair.getKey()).getAdress();
                    int distance = ((DeliveryEvent) pair.getKey()).getDistance();
                    vehicle.deliver(adress,distance);
                    sendEvent(new ReturningVehicle(vehicle.getLicense(),vehicle.getSpeed()));
                    complete((DeliveryEvent) pair.getKey(), null);
                    it.remove();
                }
            }*/


              });


            this.subscribeBroadcast(TerminateBroadcast.class, terminateBroadcast ->
            {
                for(Future<DeliveryVehicle> vc : vehicles)
                {
                    vehicles.remove(vc);

                }
                this.terminate();
            });


            this.subscribeEvent(DeliveryEvent.class, deliveryEvent ->
            {
                Future<Future<DeliveryVehicle>> futureDeliveryVehicle = sendEvent(new AcquireAndDeliver());

                if(futureDeliveryVehicle != null && futureDeliveryVehicle.get() != null)
                {
                    DeliveryVehicle vehicle = futureDeliveryVehicle.get().get();
                    if(vehicle!= null)
                    {
                        vehicle.deliver(deliveryEvent.getAdress(), deliveryEvent.getDistance());
                        sendEvent(new ReturningVehicle(vehicle));
                        complete(deliveryEvent, null);
                    }
                }




            });
        }
}




