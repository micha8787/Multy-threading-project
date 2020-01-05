package bgu.spl.mics.application.passiveObjects;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import bgu.spl.mics.MessageBusImpl;

/**
 * Passive object representing the store finance management.
 * It should hold a list of receipts issued by the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class MoneyRegister implements Serializable
{
    private List<OrderReceipt> receipts = new ArrayList<OrderReceipt>();
    private int totalEarnings;


    private static class SingeltonHolder
    {
        private static MoneyRegister INSTANCE = new MoneyRegister();
    }

    /**
     * Retrieves the single instance of this class.
     */
    public static MoneyRegister getInstance()
    {

        return SingeltonHolder.INSTANCE;
    }

    /**
     * Saves an order receipt in the money register.
     * <p>
     * @param r		The receipt to save in the money register.
     */
    public void file (OrderReceipt r)
    {
        receipts.add(r);
    }

    /**
     * Retrieves the current total earnings of the store.
     */
    public int getTotalEarnings()
    {
        return this.totalEarnings;


    }

    /**
     * Charges the credit card of the customer a certain amount of money.
     * <p>
     * @param amount 	amount to charge
     */
    public void chargeCreditCard(Customer c, int amount)
    {

        c.chargeForAmount(amount);
        this.totalEarnings = this.totalEarnings + amount;

    }

    /**
     * Prints to a file named @filename a serialized object List<OrderReceipt> which holds all the order receipts
     * currently in the MoneyRegister
     * This method is called by the main method in order to generate the output.
     */
    public void printOrderReceipts(String filename)
    {
        try{
            FileOutputStream fos= new FileOutputStream(filename);
            ObjectOutputStream oos= new ObjectOutputStream(fos);
            oos.writeObject(this.receipts);
            oos.close();
            fos.close();
        }
        catch(IOException ioe)
        {

        }
    }
}