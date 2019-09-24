## LoveLetter
LoveLetter java code and interfaces for CITS3001 AI unit at UWA.

The project will require you to research, implement and validate artifical intelligence for the card game, Love Letter. Love Letter uses a special deck of 16 cards and is a cooperative game for 2-4 players. The story of the game, is that all players are courting a princess, and are trying to have their love letter passed to the princess. This is done by giving the letter to an intermediary, or directly to the princess. These intermediaries are represented by cards, and each has an action that can affect other players in the game (essentially buring their letter and removeing them from the game). The players have only 1 cards in their hand at a time and each turn they draw one card (so they now have two cards), and choose one of the cards to play, placing it face up in front of them and following the action on that card. Depending on the player the actions are:

-Guards: You may choose any other player and guess the card they hold (but you cannot guess guard). If you are correct, they are out of the round. There are 5 of these cards, and they have value 1.
-Priest: You may choose another player and look at their card. There are two of these cards anmd they have value 2.
-Baron: You may choose another player and you comapre cards. Whoever has the lowest value is out of the round. In the case of a draw. both players remain. There are two of these cards and they have value 3.
-Handmaiden: Playing this card makes you immune to the effects of all other cards until it is your turn again. There are two of these cards and they have value 4.
-Prince: You may choose another player (or yourself) to discard their card and draw a new one from the deck. There are two of these cards, and they have value 5.
-King: You may choose another player and swap cards with them. There is one such card and it has value 6.
-Countess: This card has no effect, but it must be played if the other card in your hand has value greater than 4. There is one of these cards and it has value 7.
-Princess: Playing this automatically takes you out of the round. There is one of these cards and it has value 8.
When a card is discarded, or a player is out of the round, the card is placed face up in front of them so everyone can see. When there is only one card left in the deck, the person with the highest value wins the round. (or if everyone else is out of the round the remaining player is the winner). The first player to win four rounds winns the game.

# Rules
The offical rules, and back story are here.
For this project you will be able to work alone, or in pairs. 
You will be required to research, implement and validate agents to play Love Letter. 
You will be provided with a Java interface to implement an agent, some very basic agents, and a basic class to run a game. 
These are available here and will be updated as required. 
The documentation is [available](http://teaching.csse.uwa.edu.au/units/CITS3001/project/2019/doc/index.html), or you can compile the javadoc yourself.
A simple scipt *mkProj* is included to compile all teh code from the command line, run the main method of LoveLetter, and build the documentation.

Submission
You will be required to submit a research report (1500-2000 words), and Java source code for one or two agents (pairs must submit two agents, individuals may submit two agents). The report should include:
-A Literature review of suitable techniques: 20%
-Description and Rationale of selected technique: 20%
-Description of validation tests and metrics: 15%
-Analysis of agent performance: 15%
The source code will also be assessed on:
-The quality of the code including formatting and slection of data structures: 15%
-The performance of the agent, including in the end of semester tournament: 15%
The criterion for the assessment is found [here](http://teaching.csse.uwa.edu.au/units/CITS3001/project/2019/Criterion.pdf). 

There will be a tournament in week 12, with the rules finalised closer to that date. The tournament will involve agents playing muliple games against different agents, with high scoring agents awarded bonus marks.
For your submission include
-A pdf version of your report.
-A zip of all the source files, confuguration files, and instructions to execute your code (not a RAR!)
-a single source file Agent*studentnumber*.java (i.e. Agent[your student number]) for the agent you would like to compete in the tournament. This should not be zipped.


