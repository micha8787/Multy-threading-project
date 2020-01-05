package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;

/**
 * Passive data-object representing a receipt that should
 * be sent to a customer after the completion of a BookOrderEvent.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class OrderReceipt implements Serializable
{
    private int orderId, customerId, price, issuedTick, orderTick, proccessTick;
    private String seller, bookTitle;



    public OrderReceipt(int orderId, int customerId, int price, int issuedTick, int orderTick, int proccessTick, String seller, String bookTitle)
    {
        this.orderId = orderId;
        this.customerId = customerId;
        this.price = price;
        this.issuedTick = issuedTick;
        this.orderTick = orderTick;
        this.proccessTick = proccessTick;
        this.seller = seller;
        this.bookTitle = bookTitle;


    }


    /**
     * Retrieves the orderId of this receipt.
     */
    public int getOrderId()
    {
        return this.orderId;

    }

    /**
     * Retrieves the name of the selling service which handled the order.
     */
    public String getSeller()
    {
        return this.getSeller();

    }

    /**
     * Retrieves the ID of the customer to which this receipt is issued to.
     * <p>
     * @return the ID of the customer
     */
    public int getCustomerId()
    {
        return this.customerId;
    }

    /**
     * Retrieves the name of the book which was bought.
     */
    public String getBookTitle()
    {
        return this.bookTitle;

    }

    /**
     * Retrieves the price the customer paid for the book.
     */
    public int getPrice()
    {
        return this.price;
    }

    /**
     * Retrieves the tick in which this receipt was issued.
     */
    public int getIssuedTick()
    {
        return this.issuedTick;
    }

    /**
     * Retrieves the tick in which the customer sent the purchase request.
     */
    public int getOrderTick()
    {
        return this.orderTick;
    }

    /**
     * Retrieves the tick in which the treating selling service started
     * processing the order.
     */
    public int getProcessTick()
    {
        return this.proccessTick;
    }

    public boolean isEqual(OrderReceipt or)
    {
        if(this.customerId == or.customerId && this.orderId == or.orderId)
        {
            return true;

        }
        else
            return false;
    }


}