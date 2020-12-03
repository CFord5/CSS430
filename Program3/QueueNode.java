import java.util.*;
import java.lang.reflect.*;
import java.io.*;

public class QueueNode
{
    //Vector of ints
    Vector<Integer> childrenTID;
    
    //Constructor
    public QueueNode() {
        childrenTID = new Vector<>();  //Vector of children thread IDs that have called dequeueAndWakeup, indexed by condition in SyncQueue (parent IDs)
    }

    //When a parent thread calls SyncQueue.enqueueAndSleep( ) that calls QueueNode.sleep( ) or something,
    //it checks if the Vector queue of child thread IDs is empty. If so, it puts itself to sleep. Upon a
    //wake-up,it checks this TID queue and picks up the first ID from this queue as a return value
    public synchronized int sleep() {
        while (childrenTID.isEmpty() == true)  //Check if empty
        {
            try {
                wait();  //Sleep thread
            } catch (InterruptedException e){}
        }
        int returnValue = childrenTID.firstElement();  //Take first element from vector as return value
        childrenTID.remove(0);  //Remove first element from vector
        return returnValue;
    }
 
    //When a child thread calls SyncQueue.dequeueAndWakeup() that calls QueueNode.wakeup() or somethings
    //it enqueues its TID in this Vector queue of thread IDs:
    public synchronized void wakeup(int tid) {  //Put value in queue
        childrenTID.add(tid);  //Puts child thread ID into vector
        notify();  //Notifies sleeping thread
    }
}