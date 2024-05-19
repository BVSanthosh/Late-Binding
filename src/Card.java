public class Card {

    private  String suit;   //suit of the card
    private int num;   //the equivalent number of the card
    private int actualNum;    //number of the card as read from the layout

    //initialises each card read 
    public Card(String suit, int num, int actualNum){
        this.suit = suit;
        this.num = num;
        this.actualNum = actualNum;
    }

    //returns the suit
    public String getCardSuit(){
        return suit;
    }

    //returns the equivalent card number
    public int getCardNum(){
        return num;
    }

    //returns the card number
    public int getActualNum(){
        return actualNum;
    }
}