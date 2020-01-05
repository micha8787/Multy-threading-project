package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;

public class CheckAvailabilityEvent implements Event<Integer>
{
    private String bookTitle;
    private Customer customer;

    public CheckAvailabilityEvent(String bookTitle, Customer customer)
    {
        this.bookTitle = bookTitle;
        this.customer = customer;
    }

    public String getBookTitle()
    {
        return this.bookTitle;
    }

    public Customer getCustomer()
    {
        return this.customer;
    }

}