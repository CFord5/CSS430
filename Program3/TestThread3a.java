import java.util.*;
import java.lang.reflect.*;
import java.io.*;

//Thread that reads/writes many blocks randomly across the disk
public class TestThread3a extends Thread
{
    private String name;
    private int cpuBurst;

    private long submissionTime;
    private long responseTime;
    private long completionTime;

    public TestThread3a ( String args[] ) {
		name = args[0];
		cpuBurst = Integer.parseInt( args[1] );
		
		submissionTime = new Date( ).getTime( );
    }

    public void run( ) {
		  responseTime = new Date( ).getTime( );

	    /*for ( int burst = cpuBurst; burst > 0; burst -= 100 ) {
			SysLib.sleep( 100 );
        }*/
        
      //Creates multiple disk call blocks with a message of size 512
      byte[] bytes = new byte[512];
      String str = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
      aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
      aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
      aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
      aaaaaaaaaaaaaaaaaa";
      bytes = str.getBytes();
      SysLib.rawwrite( 1, bytes );
      SysLib.exit( );

      byte[] bytes2 = new byte[512];
      String str2 = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
      aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
      aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
      aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
      aaaaaaaaaaaaaaaaaa";
      bytes2 = str2.getBytes();
      SysLib.rawwrite( 1, bytes2 );
      SysLib.exit( );

      byte[] bytes3 = new byte[512];
      String str3 = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
      aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
      aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
      aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
      aaaaaaaaaaaaaaaaaa";
      bytes3 = str3.getBytes();
      SysLib.rawwrite( 1, bytes3 );
      SysLib.exit( );

      completionTime = new Date( ).getTime( );
      SysLib.cout( "Thread[" + name + "]:" +
          " response time = " + (responseTime - submissionTime) +
          " turnaround time = " + (completionTime - submissionTime)+
          " execution time = " + (completionTime - responseTime)+
          "\n");
      SysLib.exit( );
    }
}