package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CheckAvailabilityEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

/**
 * InventoryService is in charge of the book inventory and stock.
 * Holds a reference to the {@link Inventory} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class InventoryService extends MicroService
{
    Inventory inventory;
    int currentTick;


    public InventoryService(String name)
    {
        super(name);
        this.inventory = Inventory.getInstance();

    }

    @Override
    protected void initialize()
    {

        subscribeBroadcast(TerminateBroadcast.class, callForTerminateBroadcast ->
        {
           this.terminate();
        });

        subscribeEvent(CheckAvailabilityEvent.class, checkAvailabilityEvent ->
        {
            Integer result = -1;

            synchronized(this.inventory)
           {
                int priceOfBook = this.inventory.checkAvailabiltyAndGetPrice(checkAvailabilityEvent.getBookTitle());
                if(priceOfBook != -1)
                {
                    if(checkAvailabilityEvent.getCustomer().getAvailableCreditAmount() >= priceOfBook )
                    {
                        this.inventory.take(checkAvailabilityEvent.getBookTitle());
                        result = priceOfBook;
                        //checkAvailabilityEvent.getCustomer().chargeForAmount(result);
                        this.complete(checkAvailabilityEvent,result);
                    }
                    else
                    {

                        this.complete(checkAvailabilityEvent, result);
                    }



                }
                else
                {

                    this.complete(checkAvailabilityEvent, result);
                }

            }


        });











    }
}

