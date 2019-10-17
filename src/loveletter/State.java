package loveletter;

/**This class represents the observable state of the game.
 * The class comes in two modes, one for the players, which has update operations disabled,
 * and one for the game engine, that can update the state.
 * States of players in the same game will have common data, allowing for an efficient representation.
 * **/
public class State implements Cloneable{

  
  private int player;//the player who observes this outcome, or -1 for the game engine
  private int num; //The number of players in the game
  private Card[][] discards; //the discarded cards or each player
  private int[] discardCount; //how many cards each player has discarded
  private Card[] hand; //the cards players currently hold, or null if the player has been eliminated 
  private Card[] deck; //the deck of remaining cards
  private int[] top; //the index of the top of the deck
  private boolean[][] known; //whether player knows another players card
  private boolean[] handmaid;
  private int[] scores; //the current score of each player
  private java.util.Random random;
  private int[] nextPlayer; //the index of the next player to draw a card (using Object reference so value is shared).
  private Agent[] agents;

  /**
   * Default constructor to build the initial observed state for a player
   * First player in the array will always start
   * @param random the random number generator for the deals.
   * @param agents the array of players who start the game (must be of size 2,3 or 4)
   * @throws IllegalArgumentException if the array is of the wrong size.
   * */
  public State(java.util.Random random, Agent[] agents){
    num = agents.length;
    if(num<2 || num>4)
      throw new IllegalArgumentException("incorrect number of agents");
    this.agents = agents;
    this.random = random;
    player = -1;
    scores = new int[num];
    try{
      newRound();
    }catch(IllegalActionException e){/*unreachable code, do nothing*/}
    nextPlayer=new int[1];
  }

  /**
   * Resets state for a new round, with new deck of cards, 
   * and everyone's hand and discards reset.
   * @throws IllegalActionException if this is a player state.
   * **/
  public void newRound() throws IllegalActionException{
    if(this.player!=-1) throw new IllegalActionException("Operation not permitted in player's state.");
    deck = Card.deal(random);
    discards = new Card[num][16];
    discardCount = new int[num];
    hand = new Card[num];
    handmaid = new boolean[num];
    top = new int[1];
    known = new boolean[num][num];
    for(int i = 0; i<num; i++){
      hand[i] = this.deck[top[0]++];
      known[i][i] = true;
    }
  }


  /**
   * Produces a state object for a player in the game.
   * The update methods will be disabled for that State object.
   * @param player the player for who the State object is created.
   * @throws IllegalActionException if this is a player state.
   * @throws IllegalArgumentException if player is not between 0 and numPlayers
   * **/
  public State playerState(int player) throws IllegalActionException{
    if(this.player!=-1) throw new IllegalActionException("Operation not permitted in player's state.");
    if(player<0 || num<=player) throw new IllegalArgumentException("Player out of range.");
    try{
      State s = (State)this.clone();
      s.player = player;
      return s;
    }catch(CloneNotSupportedException e){
      e.printStackTrace();
      return null;
    }
  }

  
  /**
   * checks to see if agent a targetting agent t, with card c, whilst holding card d is a legal action.
   * That is 
   * a) the player a must hold card c, 
   * b) it must be player a's turn
   * c) if the player holds the Countess, they cannot play the Prince or the King
   * d) if the action has a target, they cannot be eliminated
   * e) if the target is protected by the Handmaid and their is some player other than the target and a not protected, 
   *    then that player must be targetted instead. 
   * f) if all players are protected by the Handmaid and the player a plays a Prince, they must target themselves
   * @param a the index of the playing agent
   * @param t the index of the targeted player or -1, of no such target exists
   * @param c the card played 
   * @param drawn the card drawn
   * @throws IllegalActionException if any of these conditions hold.
   * **/      
  private void legalAction(int a, int t, Card c, Card drawn) throws IllegalActionException{
    if(hand[a]!=c && drawn!=c)
      throw new IllegalActionException("Player does not hold the played card");
    if(nextPlayer[0]!=a)//it must be the actors turn
      throw new IllegalActionException("Wrong player in action");
    if((hand[a]==Card.COUNTESS || drawn==Card.COUNTESS) && (c==Card.KING || c==Card.PRINCE))//if one of the cards is the countess, a king or prince may not be played.
      throw new IllegalActionException("Player must play the countess");
    if(t!=-1){//if this action has a target (1,2,3,5,6 cards)
      if(eliminated(t)) //you cannot target an eliminated player
        throw new IllegalActionException("The action's target is already eliminated");
      if(c==Card.PRINCE && a==t) return;//a player can always target themselves with the Prince.
      if(handmaid(t) && (!allHandmaid(a) || c==Card.PRINCE))//you cannot target a player with the handmaid
        throw new IllegalActionException("The action's target is protected by the handmaid");
    } 
  }

