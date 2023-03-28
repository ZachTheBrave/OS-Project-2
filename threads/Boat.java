package nachos.threads;
import nachos.ag.BoatGrader;

public class Boat
{
    static BoatGrader bg;

	private static Lock is1 = new Lock(); 
    private static Lock is2 = new Lock(); 

    private static int aload1; 
	private static Condition a1 = new Condition(is1); 
    private static int cload1; 
	private static Condition cland1 = new Condition(is1);
	private static int cwait1;
	private static int cfrom1;

	private static int aload2;
	private static int cload2; 
	
    private static Condition cland2 = new Condition(is2);
	private static Condition cboat1 = new Condition(is1);
	private static boolean isb1;

	private static Semaphore done = new Semaphore(0);

    public static void selfTest()
    {
	BoatGrader b = new BoatGrader();
	
	System.out.println("\n ***Testing Boats with only 2 children***");
	begin(0, 2, b);

//	System.out.println("\n ***Testing Boats with 2 children, 1 adult***");
//  	begin(1, 2, b);

//  	System.out.println("\n ***Testing Boats with 3 children, 3 adults***");
//  	begin(3, 3, b);
    }

    public static void begin( int adults, int children, BoatGrader b )
    {
	// Store the externally generated autograder in a class
	// variable to be accessible by children.
		bg = b;
		variableInitialization(adults, children, b); 		
	// Instantiate global variables here
		
	// Create threads here. See section 3.4 of the Nachos for Java
	// Walkthrough linked from the projects page.
//LOOK IF IF LOOP WILL WORK INTEAD OF FOR


for(int z = 0; z < children; z++){

	new KThread(

	new Runnable(){

			public void run() {

				ChildItinerary();

			}

		}

).setName("child" + z).fork();

}

for(int z = 0; z < adults; z++){

	new KThread(

	new Runnable() {

			public void run() {

			AdultItinerary();

		}	

	}

		).setName("adult" + z).fork();

}

done.P();

}

	static void variableInitialization(int a, int c, BoatGrader b) { 

		bg = b;

		cload1 = c; 

		aload1 = a; 

		cload2 = 0; 

		aload2 = 0; 

		cwait1 = 0; 

		cfrom1 = 0; 

		isb1 = true;

	}

	//Runnable r = new Runnable() {
	  //  public void run() {
        //        SampleItinerary();
          //  }
        //};
        //KThread t = new KThread(r);
        //t.setName("Sample Boat Thread");
        //t.fork();

    
	
    static void AdultItinerary()
    {
	/* This is where you should put your solutions. Make calls
	   to the BoatGrader to show that it is synchronized. For
	   example:
	       bg.AdultRowToMolokai();
	   indicates that an adult has rowed the boat across to Molokai
	*/
	is1.acquire();

	while(cload1 > 1 || !isb1){

		a1.sleep();

	}

	aload1 = aload1 -1;

isb1 = false;

is1.release();

	bg.AdultRowToMolokai();

	is2.acquire();

	cland2.wake();

	is2.release();

	aload2 = aload2 - 1;

}

    static void ChildItinerary()

	{

		while(cload1 + aload1 > 1){

			is1.acquire();

			if(cload1 == 1){

	a1.wake();

}
		
while(cwait1 >= 2 || !isb1){
		
			cland1.sleep();
		
		}
		
		if (cwait1 == 0) { 
		
			cwait1++;
		
		cland1.wake(); 
		
		cboat1.sleep();
		
		bg.ChildRideToMolokai(); 
		
		cboat1.wake(); 
	
	} 
	
	else { 

		cwait1++;

		cboat1.wake(); 
		
		bg.ChildRowToMolokai(); 

		cboat1.sleep(); 
		
	}
	
	cwait1--; 

	cload1--; 

	isb1 = false; 

	is1.release(); 

	is2.acquire();

	cfrom1++;

	cload2++; 

	if (cfrom1 == 1) {

		cland2.sleep();

	}

	cload2--; 

	cfrom1 = 0; 	

	is2.release();	

	bg.ChildRowToOahu(); 	

	is1.acquire();	

	cload1++; 	

	isb1 = true; 	

	is1.release();

}
	is1.acquire();

	cload1--;
	
	is1.release();
	
	bg.ChildRowToMolokai();	
	
	is2.acquire();
	
	cload2++;
	
	is2.release();
	
	done.V();

}
    static void SampleItinerary()
    {
	// Please note that this isn't a valid solution (you can't fit
	// all of them on the boat). Please also note that you may not
	// have a single thread calculate a solution and then just play
	// it back at the autograder -- you will be caught.
	System.out.println("\n ***Everyone piles on the boat and goes to Molokai***");
	bg.AdultRowToMolokai();
	bg.ChildRideToMolokai();
	bg.AdultRideToMolokai();
	bg.ChildRideToMolokai();
    }
    
}
