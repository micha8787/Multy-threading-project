

import static org.junit.Assert.*;


import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Inventory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

public class InventoryTest {
	private Inventory inventor;
	private BookInventoryInfo book;
	private HashMap<String, BookInventoryInfo> inventory;
	private BookInventoryInfo[] ToAdd;


	@Before
	public void setUpBeforeClass() throws Exception {
		inventor = inventor.getInstance();
		book = new BookInventoryInfo("Harry Potter", 4, 3);
        inventory = new HashMap<String, BookInventoryInfo>();
        ToAdd = new BookInventoryInfo[2];
        
        ToAdd[0] = new BookInventoryInfo("bible", 1, 2);
        ToAdd[1] = book;
	}

	@AfterClass
	public  void tearDownAfterClass() throws Exception {
		book = null;
		inventor = null;
		inventory = null;
	}

	@Test
	public void testGetInstance() {
		 Inventory i;
	        i = inventor.getInstance();
	        assertTrue(inventor.getClass()==i.getClass());
	}

	@Test
	public void testLoad() {
		 inventor.load(ToAdd);
		 
		 
		 
	        assertTrue(inventory.containsKey("bible"));
	        assertTrue(inventory.containsKey(("Harry Potter")));
	}

	@Test
	public void testTake() {
        inventory.put(book.getBookTitle(),book);
        assertEquals(03, book.getAmountInInventory());
        inventor.take(book.getBookTitle());
        assertEquals(2, book.getAmountInInventory());
	}

	

}
