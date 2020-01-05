package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

public class BookOrderEvent implements Event<OrderReceipt>
{

    private Customer customer;
    private String bookTitle;
    private int orderTick;
    private int orderId;

    public BookOrderEvent(Customer customer,int orderId , String bookTitle, int orderTick)
    {
        this.customer = customer;
        this.bookTitle = bookTitle;
        this.orderTick = orderTick;
        this.orderId = orderId;
    }

    public Customer getCustomer()
    {
        return this.customer;
    }

    public String getBookTitle()
    {
        return this.bookTitle;
    }

    public int getOrderTick()
    {
        return this.orderTick;
    }

    public int getOrderId()
    {
        return this.orderId;
    }

}