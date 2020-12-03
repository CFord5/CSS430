import java.util.*;
import java.lang.reflect.*;
import java.io.*;

//Write a user-level test thread called Test3.java which spawns and waits for the completion of X pairs
//of threads (where X = 1 ~ 4), one conducting only numerical computation and the other reading/writing
//many blocks randomly across the disk. Test3.java should measure the time elapsed from the spawn to the
// termination of its child threads. 
public class Test3 extends Thread
{
    public void run( ) {
			//Create and execute threads TestThread3a and TestThread3b
			String[] args1 = SysLib.stringToArgs( "TestThread3a a 5000 0" );
			String[] args2 = SysLib.stringToArgs( "TestThread3b b 1000 0" );

			SysLib.exec( args1 );
			SysLib.exec( args2 );

			for (int i = 0; i < 2; i++ )
				SysLib.join( );
			//SysLib.cout( "Test3 finished\n" );
			SysLib.exit( );
    }
}