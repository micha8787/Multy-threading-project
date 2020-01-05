package bgu.spl.mics.application.passiveObjects;

import javax.xml.transform.sax.SAXSource;
import java.io.*;
import java.util.HashMap;

/**
 * Passive data-object representing the store inventory.
 * It holds a collection of {@link BookInventoryInfo} for all the
 * books in the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Inventory implements Serializable
{

    private HashMap<String,Integer> amountOfBooksByName = new HashMap<String,Integer>();
    private BookInventoryInfo[] inventory;

    private static class SingeltonHolder
    {
        private static Inventory INSTANCE = new Inventory();
    }

    /**
     * Retrieves the single instance of this class.
     */
    public static Inventory getInstance()
    {
        return SingeltonHolder.INSTANCE;
    }

    /**
     * Initializes the store inventory. This method adds all the items given to the store
     * inventory.
     * <p>
     * @param inventory 	Data structure containing all data necessary for initialization
     * 						of the inventory.
     */
    public void load (BookInventoryInfo[ ] inventory )
    {
        this.inventory = inventory;


    }

    /**
     * Attempts to take one book from the store.
     * <p>
     * @param book 		Name of the book to take from the store
     * @return 	an {@link Enum} with options NOT_IN_STOCK and SUCCESSFULLY_TAKEN.
     * 			The first should not change the state of the inventory while the
     * 			second should reduce by one the number of books of the desired type.
     */
    public OrderResult take (String book)
    {
        OrderResult orderResult = OrderResult.NOT_IN_STOCK;
        if(checkAvailabiltyAndGetPrice(book) != -1)
        {
            orderResult = OrderResult.SUCCESSFULLY_TAKEN;
            for(BookInventoryInfo bookInfo: this.inventory)
            {
                if(bookInfo.getBookTitle().equals(book))
                {
                    bookInfo.amountInInventory.decrementAndGet();


                }
            }

        }

        return orderResult;

    }



    /**
     * Checks if a certain book is available in the inventory.
     * <p>
     * @param book 		Name of the book.
     * @return the price of the book if it is available, -1 otherwise.
     */
    public  int checkAvailabiltyAndGetPrice(String book)
    {
        int price = -1;
        boolean found = false;
        for(int i = 0; i < inventory.length && !found; i = i + 1)
        {
            if(inventory[i].bookTitle.equals(book))
            {
                if(inventory[i].getAmountInInventory()!=0)
                {
                    price = inventory[i].price;
                    found = true;
                 }
                else
                {
                    return -1;
                }
            }

        }

        return price;

    }

    /**
     *
     * <p>
     * Prints to a file name @filename a serialized object HashMap<String,Integer> which is a
     * Map of all the books in the inventory. The keys of the Map (type {@link String})
     * should be the titles of theimplements java.io.Serializable books while the values (type {@link Integer}) should be
     * their respective available amount in the inventory.
     * This method is called by the main method in order to generate the output.
     */
    public void printInventoryToFile(String filename)
    {
        createHashMap(this.inventory);
        try
        {
            FileOutputStream fos = new FileOutputStream(filename);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this.amountOfBooksByName);
            oos.close();
            fos.close();

        }
        catch (IOException e)
        {

        }
    }

    private void createHashMap(BookInventoryInfo[] inventory)
    {
        for(BookInventoryInfo bookInfo: inventory)
        {
            this.amountOfBooksByName.put(bookInfo.bookTitle, bookInfo.getAmountInInventory());
        }
    }




}