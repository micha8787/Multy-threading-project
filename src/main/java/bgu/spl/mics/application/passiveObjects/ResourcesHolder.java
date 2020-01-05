package bgu.spl.mics.application.passiveObjects;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import bgu.spl.mics.Future;
import bgu.spl.mics.MessageBusImpl;

/**
 * Passive object representing the resource manager.
 * You must not alter any of the given public methods of this class.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class ResourcesHolder
{
    private BlockingQueue<DeliveryVehicle> deliveryVehicles = new LinkedBlockingQueue<>();
    private BlockingQueue <Future<DeliveryVehicle>> futureVehicles = new LinkedBlockingQueue<>();

    private static class SingeltonHolder
    {
        private static ResourcesHolder INSTANCE = new ResourcesHolder();
    }



    /**
     * Retrieves the single instance of this class.
     */
    public static ResourcesHolder getInstance()
    {
        return SingeltonHolder.INSTANCE;
    }

    /**
     * Tries to acquire a vehicle and gives a future object which will
     * resolve to a vehicle.
     * <p>
     * @return 	{@link Future<DeliveryVehicle>} object which will resolve to a
     * 			{@link DeliveryVehicle} when completed.
     */
    public Future<DeliveryVehicle> acquireVehicle()
    {
        Future<DeliveryVehicle> future = new Future<>();
        DeliveryVehicle deliveryVehicle = deliveryVehicles.poll();

        if(deliveryVehicle != null )
        {
            future.resolve(deliveryVehicle);

        }
        else
        {
            futureVehicles.add(future);
        }
        return future;



    }

    /**
     * Releases a specified vehicle, opening it again for the possibility of
     * acquisition.
     * <p>s
     * @param vehicle	{@link DeliveryVehicle} to be released.
     */
    public void releaseVehicle(DeliveryVehicle vehicle)
    {
        Future<DeliveryVehicle> future = futureVehicles.poll();

        if(future != null)
        {
            future.resolve(vehicle);
        }
        else
        {
            deliveryVehicles.add(vehicle);
        }






    }

    /**
     * Receives a collection of vehicles and stores them.
     * <p>
     * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
     */
    public  void  load(DeliveryVehicle[] vehicles)
    {
        for (DeliveryVehicle del:vehicles)
        {
            deliveryVehicles.add(del);
        }

    }

}