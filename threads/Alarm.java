package nachos.threads;

import nachos.machine.*;

/**
 * Uses the hardware timer to provide preemption, and to allow threads to sleep
 * until a certain time.
 */
public class Alarm {
    /**
     * Allocate a new Alarm. Set the machine's timer interrupt handler to this
     * alarm's callback.
     *
     * <p><b>Note</b>: Nachos will not function correctly with more than one
     * alarm.
     */

     public int counter = 0; //total number of threads implemented
     public static Object [][] keep = new Object [999][2]; // creates  a2d array to store wake time and thread count

    public Alarm() {
	Machine.timer().setInterruptHandler(new Runnable() {
		public void run() 
        { 
            timerInterrupt(); }
	    }
        );
    }

    /**
     * The timer interrupt handler. This is called by the machine's timer
     * periodically (approximately every 500 clock ticks). Causes the current
     * thread to yield, forcing a context switch if there is another thread
     * that should be run.
     */








//AHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH

    public void timerInterrupt() {
        boolean status = Machine.interrupt().disable(); //disables the inturrept (NEED ON ALMOST EVERY THING)
       
        for(int z = 0; z < 999; z++){ //create a loop until the end of the array for wake time
             if (keep[z][1] != null){
                if((long) keep[z][0] < Machine.timer().getTime()){
                    //check timer for threads to wake up before proceeding also must be a long due to how many digits and places are being taken
            ((KThread) keep[z][1]).ready();
        
        
        keep[z][1] = null;//if not null could cause the threads to override eachother which would be bad
        keep [z][0] = null; //lets try it -nicholas chorette (my brother)
        }
    }
    Machine.interrupt().restore(status);
}
    }
	//KThread.currentThread().yield();
    

    /**
     * Put the current thread to sleep for at least <i>x</i> ticks,
     * waking it up in the timer interrupt handler. The thread must be
     * woken up (placed in the scheduler ready set) during the first timer
     * interrupt where
     *
     * <p><blockquote>
     * (current time) >= (WaitUntil called time)+(x)
     * </blockquote>
     *
     * @param	x	the minimum number of clock ticks to wait.
     *
     * @see	nachos.machine.Timer#getTime()
     */
    public void waitUntil(long x) {
	// for now, cheat just to get something working (busy waiting is bad)
	boolean status = Machine.interrupt().disable(); //disables interrupt
    long wakeTime = Machine.timer().getTime() + x;

    //easiest way to keep track of items will be to keep a array

    keep[counter][0] = wakeTime;
    keep[counter][1] = KThread.currentThread(); // keep track of all current thread
    if (counter > 999){
        counter = 0; //reset the cpunter before incremeting
    }
    counter++; //must make sure it is outside the loop
    
    //KThread.sleep;
    KThread.currentThread().sleep();
    Machine.interrupt().restore(status);

	//while (wakeTime > Machine.timer().getTime())
	 //   KThread.yield();
    }
}

