package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

public class ReturningVehicle implements Event<DeliveryVehicle>
{

    private DeliveryVehicle vehicle;

    public ReturningVehicle(DeliveryVehicle vehicle)
    {

        this.vehicle = vehicle;
    }


    public DeliveryVehicle getvehicle()
    {
        return this.vehicle;

    }
}