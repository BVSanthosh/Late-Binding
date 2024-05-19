import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class LBSMain {

    /*output text when the arguments passed by the user isn't valid*/
    public static void printUsage() { 
        System.out.println("Input not recognised.  Usage is:");
        System.out.println("java LBSmain GEN|CHECK|SOLVE|GRACECHECK|GRACESOLVE <arguments>"  ); 
        System.out.println("     GEN arguments are seed [numpiles=17] [numranks=13] [numsuits=4] ");
        System.out.println("                       all except seed may be omitted, defaults shown");
        System.out.println("     SOLVE/GRACESOLVE argument is file]");
        System.out.println("                     if file is or is - then stdin is used");
        System.out.println("     CHECK/GRACECHECK argument is file1 [file2]");
        System.out.println("                     if file1 - then stdin is used");
        System.out.println("                     if file2 is ommitted or is - then stdin is used");
        System.out.println("                     at least one of file1/file2 must be a filename and not stdin");
	}

	/*Takes the input file and returns an ArrayList containing the answer*/
    public static ArrayList<Integer> readIntArray(String filename) {

        // File opening sample code from
        // https://www.w3schools.com/java/java_files_read.asp
		ArrayList<Integer> result;
		Scanner reader;

        try {
			File file = new File(filename);
			reader = new Scanner(file);
			result = readIntArray(reader);
			reader.close();
			return result;
        }
        catch (FileNotFoundException e) {
			System.out.println("File not found");
			e.printStackTrace();
        }

		// drop through case
		return new ArrayList<Integer>(0);
    }
        
	/*Reads the content of the file/arguments*/
    public static ArrayList<Integer> readIntArray(Scanner reader) {
		ArrayList<Integer> result = new ArrayList<Integer>(0);
        while( reader.hasNextInt()  ) {
            result.add(reader.nextInt());
        }
	  	return result;
    }

	public static void main(String[] args) {

		Scanner stdInScanner = new Scanner(System.in);    
		ArrayList<Integer> workingList = new ArrayList<>();
		ArrayList<Card> cardObjLayout = new ArrayList<>();
		ArrayList<Integer> testlayout = new ArrayList<>();
        LBSLayout layout;
        int seed ;
        int ranks ;
        int suits ;
        int numpiles ;
       
        if(args.length < 1) { printUsage(); return; };

		switch (args[0].toUpperCase()) {
            
            // Add additional commands if you wish for your own testing/evaluation
			case "GEN":
				if(args.length < 2) { printUsage(); return; };

				seed = Integer.parseInt(args[1]);
				numpiles = (args.length < 3 ? 17 : Integer.parseInt(args[2])) ;
				ranks = (args.length < 4 ? 13 : Integer.parseInt(args[3])) ;
				suits = (args.length < 5 ? 4 : Integer.parseInt(args[4])) ;
				layout = new LBSLayout(ranks,suits);
				layout.randomise(seed,numpiles);
				layout.print();
				stdInScanner.close();

				return;
			
			case "SOLVE":
				if (args.length<2 || args[1].equals("-")) {
					layout = new LBSLayout(readIntArray(stdInScanner));
				}
				else { 
					layout = new LBSLayout(readIntArray(args[1]));
				}

				stdInScanner.close();

				ArrayList<Integer> movesList = new ArrayList<>();
				StateStack stack = new StateStack();
				State initState;
				boolean solutionFound = false;
				boolean noMoreMoves = false;
				int totalMoves = 0;

				//goes through the layout and converts this list of integers into a list of Card objects
				for(int counter = 0; counter < layout.numPiles(); counter++){

					Card card;
					String suit = "";
					int cardNum = 0;
					int deckNum = layout.cardAt(counter);

					if(deckNum >= 1 && deckNum <= 13){
						suit = "spades";
						cardNum = deckNum;
					}
					else if(deckNum >= 14 && deckNum <= 26){
						suit = "hearts";
						cardNum = deckNum - 13;
					}
					else if(deckNum >= 27 && deckNum <= 39){
						suit = "diamonds";
						cardNum = deckNum - 26;
					}
					else if(deckNum >= 40 && deckNum <= 52){
						suit = "clubs";
						cardNum = deckNum - 39;
					}
					else{
						System.out.println("Invalid card detected.");
					}

					card = new Card(suit, cardNum, deckNum);   //initialises the card object 
					cardObjLayout.add(card);   //adds this card to the card object list
				}
				
				initState = new State(cardObjLayout, movesList);   //creates an initial state
				stack.push(initState);   //adds this initial state to the stack

				//main loop for the DFS algorithm
				while(solutionFound == false && noMoreMoves == false){

					ArrayList<Integer> possibleMoves = new ArrayList<>();
					ArrayList<Integer> movesMade = new ArrayList<>();
					ArrayList<Card> puzzle = new ArrayList<>();
					int cardFrom;
					int cardTo;
					State currentState = stack.pop();   //gets the current state of the game
				
					//checks if the solution is found
					if(currentState.solutionFound()){
						totalMoves = currentState.getNumOfMoves();   //if it is then the number of moves is retrieved 
						solutionFound = true;   //boolean set to true to display the appropriate output message
						break;
					}
					else{
						//checks whether the current state has at least one more move left
						if(currentState.hasNextMove()){

							State newState;
							State previousState = new State(currentState.getPuzzleState(), currentState.getMovesMade());

							//gets all three lists from the current state 
							possibleMoves = currentState.getPossibleMoves();
							movesMade = currentState.copyMoves();
							puzzle = currentState.copyLayout();

							//gets the next posssile move from this state
							cardFrom = possibleMoves.get(0);
							cardTo = possibleMoves.get(1);


							//removes this move so that it isnt done again from the next state 
							possibleMoves.remove(0);
							possibleMoves.remove(0);

							//adds this move to the moves made list for the next state 
							movesMade.add(puzzle.get(cardFrom).getActualNum());
							movesMade.add(cardTo);

							System.out.println(movesMade);
							System.out.println("-");

							//updates the possible moves of the current state so that the same move ist made when the algorithm backtracks to this state 
							previousState.upadatePossibleMoves(possibleMoves);


							//makes the actual move 
							puzzle.set(cardTo, puzzle.get(cardFrom));
							puzzle.remove(cardFrom);

							//creates the new game state after making the move and stores the updated versions of each list
							newState = new State(puzzle, movesMade);

							//pushes both these states to the stack to continue the DFS
							stack.push(previousState);
							stack.push(newState);
						}
					}

					//checks if the stack is empty and there are no more moves
					if(stack.isEmpty()){
						noMoreMoves = true;   //boolean set to true to display the appropriate output message
					}
				}

				if(solutionFound){
					System.out.println("Solution found: ");
					System.out.println(totalMoves);
				}
				else if(noMoreMoves){
					System.out.println("Unsolvable: ");
					System.out.println("-1");
				}

				return;

			case "GRACESOLVE":

			case "SOLVEGRACE":
				if (args.length<2 || args[1].equals("-")) {
					layout = new LBSLayout(readIntArray(stdInScanner));
				}
				else { 
					layout = new LBSLayout(readIntArray(args[1]));
				}
			
				// YOUR CODE HERE
 
				stdInScanner.close();

				return;

			case "CHECK":
			
				if (args.length < 2 || 
			    	( args[1].equals("-") && args.length < 3) || 
			   		( args[1].equals("-") && args[2].equals("-"))
			   		) 
				{ printUsage(); return; };

				if (args[1].equals("-")) {
					layout = new LBSLayout(readIntArray(stdInScanner));
				}
				else { 
					layout = new LBSLayout(readIntArray(args[1]));
				}

				if (args.length < 3 || args[2].equals("-")) {
					workingList = readIntArray(stdInScanner);
				}
				else { 
					workingList = readIntArray(args[2]);
				}

				stdInScanner.close();

				int numOfMoves = workingList.get(0);
				workingList.remove(0);
				int moves = workingList.size();
				
				for(int counter = 0; counter < testlayout.size(); counter++){

					Card card;
					String suit = "";
					int cardNum = 0;   
					int deckNum = testlayout.get(counter);

					if(deckNum >= 1 && deckNum <= 13){
						suit = "spades";
						cardNum = deckNum;
					}
					else if(deckNum >= 14 && deckNum <= 26){
						suit = "hearts";
						cardNum = deckNum - 13;
					}
					else if(deckNum >= 27 && deckNum <= 39){
						suit = "diamonds";
						cardNum = deckNum - 26;
					}
					else if(deckNum >= 40 && deckNum <= 52){
						suit = "clubs";
						cardNum = deckNum - 39;
					}
					else{
						System.out.println("Invalid card detected.");
					}

					card = new Card(suit, cardNum, deckNum);
					cardObjLayout.add(card);
				}

				//loops through all the moves in the workingList
				while(workingList.size() >= 2){

					int cardNum = workingList.get(0);
					int pileNUm = workingList.get(1);
					int des = pileNUm;
					int src = 0;

					workingList.remove(0);
					workingList.remove(0);

					//loop for finding the deck number of the card to move
					for(int counter = 0; counter < cardObjLayout.size(); counter++){  

						if(cardNum == cardObjLayout.get(counter).getActualNum()){    //if this card is found
							src = counter;
							break;
						}
					}

					//checks that the move made is valid
					if((des == src - 1) || (des == src - 3) ){    //is 1 or 3 moves away
						if((cardObjLayout.get(src).getCardSuit().equals(cardObjLayout.get(des).getCardSuit())) || (cardObjLayout.get(src).getCardNum() == cardObjLayout.get(des).getCardNum())){    //is the same suit or number
							cardObjLayout.set(des, cardObjLayout.get(src));    //makes the move 
							cardObjLayout.remove(src);
						}
						else{
							break;
						}
					}
					else{
						break;
					}
				}

				if((numOfMoves == (moves / 2)) && (cardObjLayout.size() == 1)){
					System.out.println("true");
				}
				else{
					System.out.println("false");
				}

				return;	

			case "GRACECHECK":

			case "CHECKGRACE":
				if (args.length < 2 || 
			  		( args[1].equals("-") && args.length < 3) || 
			   		( args[1].equals("-") && args[2].equals("-"))
			  		) 
					{ printUsage(); return; };

				if (args[1].equals("-")) {
					layout = new LBSLayout(readIntArray(stdInScanner));
				}
				else { 
					layout = new LBSLayout(readIntArray(args[1]));
				}

				if (args.length < 3 || args[2].equals("-")) {
					workingList = readIntArray(stdInScanner);
				}
				else { 
					workingList = readIntArray(args[2]);
				}

				stdInScanner.close();

                boolean graceUsed = false;
				int movePairs = workingList.get(0);
				workingList.remove(0);

				for(int counter = 0; counter < testlayout.size(); counter++){

					Card card;
					String suit = "";
					int cardNum = 0;
					int deckNum = layout.cardAt(counter);

					if(deckNum >= 1 && deckNum <= 13){
						suit = "spades";
						cardNum = deckNum;
					}
					else if(deckNum >= 14 && deckNum <= 26){
						suit = "hearts";
						cardNum = deckNum - 13;
					}
					else if(deckNum >= 27 && deckNum <= 39){
						suit = "diamonds";
						cardNum = deckNum - 26;
					}
					else if(deckNum >= 40 && deckNum <= 52){
						suit = "clubs";
						cardNum = deckNum - 39;
					}
					else{
						System.out.println("Invalid card detected.");
					}

					card = new Card(suit, cardNum, deckNum);
					cardObjLayout.add(card);
				}

				for(int counter1 = 0; counter1 < workingList.size(); counter1 = counter1 + 2){

					int temp = counter1 + 1;
					int cardNum = workingList.get(counter1);
					int deckNum = workingList.get(temp);
					int to = deckNum;
					int from = 0;

					for(int counter2 = 0; counter2 < cardObjLayout.size(); counter2++){

						if((cardNum == cardObjLayout.get(counter2).getActualNum())){
							from = counter2;
							break;
						}
					}

					int toCardNum = cardObjLayout.get(to).getCardNum();
					int fromCardNum = cardObjLayout.get(from).getCardNum();
					String toCardSuit = cardObjLayout.get(to).getCardSuit();
					String fromCardSuit = cardObjLayout.get(from).getCardSuit();

					if((from == to + 1) && ((fromCardSuit.equals(toCardSuit)) || (fromCardNum == toCardNum))){
						cardObjLayout.set(to, cardObjLayout.get(from));
						cardObjLayout.remove(from);
					}
					else if((from == to + 3) && ((fromCardSuit.equals(toCardSuit)) || (fromCardNum == toCardNum))){
						cardObjLayout.set(to, cardObjLayout.get(from));
						cardObjLayout.remove(from);
					}
					else if((to == 0) && (from - to > 3) && !graceUsed){
						cardObjLayout.set(to, cardObjLayout.get(from));
						cardObjLayout.remove(from);
						graceUsed = true;
					}
					else{
						break;
					}
				}

				if(cardObjLayout.size() == 1){
					System.out.println("true");
				}
				else{
					System.out.println("false");
				}

				return;

			default : 
				printUsage(); 
				return;
		}
	}
}
