
package bgu.spl.mics;


import bgu.spl.mics.application.messages.ReturningVehicle;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

import javax.xml.transform.sax.SAXSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl<T> implements MessageBus {

    private static class SingeltonHolder {
        private static MessageBusImpl INSTANCE = new MessageBusImpl();
    }


    private ConcurrentHashMap<MicroService, BlockingQueue<Message>> listOfQueues = new ConcurrentHashMap<MicroService, BlockingQueue<Message>>();  //string is key ( micro service)
    private ConcurrentHashMap<Class, BlockingQueue<MicroService>> subsToMessages = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Event<?>, Future> futures = new ConcurrentHashMap<>();


    @Override
    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {

        if (type != null && m != null) {
            if (messageExist(type)) {
                subsToMessages.get(type).add(m);

            } else {

                subsToMessages.put(type, new LinkedBlockingQueue<MicroService>());
                subsToMessages.get(type).add(m);

            }


        }


    }

    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        if (type != null && m != null) {
            if (messageExist(type)) {

                subsToMessages.get(type).add(m);

            } else {

                subsToMessages.put(type, new LinkedBlockingQueue<MicroService>());
                subsToMessages.get(type).add(m);

            }
        }


    }

    @Override
    public <T> void complete(Event<T> e, T result) {


        Future future = (Future) futures.get(e);
        future.resolve(result);

    }

    @Override
    public  void sendBroadcast(Broadcast b) {

        if (subsToMessages.containsKey(b.getClass())) {
            for (MicroService m : subsToMessages.get(b.getClass())) {

                listOfQueues.get(m).add(b);

            }
        }


    }


    @Override
    public  <T> Future<T> sendEvent(Event<T> e) {

        Future<T> future = new Future<T>();



        synchronized (subsToMessages.get(e.getClass())) {


            if (subsToMessages.get(e.getClass()) == null || subsToMessages.get(e.getClass()).isEmpty()) {
                return null;
            } else {


                MicroService m = subsToMessages.get(e.getClass()).poll(); //to check what happens when theres more threads then micros in the list
                futures.put(e, future);
                listOfQueues.get(m).add(e);
                subsToMessages.get(e.getClass()).add(m);



            }
        }


        return future;


    }


    @Override
    public void register(MicroService m) {
        if (m != null){ //&& !isRegistered(m)) {


            listOfQueues.put(m, new LinkedBlockingQueue<Message>());


        }

    }

    @Override
    public void unregister(MicroService m) {
        ///thread safety problem: sending msg while deleting micro service


        if (m != null && isRegistered(m)) {
            //deleteMicroServiceFromSubs(m);

            for (Message msg : listOfQueues.get(m)) {
                if (futures.containsKey(msg))
                {
                    // michael today
                    if (msg instanceof Event)
                    {
                        complete((Event) msg, null);
                    }

                    subsToMessages.remove(msg,m);
                    //ad kan
                    futures.remove(msg);
                    listOfQueues.get(m).remove(msg);

                }
                else
                listOfQueues.remove(msg);

            }

        }


    }

    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException {

        Message mes = listOfQueues.get(m).take();

        return mes;
    }


    //if(!isRegistered(m))
    //throw new IllegalStateException();

    //else
    //{
		/*Message ms = null;
		synchronized (this)
		{
			try
			{
				wait();
			}
			catch(InterruptedException ex)
			{
				ms =  listOfQueues.get(m).take();
				notifyAll();
			}

		}

			return ms;

		//} */

    private boolean isRegistered(MicroService m) {
        if (listOfQueues.containsKey(m))
            return true;
        else
            return false;
    }


    public static MessageBusImpl getInstance() {
        return SingeltonHolder.INSTANCE;
    }

    private boolean messageExist(Class<? extends Message> type) {
        return subsToMessages.containsKey(type);
    }





	/*private void deleteMicroServiceFromSubs(MicroService m)
	{
		for (Map.Entry<Class<? extends Event<?>>, BlockingQueue<MicroService>> entry : subsToEvents.entrySet())
		{
			if(entry.getValue().contains(m))
			{
				entry.getValue().remove(m);
			}
		}

		for (Map.Entry<Class<? extends Broadcast>, BlockingQueue<MicroService>> entry : subsToBroadcasts.entrySet())
		{
			if(entry.getValue().contains(m))
			{
				entry.getValue().remove(m);
			}
		}
		}*/


}