package bgu.spl.mics.application.services;





import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AcquireAndDeliver;
import bgu.spl.mics.application.messages.ReturningVehicle;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

/**
 * ResourceService is in charge of the store resources - the delivery vehicles.
 * Holds a reference to the {@link ResourcesHolder} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link MoneyRegister}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ResourceService extends MicroService
{
    ResourcesHolder resourceHolder;
    int currentTick;
    private BlockingQueue<Future<DeliveryVehicle>> futuresToResolve;


    public ResourceService(String name)
    {

        super(name);
        this.resourceHolder = resourceHolder.getInstance();
        futuresToResolve= new LinkedBlockingQueue<Future<DeliveryVehicle>>();

    }

    @Override
    protected void initialize()
    {


        subscribeBroadcast(TickBroadcast.class, callForTickBroadcast ->
        {
            this.currentTick = callForTickBroadcast.getCurrentTick();

        });

        subscribeBroadcast(TerminateBroadcast.class, callForTerminateBroadcast ->
        {
            for(Future<DeliveryVehicle> f: futuresToResolve)
            {

                f.resolve(null);
            }
            this.terminate();
        });

        subscribeEvent(AcquireAndDeliver.class, acquireAndDeliver->
        {
            Future<DeliveryVehicle> deliveryVehicleFuture = resourceHolder.acquireVehicle();
            if(deliveryVehicleFuture.isDone())
            {
                complete(acquireAndDeliver, deliveryVehicleFuture);
            }
            else
            {
                complete(acquireAndDeliver, deliveryVehicleFuture);
                futuresToResolve.add(deliveryVehicleFuture);

            }




        });
        subscribeEvent(ReturningVehicle.class, returningVehicleEvent->
        {
          resourceHolder.releaseVehicle(returningVehicleEvent.getvehicle());







            //this.resourceHolder.releaseVehicle(returningVehicleEvent.getvehicle());
            /* Future<DeliveryVehicle> future = futuresToResolve.poll();
            if (future!=null)
            {
                future.resolve(returningVehicleEvent.getvehicle());
            }
            else
            {
                resourceHolder.releaseVehicle(returningVehicleEvent.getvehicle());
                this.complete(returningVehicleEvent, null);
            }
            */
        });

        // send car from logistics after delivery or when we send car from Resource we send a request to get it back
//		this.subscribeBroadcast(TickBroadcast.class, tickBroadcast ->
//		{
//			this.currentTick = tickBroadcast.getCurrentTick();
//		});
//		the last implementation:
//			subscribeEvent(AcquireAndDeliver.class,AcquireAndDeliver->
//			{
//			complete(AcquireAndDeliver,resourceHolder.acquireVehicle());
//			});
//



    }

}