import java.util.ArrayList;

public class State {
    
    private ArrayList<Card> cardList = new ArrayList<>();   //list for the current card object layout
    private ArrayList<Integer> movesMade = new ArrayList<>();   //list for the moves made to reach the current state
    private ArrayList<Integer> possibleMoves = new ArrayList<>();   //list for all the possible moves left for the current state


    //initialises the current state
    public State(ArrayList<Card> cardList, ArrayList<Integer> movesMade){

        this.cardList = cardList;
        this.movesMade = movesMade;
        possibleMoves();
    }

    //returns the current state of the puzzle which is the card object layout 
    public ArrayList<Card> getPuzzleState(){
        return cardList;
    }

    //returns the list of moves made 
    public ArrayList<Integer> getMovesMade(){
        return movesMade;
    }

    //returns a copy of the card object layout  
    public ArrayList<Card> copyLayout() {

        ArrayList<Card> copy = new  ArrayList<Card>();

        for(int i=0 ;i<cardList.size(); i++) { 
            copy.add(cardList.get(i));
        }

        return copy; 
    }

    //returns a copy of the list of moves made   
    public ArrayList<Integer> copyMoves() {

        ArrayList<Integer> copy = new  ArrayList<Integer>();

        for(int i=0 ;i<movesMade.size(); i++) { 
            copy.add(movesMade.get(i));
        }

        return copy; 
    }

    //goes through the card layout and searches for every possible move that can be made and stores it in a list 
    public void possibleMoves(){

        for(int counter = cardList.size() - 1; counter >= 0; counter--){    //stars from the right end of the layout and moves to the left

            Card currentCard = cardList.get(counter);    //gets the current card to compare with 
            int temp1 = counter - 1;    //position of the card one move to the left
            int temp2 = counter - 3;   //position of the card three move to the left

            if(counter >= 1){
                if((currentCard.getCardSuit().equals(cardList.get(temp1).getCardSuit())) || (currentCard.getCardNum() == cardList.get(temp1).getCardNum())){   //checks whether the suit or the number of the card one move to the left matches
                    possibleMoves.add(counter);   //adds the deck number of both these cards if they match
                    possibleMoves.add(temp1);
                }
            }
            if(counter >= 3){
                if((currentCard.getCardSuit().equals(cardList.get(temp2).getCardSuit())) || (currentCard.getCardNum() == cardList.get(temp2).getCardNum())){   //checks whether the suit or the number of the card three move to the left matches
                    possibleMoves.add(counter);    //adds the deck number of both these cards if they match
                    possibleMoves.add(temp2);
                }
            }
        }
    }

    //gets the list of possibles moves left that can be made 
    public ArrayList<Integer> getPossibleMoves(){
        return possibleMoves;
    }

    //updates the list of possible moves
    public void upadatePossibleMoves(ArrayList<Integer> updatedList){ 
        possibleMoves = updatedList;
    }

    //checks if there is at least one more move left that can be made
    public boolean hasNextMove(){

        if(possibleMoves.size() >= 2){
            return true; 
        }
        else{
            return false;
        }
    }

    //checks if the solution to the game has been found with the current state
    public boolean solutionFound(){

        if(cardList.size() == 1){
            return true;
        }
        else{
            return false;
        }
    }

    //returns the number of moves that have been made to reach the current state
    public int getNumOfMoves(){
        return (movesMade.size() / 2);
    }
}