/**
 * 
 */

import static org.junit.Assert.*;

import bgu.spl.mics.Future;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
/**
 * @author shafirmi@wincs.cs.bgu.ac.il
 *
 */
public class FutureTest {
	private Future f;
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Future f=new Future();
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}


	@Test
	public void testFuture() {
		
	
	}


	@Test
	public void testGet() {
		String a="finished";
		assertTrue("get function doesnt work", f.get()==null);
		f.resolve(a);
		
		assertTrue("get function doesnt work", f.get()==a);
		
	}


	@Test
	public void testResolve() {
		String a="finished";
		assertTrue("resolve function doesnt work", f.get()==null);
		f.resolve(a);
		
		assertTrue("resolve function doesnt work", f.get()==a);
		
		
		
	}


	@Test
	public void testIsDone() {
		assertTrue("doesnt working", f.get()!=null);
		
		
	}


	@Test
	public void testGetLongTimeUnit() {
		
		f.resolve("finished");
        TimeUnit unit = MILLISECONDS;
        assertTrue(f.get(1000, unit) != null);
	}

}
