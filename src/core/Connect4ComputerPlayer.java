package core;

import java.util.Random;
/**
 * Create a computer that will act as a player in a game of Connect4 by choosing a column number to place a piece into
 */
public class Connect4ComputerPlayer
{
    private int colSelection;

    /**
     * Initialize the column number that the computer will drop a piece into
     */
    public Connect4ComputerPlayer()
    {
        colSelection = 0;
    }

    /**
     * Choose a random number between 1 and 7 to act as a column selection for the computer player
     * @return column selection
     */
    public int chooseRandomCol()
    {
        Random rand = new Random();
        colSelection = rand.nextInt(7)+1;
        return colSelection;
    }
}
