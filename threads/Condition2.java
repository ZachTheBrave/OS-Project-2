package nachos.threads;

import nachos.machine.*;

/**
 * An implementation of condition variables that disables interrupt()s for
 * synchronization.
 *
 * <p>
 * You must implement this.
 *
 * @see	nachos.threads.Condition
 */
public class Condition2 {
    /**
     * Allocate a new condition variable.
     *
     * @param	conditionLock	the lock associated with this condition
     *				variable. The current thread must hold this
     *				lock whenever it uses <tt>sleep()</tt>,
     *				<tt>wake()</tt>, or <tt>wakeAll()</tt>.
     */
    public Condition2(Lock conditionLock) {
    this.wq = ThreadedKernel.scheduler.newThreadQueue(false); //creates wait queue for threads
	this.conditionLock = conditionLock;
    }

    /**
     * Atomically release the associated lock and go to sleep on this condition
     * variable until another thread wakes it using <tt>wake()</tt>. The
     * current thread must hold the associated lock. The thread will
     * automatically reacquire the lock before <tt>sleep()</tt> returns.
     */
    public void sleep() {
	Lib.assertTrue(conditionLock.isHeldByCurrentThread());

    boolean intStatus = Machine.interrupt().disable();// disable interrupts

    wq.waitForAccess(KThread.currentThread()); //adds itself into kthread

	conditionLock.release();
    KThread.sleep();
	conditionLock.acquire();
    
    Machine.interrupt().restore(intStatus);
    
    }

    public void wake() {
	
    Lib.assertTrue(conditionLock.isHeldByCurrentThread());
    
    boolean intStatus = Machine.interrupt().disable();// disable interrupts

    KThread next = wq.nextThread(); //allows the next thread to be assigned and continue
    
    if(next != null){
        next.ready(); //allows the wake statement to begin
    }
    
    Machine.interrupt().restore(intStatus); //yeah yeah we get it 
    
    }
    
    public void wakeAll() {
    
    boolean intStatus = Machine.interrupt().disable();// disable interrupts

    KThread next = wq.nextThread(); //allows the next thread to be assigned and continue
   
    while(next != null){
    next.ready(); //allows the wake statement to begin
        next = wq.nextThread(); //allows the queue to get the next one
    }

    Machine.interrupt().restore(intStatus);
    Lib.assertTrue(conditionLock.isHeldByCurrentThread());

    }
	private ThreadQueue wq = null;
    private Lock conditionLock;
}
