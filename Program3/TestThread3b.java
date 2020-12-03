import java.util.*;
import java.lang.reflect.*;
import java.io.*;

//Thread that conducts only numerical computation
public class TestThread3b extends Thread
{
    private String name;
    private int cpuBurst;

    private long submissionTime;
    private long responseTime;
    private long completionTime;

    public TestThread3b ( String args[] ) {
        name = args[0];
        cpuBurst = Integer.parseInt( args[1] );
        
        submissionTime = new Date( ).getTime( );
    }

    public void run( ) {
	    responseTime = new Date( ).getTime( );

        /*for ( int burst = cpuBurst; burst > 0; burst -= 100 ) {
            SysLib.cout( "Thread[" + name + "] is running\n" );
            SysLib.sleep( 100 );
        }*/

        //Creates multiple loop blocks with random arithmetic 
        int testNum = 1;
        for (int i = 0; i < 500; i++) {
            testNum += 100;
            testNum *= 3;
            testNum -= 10;
        }

        for (int i = 0; i < 1000; i++) {
            testNum += 10;
            testNum *= 2;
            testNum -= 20;
        }

        for (int i = 0; i < 1000; i++) {
            testNum += 1000;
            testNum *= 1;
            testNum -= 100;
        }

        completionTime = new Date( ).getTime( );
        SysLib.cout( "Thread[" + name + "]:" +
                " response time = " + (responseTime - submissionTime) +
                " turnaround time = " + (completionTime - submissionTime)+
                " execution time = " + (completionTime - responseTime)+
                "\n");
        SysLib.exit( );
    }
}