package loveletter;

/**
 * An interface for representing an agent in the game Love Letter
 * All agents must have a 0 parameter constructor
 * */
public interface Agent{


  /**
   * Reports the agent's name
   * */
  public String toString();


  /**
   * Method called at the start of a round
   * @param start the initial state of the round
   **/
  public void newRound(State start);

  /**
   * Method called when any agent performs an action. 
   * @param act the action an agent performs
   * @param results the state of play the agent is able to observe.
   * **/
  public void see(Action act, State results);

  /**
   * Perform an action after drawing a card from the deck
   * @param c the card drawn from the deck
   * @return the action the agent chooses to perform
   * @throws IllegalActionException when the Action produced is not legal.
   * */
  public Action playCard(Card c);

}


