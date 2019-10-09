package loveletter;

/**
 * An abstract class to represent actions in the game LoveLetter.
 * The class is designed to be immutable.
 * @author Tim French
 **/
public class Action{
  //the player performing the action
  private Card card;
  //the player's name (for nice formatting)
  private int player;
  //The type of action
  private int target;
  //The position of the card played/discarded
  private Card guess;

  /**
   * Private constructor for creating a new action.
   * Called by the static play methods.
   * @param card the card being played
   * @param player the player performing the action
   * @param target the player targetted by this action, or the player in the event an action has no target
   * @param guess the card the player guesses in a guard action
   * @throws IllegalActionException if an illegal action.
   * **/
  private Action(Card card, int player, int target, Card guess) throws IllegalActionException{
    if(player < 0 || player >3) throw new IllegalActionException("Player out of range");
    if(card==null) throw new IllegalActionException("Null card specified in action");
    if(target < -1 || target >3) throw new IllegalActionException("Player out of range");
    this.card = card;
    this.player = player;
    this.target = target;
    this.guess = guess;
  }

  /**the card of the action
   * @return the card for the action
   **/
  public Card card(){return card;}

  /**The player who did the action
   * @return the player index who did the action
   * **/
  public int player(){return player;}

  /**the target of the action for 1,2,3,5,6 cards
   * @return the index of the targetted player for 1,2,3,5,6 cards and -1 otherwise
   * **/
  public int target(){return target;}

  /**the announced guess for 1 cards
   * @return the guessed card for GUARD actions, and null otherwise.
   * **/
  public Card guess(){return guess;}

  /**produces a string representation of the action
   * @return a string representation of the action
   * **/
  public String toString(String player, String target){
    String str = "Player "+player+" played the "+card; 
    switch(card){
      case GUARD: return str+" and guessed player "+target+" held the "+guess+".";
      case PRIEST: return str+" and asked to see player "+target+"'s card.";
      case BARON: return str+" and challenged player "+target+".";
      case HANDMAID: return str+".";
      case PRINCE: return str+" and asked player "+target+" to discard.";
      case KING: return str+" and asked player "+target+" to swap cards.";
      case COUNTESS: return str+".";
      case PRINCESS: return str+".";
      default: return str+".";               
    }
  }
 
  public String toString(){
    return toString(""+player,""+target);
  }


  /**
   * Constructs a GUARD action from the player guessing the targets card.
   * @param player the player performing the action
   * @param target the player taregtted by this action
   * @param guess the card the player guesses
   * @return the action object
   * @throws IllegalActionException if an illegal action.
   * **/
  public static Action playGuard(int player, int target, Card guess) throws IllegalActionException{
    if(target==-1) throw new IllegalActionException("Target must be specified");
    if(player == target) throw new IllegalActionException("Player cannot target themself");
    if(guess == null) throw new IllegalActionException("Player cannot guess a null card");
    if(guess == Card.GUARD) throw new IllegalActionException("Player cannot guess a guard");
    return new Action(Card.GUARD, player, target, guess);
  }

  /**
   * Constructs a PRIEST action for the player seeing the targets card.
   * @param player the player performing the action
   * @param target the player targetted by this action
   * @return the action object
   * @throws IllegalActionException if an illegal action.
   * **/
  public static Action playPriest(int player, int target) throws IllegalActionException{
    if(target==-1) throw new IllegalActionException("Target must be specified");
    if(player == target) throw new IllegalActionException("Player cannot target themself");
    return new Action(Card.PRIEST, player, target, null);
  }

  /**
   * Constructs a BARON action for the player challenging another player.
   * @param player the player performing the action
   * @param target the player targetted by this action
   * @return the action object
   * @throws IllegalActionException if an illegal action.
   * **/
  public static Action playBaron(int player, int target) throws IllegalActionException{
    if(target==-1) throw new IllegalActionException("Target must be specified");
    if(player == target) throw new IllegalActionException("Player cannot target themself");
    return new Action(Card.BARON, player, target, null);
  }
  
  /**
   * Constructs a HANDMAID action for the player.
   * @param player the player performing the action
   * @return the action object
   * **/
  public static Action playHandmaid(int player) throws IllegalActionException{
    return new Action(Card.HANDMAID, player, -1,null);
  }

  /**
   * Constructs a PRINCE action for the player requiring 
   * the targetted player to discard their card and draw a new one.
   * @param player the player performing the action
   * @param target the player targetted by this action
   * @return the action object
   * **/
  public static Action playPrince(int player, int target) throws IllegalActionException{
    if(target==-1) throw new IllegalActionException("Target must be specified");
    return new Action(Card.PRINCE, player, target, null);
  }

  /**
   * Constructs a KING action for the player requiring 
   * swapping cards with the target.
   * @param player the player performing the action
   * @param target the player targetted by this action
   * @return the action object
   * @throws IllegalActionException if an illegal action.
   * **/
  public static Action playKing(int player, int target) throws IllegalActionException{
    if(target==-1) 
      throw new IllegalActionException("Target must be specified");
    if(player==target)
      throw new IllegalActionException("A player cannot target themselves.");
    return new Action(Card.KING, player, target, null);
  }

  /**
   * Constructs a COUNTESS action for the player.
   * @param player the player performing the action
   * @return the action object
   * @throws IllegalActionException if the player is out of range
   * **/
  public static Action playCountess(int player) throws IllegalActionException{
    return new Action(Card.COUNTESS, player, -1, null);
  }

  /**
   * Constructs a PRINCESS action for the player.
   * @param player the player performing the action
   * @return the action object
   * @throws IllegalActionException if the player is out of range
   * **/
  public static Action playPrincess(int player) throws IllegalActionException{
    return new Action(Card.PRINCESS, player, -1, null);
  }
}
