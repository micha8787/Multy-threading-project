package bgu.spl.mics.application.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicInteger;

import bgu.spl.mics.Callback;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;

import bgu.spl.mics.application.messages.DeliveryEvent;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.*;


/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService
{
    private Customer customer;
    private int currentTickApi;
    public AtomicInteger orderId = new AtomicInteger(0);
    private List<Future<?>> futureReceipts = new ArrayList<Future<?>>();


    public APIService(Customer customer)
    {
        super("");
        this.customer = customer;
    }

    public Customer getCustomer()
    {
        return this.customer;
    }

    @Override
    protected void initialize()
    {
        subscribeBroadcast(TickBroadcast.class, tickBroadcast ->
        {
            this.currentTickApi = tickBroadcast.getCurrentTick();

            if(this.getCustomer().getOrderSchedule().containsKey(this.currentTickApi)  && !this.getCustomer().getOrderSchedule().get(this.currentTickApi).isEmpty())
            {
                for(String bookTitle : this.getCustomer().getOrderSchedule().get(this.getCurrentTick()))
                {
                    //System.out.println(this.getCustomer().getName() + " " + "num of oders");
                    //System.out.println(this.getCurrentTick());
                    this.futureReceipts.add(sendEvent(new  BookOrderEvent(this.customer, this.orderId.getAndIncrement() , bookTitle, this.getCurrentTick())));
                    //this.getCustomer().getOrderSchedule().get(this.currentTickApi).remove(bookTitle);
                }

            }

            ListIterator<Future<?>> it = futureReceipts.listIterator();
            while(it.hasNext())
            {
                Future f = it.next();
                if(f.isDone() && f.get() != null)
                {
                    OrderReceipt receipt = (OrderReceipt) f.get();
                    this.sendEvent(new DeliveryEvent(this.getCustomer().getAddress(), this.getCustomer().getDistance()));
                    it.remove();

                }

                else if(f.get() == null)
                {
                    it.remove();
                }

            }






        });




        subscribeBroadcast(TerminateBroadcast.class, callForTerminateBroadcast ->
        {
            this.terminate();


        });

    }

    public int getCurrentTick()
    {
        return this.currentTickApi;

    }












}

