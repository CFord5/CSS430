import java.util.*;

public class Scheduler extends Thread
{
	private Vector queueMaster;
	private Vector queue0;
	private Vector queue1;
	private Vector queue2;
	private int timeSlice;
	private static final int DEFAULT_TIME_SLICE = 500; //Changed from 1000ms

	// New data added to p161 
	private boolean[] tids; // Indicate which ids have been used
	private static final int DEFAULT_MAX_THREADS = 10000;

	// A new feature added to p161 
	// Allocate an ID array, each element indicating if that id has been used
	private int nextId = 0;
	private void initTid( int maxThreads ) {
		tids = new boolean[maxThreads];
		for ( int i = 0; i < maxThreads; i++ )
			tids[i] = false;
	}

	// A new feature added to p161 
	// Search an available thread ID and provide a new thread with this ID
	private int getNewTid( ) {
		for ( int i = 0; i < tids.length; i++ ) {
			int tentative = ( nextId + i ) % tids.length;
			if ( tids[tentative] == false ) {
				tids[tentative] = true;
				nextId = ( tentative + 1 ) % tids.length;
				return tentative;
			}
		}
		return -1;
	}

	// A new feature added to p161 
	// Return the thread ID and set the corresponding tids element to be unused
	private boolean returnTid( int tid ) {
		if ( tid >= 0 && tid < tids.length && tids[tid] == true ) {
				tids[tid] = false;
				return true;
		}
		return false;
	}

	// A new feature added to p161 
	// Retrieve the current thread's TCB from the queue
	public TCB getMyTcb( ) {
		Thread myThread = Thread.currentThread( ); // Get my thread object
		synchronized(queue0) {  //Seach through queue0, queue1, and queue2 for tcb
			for ( int i = 0; i < queue0.size( ); i++ ) {
				TCB tcb = ( TCB )queue0.elementAt( i );
				Thread thread = tcb.getThread( );
				if ( thread == myThread ) // if this is my TCB, return it
					return tcb;
				}
		}
		synchronized(queue1) {
			for ( int i = 0; i < queue1.size( ); i++ ) {
				TCB tcb = ( TCB )queue1.elementAt( i );
				Thread thread = tcb.getThread( );
				if ( thread == myThread ) // if this is my TCB, return it
					return tcb;
				}
		}
		synchronized(queue2) {
			for ( int i = 0; i < queue2.size( ); i++ ) {
				TCB tcb = ( TCB )queue2.elementAt( i );
				Thread thread = tcb.getThread( );
				if ( thread == myThread ) // if this is my TCB, return it
					return tcb;
				}
		}
		return null;
	}
	
	// A new feature added to p161 
	// Return the maximal number of threads to be spawned in the system
	public int getMaxThreads( ) {
		return tids.length;
	}

	public Scheduler( ) {
		timeSlice = DEFAULT_TIME_SLICE;
		queueMaster = new Vector( );  //Modify constructor
		queue0 = new Vector( );
		queue1 = new Vector( );
		queue2 = new Vector( );
		initTid( DEFAULT_MAX_THREADS );
	}

	public Scheduler( int quantum ) {
		timeSlice = quantum;
		queueMaster = new Vector( );  //Modify constructor
		queue0 = new Vector( );
		queue1 = new Vector( );
		queue2 = new Vector( );
		initTid( DEFAULT_MAX_THREADS );
	}

	// A new feature added to p161 
	// A constructor to receive the max number of threads to be spawned
	public Scheduler( int quantum, int maxThreads ) {
		timeSlice = quantum;
		queueMaster = new Vector( );  //Modify constructor
		queue0 = new Vector( );
		queue1 = new Vector( );
		queue2 = new Vector( );
		initTid( maxThreads );
	}

	private void schedulerSleep( ) {
		try {
				Thread.sleep( timeSlice );
		} catch ( InterruptedException e ) {
		}
	}

	// A modified addThread of p161 example
	public TCB addThread( Thread t ) {
		//t.setPriority( 2 );
		TCB parentTcb = getMyTcb( ); // get my TCB and find my TID
		int pid = ( parentTcb != null ) ? parentTcb.getTid( ) : -1;
		int tid = getNewTid( ); // get a new TID
		if ( tid == -1)
			return null;
		TCB tcb = new TCB( t, tid, pid ); // create a new TCB
		queue0.add(tcb);
		return tcb;
	}

	// A new feature added to p161
	// Removing the TCB of a terminating thread
	public boolean deleteThread( ) {
		TCB tcb = getMyTcb( ); 
		if ( tcb!= null )
			return tcb.setTerminated( );
		else
			return false;
	}

	public void sleepThread( int milliseconds ) {
		try {
			sleep( milliseconds );
		} catch ( InterruptedException e ) { }
	}
    
	// A modified run of p161
	public void run( ) {
		Thread current = null;
		queueMaster = queue0;
		while ( true ) {
			try {
				// get the next TCB and its thread
				if ( queueMaster.size( ) == 0 )
					continue;
				TCB currentTCB = (TCB)queueMaster.firstElement( );
				if ( currentTCB.getTerminated( ) == true ) {
					queueMaster.remove( currentTCB );
					returnTid( currentTCB.getTid( ) );
					continue;
				}
				current = currentTCB.getThread( );
				if ( current != null ) {
					if ( current.isAlive( ) )
						current.resume(); //current.setPriority( 4 );
					else {
						// Spawn must be controlled by Scheduler
						// Scheduler must start a new thread
						current.start( );
						run();
					}
				}
				
				schedulerSleep( );  // System.out.println("* * * Context Switch * * * ");
		
				synchronized (queue0) {
					if ( current != null && current.isAlive( ) ) {  //4: If thread in Queue0 doesn't complete execution quantum Q0, scheduler moves TCB to Queue1 
						current.suspend();
						queue0.remove( currentTCB ); // rotate this TCB to the end
						queue1.add( currentTCB );
						queueMaster = queue1;
					} else {  //5: If Queue0 empty, execute threads in Queue1 for 500 ms, check Queue0 for pending TCBs, execute Queue0 first	
						if (getMyTcb() != null) {
							current.suspend();
							queueMaster = queue0;
						} 
					}
				}
				synchronized (queue1) { 
					if ( current != null && current.isAlive( ) ) {  //6: If thread in Queue1 doesn't complete execution quantum Q1, scheduler moves TCB to Queue2
						current.suspend();
						queue1.remove( currentTCB ); // rotate this TCB to the end
						queue2.add( currentTCB );
						queueMaster = queue2;
					} else {  //7: If Queue1 empty, execute threads in Queue 2 for 500 ms, check Queue0 and Queue1 for pending TCBs, execute Queue0 first before Queue2
						if (getMyTcb() != null) {
							current.suspend();
							queueMaster = queue0;
						}
					}
				}
				synchronized (queue2) {
					if ( current != null && current.isAlive( ) ) {  //8: If thread in Queue2 doesn't complete execution quantum Q2, scheduler moves TCB to tail of Queue2
						current.suspend();
						queue2.remove( currentTCB ); // rotate this TCB to the end
						queue2.add( currentTCB );
						queueMaster = queue2;
					} 
				}
			} catch ( NullPointerException e3 ) { };
		}
	}		
}			