package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Passive data-object representing a customer of the store.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Customer implements Serializable
{

    private  List<OrderReceipt>  orderReceipts = new ArrayList <OrderReceipt>();
    private int id;
    private String name;
    private String address;
    private int distance;
    private int creditCard;
    private int availableCreditAmount;
    private ConcurrentHashMap<Integer, Vector<String>> orderScheduele = new ConcurrentHashMap<>();

    public Customer(int id,String name,String address,int distance,int creditCard,int availableCreditAmount, ConcurrentHashMap<Integer, Vector<String>> orderScheduele)
    {
        this.id = id;
        this.name = name;
        this.address = address;
        this.distance = distance;
        this.creditCard = creditCard;
        this.availableCreditAmount = availableCreditAmount;
        this.orderScheduele = orderScheduele;

    }


    /**
     * Retrieves the name of the customer.
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Retrieves the ID of the customer  .
     */
    public int getId()
    {
        return this.id;
    }

    /**
     * Retrieves the address of the customer.
     */
    public String getAddress()
    {
        return this.address;
    }

    /**
     * Retrieves the distance of the customer from the store.
     */
    public int getDistance()
    {
        return this.distance;
    }


    /**
     * Retrieves a list of receipts for the purchases this customer has made.
     * <p>
     * @return A list of receipts.
     */
    public List<OrderReceipt> getCustomerReceiptList()
    {
        return orderReceipts;
    }

    /**
     * Retrieves the amount of money left on this customers credit card.
     * <p>
     * @return Amount of money left.
     */
    public int getAvailableCreditAmount()
    {
        return this.availableCreditAmount;
    }

    /**
     * Retrieves this customers credit card serial number.
     */
    public int getCreditNumber()
    {
        return this.creditCard;
    }

    public ConcurrentHashMap<Integer, Vector<String>> getOrderSchedule()
    {
        return this.orderScheduele;
    }

    public void chargeForAmount(int amount)
    {
        this.availableCreditAmount = this.availableCreditAmount - amount;
    }

    public void insertReceipt(OrderReceipt receipt)
    {
        this.getCustomerReceiptList().add(receipt);
    }



}