  /**
   * Checks to see if an action is legal given the current state of the game, for an agent who has just drawn a card.
   * That is 
   * a) the player a must hold card c, 
   * b) it must be player a's turn
   * c) if the player holds the Countess, they cannot play the Prince or the King
   * d) if the action has a target, they cannot be eliminated
   * e) if the target is protected by the Handmaid and their is some player other than the target and a not protected, 
   *    then that player must be targetted instead. 
   * f) if all players are protected by the Handmaid and the player a plays a Prince, they must target themselves
   * There are other rules (such as a player not targetting themselves) that is enforced in the Action class.
   * @param act the action to be performed
   * @param drawn the card drawn by the playing agent.
   * @throws IllegalActionException if any of these conditions hold.
   * **/      
  public boolean legalAction(Action act, Card drawn){
    if(act ==null) return false;
    try{
      legalAction(act.player(), act.target(), act.card(), drawn);
    }
    catch(IllegalActionException e){return false;}
    return true;
  }


  /**
   * Draws a card for a player from the shuffled deck. May only be performed in the game state.
   * The card is no longer available on the top of the deck. 
   * @return the top card of the deck
   * @throws IllegalActionException if an agent attempts to access this from a player state.
   * **/
  public Card drawCard() throws IllegalActionException{
    if(player!=-1) throw new IllegalActionException("operation not permitted in player's state.");
    return deck[top[0]++];
  }


  /**
   * Executes the given action of a player.
   * May only be called for non-player states (i.e. the omniscient game engine state)
   * @param act the action to be performed
   * @param card the card drawn by the actor
   * @return a plain English description of the action
   * @throws IllegalActionAxception if the state is a player state, or if the action is against the rules. 
   ***/
  public String update(Action act, Card card) throws IllegalActionException{
    if(player!= -1)//Actions may only be executed from game states 
      throw new IllegalActionException("Method cannot be called from a player state");
    int a = act.player();//actor
    int t = act.target();//target
    Card c = act.card();
    discards[a][discardCount[a]++] = c;//put played card on the top of the acting player's discard pile, required for checking actions.
    try{
       legalAction(a,t,c,card);
    }catch(IllegalActionException e){
      discardCount[a]--;
      throw e;//reset discard top
    }
    if(c==hand[a]){//if the player played the card in their hand, insert the new card into their hand.
      hand[a]=card;
      for(int p = 0; p<num; p++)
        if(p!=a) known[p][a]=false;//rescind players knowledge if a known card was played
    }
    handmaid[a]=false;
    String ret = act.toString(name(a), t!=-1?name(t):"");
    switch(c){
      case GUARD://actor plays the guard
        ret+=guardAction(a,t,act.guess());
        break;
      case PRIEST:
        ret+=priestAction(a,t);
        break;
      case BARON:
        ret+=baronAction(a,t);
        break;
      case HANDMAID:
        handmaid[a]=true;
        break;
      case PRINCE:
        ret+= princeAction(t);  
        break;
      case KING:
        ret+= kingAction(a,t);
        break;
      case COUNTESS:  
        //no update required
        break;
      case PRINCESS:
        ret+= princessAction(a);
        break;
      default: 
        throw new IllegalActionException("Illegal Action? Something's gone very wrong");
    }//end of switch
    if(roundOver()){//check for round over
      for(int i = 0; i<num; i++)
       for(int p = 0; p<num; p++) 
         known[i][p]=true;
      int winner = roundWinner();
      ret+="\nPlayer "+winner+" wins the round.";
      scores[winner]++;
      nextPlayer[0] = winner;
    }
    else{//set nextPlayer to next noneliminated player
      nextPlayer[0] = (nextPlayer[0]+1)%num; 
      while(eliminated(nextPlayer[0])) nextPlayer[0] = (nextPlayer[0]+1)%num; 
    }
    return ret;
  }

