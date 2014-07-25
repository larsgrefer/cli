package de.larsgrefer.cli;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
	public Set<Long> testSet;
	
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() throws Exception
    {
        assertTrue( List.class.isAssignableFrom(ArrayList.class) );
		ParameterizedType pt =  (ParameterizedType) this.getClass().getField("testSet").getGenericType();
		assertTrue(pt.getActualTypeArguments()[0].equals(Long.class));
			
				
    }
}
