import java.util.*;
import java.lang.reflect.*;
import java.io.*;

public class SyncQueue
{
    //Maintains an array of QueueNode objects, each representing a different condition and enqueuing 
    //all threads that wait for this condition. The size of the queue array should be given through 
    //a constructor whose spec is given below.
    private QueueNode[] queue;

    //Constructor that creates a queue and allow threads to wait for a default condition number (=10) 
    public SyncQueue() {
        queue = new QueueNode[10];
    }

    //Constructor that creates a queue, allows threads to wait for condMax # of condition/event types 
    public SyncQueue(int condMax) {
        queue = new QueueNode[condMax];
    }

    //Enqueues the calling thread into the queue and waits until a given condition is satisfied. It
    //returns the ID of a child thread that has woken the calling thread.
    public int enqueueAndSleep(int condition) { 
        QueueNode node = new QueueNode();  //Set new node
        queue[condition] = node;  //Place node at index specified by condition
        int childTID = queue[condition].sleep();  //Call sleep method at index
        return childTID;  //Return child thread ID from sleep()
    }

    //Dequeues and wakes up a thread waiting for a given condition. If there are two or more threads 
    //waiting for the same condition, only one thread is dequeued and resumed. The FCFS 
    //(first-come-first-service) order does not matter. This tid will be passed to the thread that 
    //has been woken up from enqueueAndSleep. If no 2nd argument is given, you may regard tid as 0. 
    public void dequeueAndWakeup(int condition) { 
        int tid = 0;  //Default value
        queue[condition].wakeup(tid);  //Call wakeup at index and pass in the child thread ID
    }

    //Dequeues and wakes up a thread waiting for a given condition. If there are two or more threads 
    //waiting for the same condition, only one thread is dequeued and resumed. The FCFS 
    //(first-come-first-service) order does not matter. This function can receive the calling 
    //thread's ID, (tid) as the 2nd argument. This tid will be passed to the thread that has been 
    //woken up from enqueueAndSleep. 
    public void dequeueAndWakeup(int condition, int tid) {
        queue[condition].wakeup(tid);  //Call wakeup at index and pass in the child thread ID
    }
}