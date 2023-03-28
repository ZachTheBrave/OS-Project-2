 package nachos.threads;
 import nachos.machine.*;
 import java.util.*;
 
 /**
  * A scheduler that chooses threads based on their priorities.
  * <p/>
  * <p/>
  * A priority scheduler associates a priority with each thread. The next thread
  * to be dequeued is always a thread with priority no less than any other
  * waiting thread's priority. Like a round-robin scheduler, the thread that is
  * dequeued is, among all the threads of the same (highest) priority, the
  * thread that has been waiting longest.
  * <p/>
  * <p/>
  * Essentially, a priority scheduler gives access in a round-robin fassion to
  * all the highest-priority threads, and ignores all other threads. This has
  * the potential to
  * starve a thread if there's always a thread waiting with higher priority.
  * <p/>
  * <p/>
  * A priority scheduler must partially solve the priority inversion problem; in
  * particular, priority must be donated through locks,` and through joins.
  */
 public class PriorityScheduler extends Scheduler {
	 /**
	  * Allocate a new priority scheduler.
	  */
	 public PriorityScheduler() {
	 } // didnt change 
 
	 /**
	  * Allocate a new priority thread queue.
	  *
	  * @param moveprio <tt>true</tt> if this queue should
	  *                         transfer priority from waiting threads
	  *                         to the owning thread.
	  * @return a new priority thread queue.
	  */
	 public ThreadQueue newThreadQueue(boolean moveprio) {
		
		 return new PriorityQueue(moveprio);
	 }

	 public int getPriority(KThread thread) {

		 Lib.assertTrue(Machine.interrupt().disabled()); 
 
		 return getThreadState(thread).numpri();
	 }

	 public int geteffprio(KThread thread) {

		 Lib.assertTrue(Machine.interrupt().disabled());

		 return getThreadState(thread).geteffprio(); 
	 }
 
	 public void setPriority(KThread thread, int priority) {

		Lib.assertTrue(Machine.interrupt().disabled());

		Lib.assertTrue(priority >= priorityMinimum &&
		   priority <= priorityMaximum);

		getThreadState(thread).setPriority(priority); 
	 }
 
	 public boolean increasePriority() {//TAKE NOTE WILL NEED LATER

		 boolean intStatus = Machine.interrupt().disable();

		 KThread thread = KThread.currentThread(); 

		 int priority = getPriority(thread);
		

		 if (priority == priorityMaximum) { 

			 return false;
		 }
		
		 setPriority(thread, priority + 1);
 
		 Machine.interrupt().restore(intStatus);

		 return true;  
	 }
 
	 public boolean decreasePriority() {

		 boolean intStatus = Machine.interrupt().disable();
 
		 KThread thread = KThread.currentThread();

		 int priority = getPriority(thread); 
		 
		 if (priority == priorityMinimum) { 

			 return false;
		 }
		 setPriority(thread, priority - 1);
 
		 Machine.interrupt().restore(intStatus);
		 
		 return true;
	 }
 
	 /**
	  * The default priority for a new thread. Do not change this value.
	  */
	 public static final int priorityDefault = 1;
	 /**
	  * The minimum priority that a thread can have. Do not change this value.
	  */
	 public static final int priorityMinimum = 0;
	 /**
	  * The maximum priority that a thread can have. Do not change this value.
	  */
	 public static final int priorityMaximum = 7;
 
	 /**
	  * Return the scheduling state of the specified thread.
	  *
	  * @param thread the thread whose scheduling state to return.
	  * @return the scheduling state of the specified thread.
	  */
	 protected ThreadState getThreadState(KThread thread) {

		 if (thread.schedulingState == null) 

			 thread.schedulingState = new ThreadState(thread);
 
		 return (ThreadState) thread.schedulingState;
	 }
 
	 protected class PriorityQueue extends ThreadQueue {
 
		 PriorityQueue(boolean moveprio) {

			this.waitthread = new LinkedList<ThreadState>();

			 this.moveprio = moveprio;

		 }
 
		 public void acquire(KThread thread) {

			 Lib.assertTrue(Machine.interrupt().disabled());

			 final ThreadState ts = getThreadState(thread);

			 if (this.resourceHolder != null) {

				 this.resourceHolder.release(this);

			 }

			 this.resourceHolder = ts;

			 ts.acquire(this);
		 }
 
		 public KThread nextThread() {

			 Lib.assertTrue(Machine.interrupt().disabled());

			 final ThreadState nextThread = this.pickNextThread();
 
			 if (nextThread == null) { 

				 return null;
			 }
 
			 this.waitthread.remove(nextThread);
 
			 this.acquire(nextThread.getThread());
 
			 return nextThread.getThread();
				}
 
		 public void waitForAccess(KThread thread) {

			Lib.assertTrue(Machine.interrupt().disabled());

			final ThreadState ts = getThreadState(thread);

			this.waitthread.add(ts);

			ts.waitForAccess(this);
		}

		 protected ThreadState pickNextThread() {
			 
			int nextPriority = priorityMinimum;

			 ThreadState next = null;

			 for (final ThreadState currThread : this.waitthread) {

				 int currPriority = currThread.geteffprio();

				 if (next == null || (currPriority > nextPriority)) {

					 next = currThread;

					 nextPriority = currPriority;

				 }
			 }
			 return next;
		 }
 
		 /**
		  * This method returns the effprio of this PriorityQueue.
		  * The return value is cached for as long as possible. If the cached value
		  * has been invalidated, this method will spawn a series of mutually
		  * recursive calls needed to recalculate effectivePriorities across the
		  * entire resource graph.
		  * @return
		  */
		 public int geteffprio() {
			 if (!this.moveprio) {

				 return priorityMinimum;

			 } else if (this.prioch) {
				 
				 this.effprio = priorityMinimum;

				 for (final ThreadState curr : this.waitthread) {

					 this.effprio = Math.max(

							 this.effprio, curr.geteffprio()

							 );
				 }
				 this.prioch = false;
			 }
			 return effprio;
		 }
		 
		 public void print() { 
			 Lib.assertTrue(Machine.interrupt().disabled()); 
		 }
 /*
		 public void print() {
			 Lib.assertTrue(Machine.interrupt().disabled());
			 for (final ThreadState ts : this.waitthread) {
				 System.out.println(ts.geteffprio());
			 }//end of for loop
		 }//end of print()
 */
		 private void nocache() {
 
			 this.prioch = true;
 
			 if (this.resourceHolder != null) {
				 resourceHolder.nocache();
			 }
		 }
 
		 protected final List<ThreadState> waitthread;
		 
		 protected int effprio = priorityMinimum;
		 
		 protected boolean prioch = false;

		 protected ThreadState resourceHolder = null;

		 public boolean moveprio;

	 }
	 /**
	  * The scheduling state of a thread. This should include the thread's
	  * priority, its effective priority, any objects it owns, and the queue
	  * it's waiting for, if any.
	  *
	  * @see nachos.threads.KThread#schedulingState
	  */
	 protected class ThreadState {
		 /**
		  * Allocate a new <tt>ThreadState</tt> object and associate it with the
		  * specified thread.
		  *
		  * @param thread the thread this state belongs to.
		  */
		 public ThreadState(KThread thread) {

			 this.thread = thread;
 
			 this.obtained = new LinkedList<PriorityQueue>();

			 this.need = new LinkedList<PriorityQueue>();
 
			 setPriority(priorityDefault);
 
		 }
 
		 /**
		  * Return the priority of the associated thread.
		  *
		  * @return the priority of the associated thread.
		  */
		 
		 public int numpri() { 
			 return priority;
		 }

		 private void nocache() {
			if (this.prioch){ 
				return; 
			}
			this.prioch = true; 
			for (final PriorityQueue prioq : this.need) 
			{ 
				prioq.nocache();
			}
		}

		 public int geteffprio() {
 
			 if (this.obtained.isEmpty()) 
			 
			 {

				 return this.numpri();

			 } else if (this.prioch) 
			 
			 {

				 this.effprio = this.numpri();

				 for (final PriorityQueue prioq : this.obtained) {

					 this.effprio = Math.max(

							 this.effprio, prioq.geteffprio()

							 );
				 }

				 this.prioch = false;

			 }

			 return this.effprio;

		 }

		 public void setPriority(int priority) {

			if (this.priority == priority)

			return;

			this.priority = priority;

		 }
 
		 public void waitForAccess(PriorityQueue waitQueue) {

			 waitQueue.nocache();

		 }

		 public void acquire(PriorityQueue waitQueue) {

			this.need.remove(waitQueue);
			
			this.obtained.add(waitQueue);

			this.nocache();
		 }
 
		
		 public void release(PriorityQueue waitQueue) {
			 this.obtained.remove(waitQueue);
		 }
 
		 public KThread getThread() {
			 return thread;
		 }
		 

		 protected boolean prioch = false;

		 protected int effprio = priorityMinimum;

		 protected int priority;

		 protected List<PriorityQueue> obtained;

		 protected List<PriorityQueue> need;
		 
		 protected KThread thread;	 
	 }
 }