  private String guardAction(int a, int t, Card guess){
    if(allHandmaid(a))
      return "\nPlayer "+name(t)+" is protected by the Handmaid.";//no effect action
    else if(guess==hand[t]){//correct guess, target eliminated
      discards[t][discardCount[t]++] = hand[t];
      hand[t]=null;
      for(int p = 0; p<num; p++)known[p][t]=true;
      return "\nPlayer "+name(t)+" had the "+guess+" and is eliminated from the round";
    } 
    else return "\nPlayer "+name(t)+" does not have the "+guess;
  }

  private String priestAction(int a, int t){
    if(allHandmaid(a))
      return "\nPlayer "+name(t)+" is protected by the Handmaid.";//no effect action
    else known[a][t]=true;
    return "\nPlayer "+name(a)+" sees player "+name(t)+"'s card.";
  }

  private String baronAction(int a, int t){
    if(allHandmaid(a))
      return "\nPlayer "+name(t)+" is protected by the Handmaid.";//no effect action
    int elim = -1;
    if(hand[a].value()>hand[t].value()) elim = t;
    else if(hand[a].value()<hand[t].value()) elim = a;
    if(elim!=-1){
      discards[elim][discardCount[elim]++] = hand[elim];
      hand[elim]=null;
      for(int p = 0; p<num; p++) known[p][elim]=true;
      return "\nPlayer "+name(elim)+" holds the lesser card: "+discards[elim][discardCount[elim]-1]+", and is eliminated";
    }
    known[a][t]=true;
    known[t][a]=true;
    return "\n Both players hold the same card, and neither is eliminated.";
  }

  //handmaid action requires no update

  private String princeAction(int t){
    Card discard = hand[t];
    discards[t][discardCount[t]++] = discard;
    if(discard==Card.PRINCESS){
      hand[t]=null;
      for(int p = 0; p<num; p++) known[p][t]=true;
      return "\nPlayer "+name(t)+" discarded the Princess and is eliminated.";
    }
    hand[t]=deck[top[0]++];
    for(int p =0; p<num;p++) 
      if(p!=t)known[p][t]=false;
    return "\nPlayer "+name(t)+" discards the "+discard+".";
  }

  private String kingAction(int a, int t){
    if(allHandmaid(a))
      return "\nPlayer "+name(t)+" is protected by the Handmaid.";
    known[a][t]=true;
    known[t][a]=true;
    for(int p =0; p<num;p++){ 
      if(p!=t && p!=a){
        boolean tmp = known[p][t];
        known[p][t] = known[p][a];
        known[p][a] = tmp;
      }
    }
    Card tmp = hand[a];
    hand[a] = hand[t];
    hand[t] = tmp;
    return "\nPlayer "+name(a)+" and player "+name(t)+" swap cards.";
  }

  //countess action not required
  
  private String princessAction(int a){
    discards[a][discardCount[a]++] = hand[a];
    hand[a]=null;
    for(int p = 0; p< num; p++) known[p][a]=true;
    String outcome =  "\nPlayer "+name(a)+" played the Princess and is eliminated.";
    outcome += "\n Player "+name(a)+" was also holding the "+discards[a][discardCount[a]-1]+".";
    return outcome;
  }

  /**
   * returns the index of the observing player, or -1 for perfect information.
   * @return the index of the observing player, or -1 for perfect information.
   * **/
  public int getPlayerIndex(){return player;}

  /**
   * returns an iterator to go through a players discard pile, from most recent to earliest.
   * @param player the index of the player whos discard pile is sought.
   * @return an iterator to go through the discard pile, from most recently discarded to oldest discard
   * **/
  public java.util.Iterator<Card> getDiscards(int player){
    return new java.util.Iterator<Card>(){
      int p=player;
      int top=discardCount[player];
      public boolean hasNext(){return top>0;}
      public Card next() throws java.util.NoSuchElementException{
        if(hasNext()) return discards[p][--top];
        else throw new java.util.NoSuchElementException();
      }
    };
  }

  /**
   * get the card of the specified player, if known.
   * @param playerIndex the player for which we seek the card
   * @return the card the player currently holds, or null, if it is not known
   * @throws ArrayIndexoutOfBoundsException if the playerIndex is out of range.
   * **/
  public Card getCard(int playerIndex){
    if(player==-1 || known[player][playerIndex]) return hand[playerIndex];
    else return null;
  }

