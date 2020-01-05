package bgu.spl.mics.application.services;

import java.util.Timer;
import java.util.TimerTask;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService
{

    private int programDuration;
    private int currentTick;
    private int speed;

    public TimeService(int programDuration,int speed)
    {
        super("");
        this.programDuration=programDuration;
        this.currentTick = 1;
        this.speed=speed;


    }

    @Override
    protected void initialize()
    {
        Thread timerthread = Thread.currentThread();
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask()
        {

            @Override
            public void run()
            {

                if(currentTick <= programDuration)
                {

                    sendBroadcast(new TickBroadcast(currentTick));
                    currentTick++;
                }
                else
                {
                    sendBroadcast(new TerminateBroadcast());

                }

            }
        };
        timer.scheduleAtFixedRate(timerTask, 0,speed );

        subscribeBroadcast(TerminateBroadcast.class, terminateBroadcast ->
        {
            timer.cancel();
            timer.purge();
            //timerthread.interrupt();
            this.terminate();


        });


    }

}