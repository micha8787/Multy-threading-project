package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

import java.util.concurrent.atomic.AtomicBoolean;

public class DeliveryEvent implements Event<AtomicBoolean>
{
    private String adress;
    private int distance;

    public DeliveryEvent(String adress, int distance)
    {
        this.adress = adress;
        this.distance = distance;
    }

    public String getAdress()
    {
        return this.adress;
    }
    public int getDistance()
    {
        return this.distance;
    }

}