  /**
   *returns true if the nominated player is eliminated in the round
   * @param player the player being checked
   * @return true if and only if the player has been eliminated in the round.
   * @throws ArrayIndexoutOfBoundsException if the playerIndex is out of range.
   * **/
  public boolean eliminated(int player){
    return hand[player]==null;
  }

  /**
   * Gives the next player to play in the round
   * @return the index of the next player to play
   * **/
  public int nextPlayer(){
    return nextPlayer[0];
  }

  /**
   * Gives the number of players in the game
   * @return the number of players in the game
   * **/
  public int numPlayers(){
    return num;
  }

  /**
   * helper method to determine if the nominated player is protected by the handmaid
   * @return true if and only if the index corresponds to a player who is protected by the handmaid
   * **/
  public boolean handmaid(int player){
    if(player<0 || player >=num) return false;
    return handmaid[player];
  }

  /**
   * helper method to check if every other player other than the specified player is either eliminated or protected by the handmaid
   * @param player the player who would be playing a card
   * @return true if and only if every player other than the nominated player is eliminated or prtoected by the handmaid
   * @throws ArrayIndexoutOfBoundsException if the playerIndex is out of range.
   * **/
  public boolean allHandmaid(int player){
    boolean noAction = true;
    for(int i = 0; i<num; i++)
      noAction = noAction && (eliminated(i) || handmaid[i] || i==player); 
    return noAction;
  }

  private String name(int playerIndex){
    return agents[playerIndex].toString()+"("+playerIndex+")";
  }
  /**
   * gives the remaining size of the deck, including the burnt card
   * @return the number of cards not in players hands or discarded.
   * **/
  public int deckSize(){
    return 16-top[0];
  }

  /**
   * returns an array of the remaining cards that haven't been played yet.
   * Should be called unplayedCards???
   * @return an array of all cards not in the discard piles
   ***/
  public Card[] unseenCards(){
    int alive = 0;
    for(int p = 0; p<num; p++)
      if(!eliminated(p))alive++;
    Card[] rem = new Card[deckSize()+alive];
    int aCount = 0;
    for(int p = 0; p<num; p++)
      if(!eliminated(p)) rem[aCount++]=hand[p];
    for(int i = 0; i<deckSize(); i++) rem[alive+i] = deck[top[0]+i];
    java.util.Arrays.sort(rem);
    return rem;
  }

  /**
   * Tests to see if the round is over, either by all but one player being eliminated
   * or by all but one card being drawn from the deck.
   * @return true if and only if the round is over
   * **/
  public boolean roundOver(){
    int remaining = 0;
    for(int i=0; i<num; i++) 
      if(!eliminated(i)) remaining++;
    return remaining==1 || deckSize()<2;
  }

  /**helper method to determine the winner of the round.
   * In the unlikely event of a total draw, 
   * the player with the smallest index is the winner.
   * @return the index of the winner, or -1 if the round is not yet over.
   * **/ 
  public int roundWinner(){
    if(!roundOver()) return -1;
    int winner=-1;
    int topCard=-1;
    int discardValue=-1;
    for(int p=0; p<num; p++){
      if(!eliminated(p)){
        int dv = 0;
        for(int j=0; j<discardCount[p]; j++) dv+=discards[p][j].value();
        if(hand[p].value()>topCard || (hand[p].value()==topCard && dv>discardValue)){
          winner = p;
          topCard = hand[p].value();
          discardValue = dv;
        }
      }
    }
    return winner;
  }

  /**
   * returns the score of the specified player
   * @param player the player whose score is sought
   * @return the score of the specified player
   * **/
  public int score(int player){
    if(player<0 || player > num) return 0;
    return scores[player];}

  /**
   * confirms the game is over
   * @return true if and only if a player a acrued sufficient tokens to win the game
   * **/
  public boolean gameOver(){
    return gameWinner()!=-1;
  }

  /**
   * Gives the index of the winning player if there is one, otherwise returns -1
   * @return the index of the winning player, or -1 if the game is not yet over.
   * **/
  public int gameWinner(){
    int threshold = num==4?4:num==3?5:num==2?7:0;//sets the required threshhold for different numbers of players.
    for(int p = 0; p<num; p++)
      if(scores[p]==threshold)return p;
    return -1;
  }

}



       

