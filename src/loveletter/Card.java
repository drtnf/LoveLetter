package loveletter;

import java.util.Random;

/**An enumeration of the card types
 * Cards are immutable
 * **/
public enum Card {
    GUARD(1,"Guard",5),
    PRIEST(2,"Priest",2),
    BARON(3,"Baron",2),
    HANDMAID(4,"Handmaid",2),
    PRINCE(5,"Prince",2),
    KING(6,"King",1),
    COUNTESS(7,"Countess",1),
    PRINCESS(8,"Princess",1);
  
    private int value; //numerical value of card
    private String name; //String description of card
    private int count; //number of cards in the deck


    /**
     * Creates the card with value, name and count.
     * @param value the value of the card
     * @param name the name of the card
     * @param count the number of instances of the card in a standard deck
     * **/
    private Card(int value, String name, int count){
      this.value = value;
      this.name = name;
      this.count = count;
    }

    /**
     * @return the value of the card
     * **/
    public int value(){return value;}

    /**
     * @return the name of the card
     * **/
    public String toString(){return name;}

    /**
     * @return the number of times the card appears in the deck
     * **/
    public int count(){return count;}

    /**
     * Creates a shuffled deck of cards
     * @param rand a random number generator to shuffle the deck
     * @return an array of cards representing a standard deck of loveletter cards, in random order.
     * **/
    public static Card[] deal(java.util.Random rand){
      Card[] deck = new Card[16];
      int j = 0;
      for(Card c: Card.values())
        for(int i = 0; i<c.count(); i++)
          deck[j++] = c;
      for(int i = 0; i<200; i++){//make two hundred random swaps of cards
        int index1 = rand.nextInt(16);
        int index2 = rand.nextInt(16);
        Card c = deck[index1];
        deck[index1]=deck[index2];
        deck[index2]=c;
      }
      return deck;
    }


    /**
     * Creates a shuffled deck of cards, using a default random number generator
     * @return an array of cards representing a standard deck of loveletter cards, in random order.
     * **/
    public static Card[] deal(){
      return deal(new java.util.Random());
    }
  
}


