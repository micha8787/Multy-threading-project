package bgu.spl.mics.application.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import bgu.spl.mics.*;

import bgu.spl.mics.application.messages.CheckAvailabilityEvent;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.Event;

/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 * Handles {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class SellingService extends MicroService{

    MoneyRegister moneyRegister;
    String name;
    int currentTick;
    ConcurrentHashMap <Future, Message> futures = new ConcurrentHashMap<>();





    public SellingService(String name)
    {
        super(name);
        moneyRegister = MoneyRegister.getInstance();

    }

    @Override
    protected void initialize()
    {

        subscribeBroadcast(TerminateBroadcast.class, callForTerminateBroadcast ->
        {

                this.terminate();

        });

        subscribeEvent(BookOrderEvent.class , bookOrderEvent ->
        {


            futures.put(sendEvent(new CheckAvailabilityEvent(bookOrderEvent.getBookTitle(),bookOrderEvent.getCustomer())), bookOrderEvent);


        });

        subscribeBroadcast(TickBroadcast.class, callForTickBroadcast ->
        {
            this.currentTick = callForTickBroadcast.getCurrentTick();

            Iterator it = futures.entrySet().iterator();
            while(it.hasNext())
            {

                Map.Entry pair = (Map.Entry)it.next();
                Customer customer = ((BookOrderEvent) pair.getValue()).getCustomer();
                BookOrderEvent bookOrderEvent =(BookOrderEvent) pair.getValue();
                int startedProccessingEvent = this.getCurrentTick();

                if( ((Future)pair.getKey()).isDone() )
                {
                    Integer result = (Integer)((Future) pair.getKey()).get();
                    if(result != -1)
                    {

                        this.moneyRegister.chargeCreditCard(customer, result);
                        OrderReceipt receipt = new OrderReceipt(bookOrderEvent.getOrderId(), customer.getId(), result, this.getCurrentTick(), bookOrderEvent.getOrderTick(), startedProccessingEvent, this.getName(), bookOrderEvent.getBookTitle());
                        customer.insertReceipt(receipt);
                        moneyRegister.file(receipt);
                        this.complete(bookOrderEvent, receipt);
                        it.remove();
                    }
                    else
                    {
                        complete(bookOrderEvent,null);
                    }
                }



            }
        });


    }

    public int getCurrentTick()
    {
        return this.currentTick;
    }







}

