package nachos.threads;

import java.util.concurrent.TimeUnit;

import nachos.machine.*;

public class Communicator {
	
    public Communicator() {
    }
     
    public void speak(int word) { //start speak
    	boolean status = Machine.interrupt().disable();
    	
    	templock.acquire();
    	howmanyspeaker++;
    	
    	
    	while(talk){ 
    		speaker.sleep();
    	}
    	
    	talk = true; 
    	testword = word; 
    	
    	while(howmanylisteners == 0 ) {
    		test.sleep(); 
        }
    	listen.wake(); 
    	test.sleep(); 
    	talk= false; 
    	speaker.wake();
    	templock.release(); 
    	
    	howmanyspeaker--; 
    	Machine.interrupt().restore(status);
    	
    	
    }//end speak
   
    public int listen() {//start listen
    	boolean status = Machine.interrupt().disable();
    	templock.acquire();

    	howmanylisteners++; 

    	if(howmanylisteners == 1) {
            if (talk){
            test.wake();
    	}
    }
        listen.sleep(); 
    	test.wake(); 
    	
    	howmanylisteners--; 
    	templock.release(); 
    	Machine.interrupt().restore(status); 
    	run ++;
    	return testword; 
    }//end listen
    private Lock templock = new Lock();;
	private Condition speaker = new Condition(templock);
	private Condition listen = new Condition(templock); 
	private Condition test = new Condition(templock);
	private int testword; 
	private int howmanylisteners = 0; 
	private static int howmanyspeaker = 0;
	private static int run=0;  
	private boolean talk; 
}