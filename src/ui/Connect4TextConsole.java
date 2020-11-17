package ui;

import core.Connect4;
import core.Connect4ComputerPlayer;
import javafx.application.Application;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Creates a new Connect4 game with either two players or one player and a computer
 */
public class Connect4TextConsole
{
    Connect4 game;
    Connect4ComputerPlayer computer = new Connect4ComputerPlayer();
    Scanner scan = new Scanner((System.in));

    private char p1;
    private char p2;
    private char[][] grid;
    private int colSelection;
    private Exception exception;

    /**
     * Initializes the players and the game board of the Connect4 game
     */
    public Connect4TextConsole()
    {
        game = new Connect4();
        p1 = game.getPlayer1Token();
        p2 = game.getPlayer2Token();
        grid = game.getMatrix();
        colSelection = 0;
        exception = null;
    }

    /**
     * Prompts player1 to enter the column number they wish to drop their piece into. If the
     * column is full or if they choose a column that does not exist, prompts them for another value.
     */
    public void player1Turn()
    {
        do
        {
            exception = null;
            System.out.print("Player 1 Enter Column Number: ");
            try
            {

                colSelection = scan.nextInt();
                game.player1Turn(colSelection);
                showBoard();

            } catch(InputMismatchException e)
            {
                scan.next();
                System.out.println("Invalid Selection. Please enter a number: ");
                exception = e;

            }catch (ArrayIndexOutOfBoundsException e)
            {
                System.out.println("Invalid Selection");
                exception = e;
            }
        } while (exception != null);
    }
    /**
     * Prompts player2 to enter the column number they wish to drop their piece into. If the
     * column is full or if they choose a column that does not exist, prompts them for another value.
     * Only applicable if player1 chooses to play against another player.
     */
    public void player2Turn()
    {
        do
        {
            exception = null;
            System.out.print("Player 2 Enter Column Number: ");
            try
            {
                colSelection = scan.nextInt();
                game.player2Turn(colSelection);
                showBoard();
            } catch(InputMismatchException e)
            {
                scan.next();
                System.out.println("Invalid Selection. Please enter a number: ");
                exception = e;

            }catch (ArrayIndexOutOfBoundsException e)
            {
                System.out.println("Invalid Selection");
                exception = e;
            }
        } while (exception != null);
    }

    /**
     * Gets the value that the computer wishes to drop its piece into. If the column
     * is full, the computer picks another column. The computer acts as player2 and is
     * only applicable is player1 chooses to play against the computer
     */
    public void computerTurn()
    {
        do
        {
            exception = null;
            int computerSelection = computer.chooseRandomCol();
            System.out.println("Computer's Chooses: " + computerSelection);
            try
            {
                game.player2Turn(computerSelection);
                showBoard();
            } catch (ArrayIndexOutOfBoundsException e)
            {
                exception = e;
            }

        } while (exception != null);
    }

        public void showBoard()
    {
        for(int i = 0; i < game.getMatrix().length; i++)
        {
            System.out.print("|");
            for(int j = 0; j < game.getMatrix()[0].length; j++)
            {
                System.out.print(game.getMatrix()[i][j]);
                System.out.print("|");
            }
            System.out.println();
        }
    }

    /**
     * Checks to see if the maximum number of moves has been made(tie) or
     * if there is four in a row for the given player. If either case is true,
     * displays the results on the screen.
     *
     * @param player to check if they have 4 in a row
     * @return true if game is over, false if game is not over
     */
    public boolean showResultIfOver(char player)
    {
        int playerNum = 0;
        if(player =='X')
            playerNum = 1;
        if(player == 'O')
            playerNum = 2;

        if (game.tieGame())
        {
            System.out.println("This game ended in a tie");
            return true;
        }
        if (game.winner(player))
        {
            System.out.println("Player " + playerNum + " Wins!");
            return true;
        }
        return false;
    }

    /**
     * Sets up game if player chooses to play against another player. Player one goes first
     * and it checks if the game is over by tie or victory. If the game is not over, player2
     * takes a turn and it checks for game over again. Continues until there is a winner or a tie
     */
    public void playAgainstPlayer()
    {
        while (!(game.winner(p1) && !(game.winner(p2) && !(game.tieGame()))))
        {
            player1Turn();
            if(showResultIfOver(p1))
                break;
            player2Turn();
            if(showResultIfOver(p2))
                break;
        }
    }
    /**
     * Sets up game if player chooses to play against the computer. Player one goes first
     * and it checks if the game is over by tie or victory. If the game is not over, the computer,
     * acting as player2 takes a turn and it checks for game over again. Continues until there is a
     * winner or a tie
     */
    public void playAgainstComputer()
    {
        while (!(game.winner(p1)) && !(game.winner(p2) && !(game.tieGame())))
        {
            player1Turn();
            if(showResultIfOver(p1))
                break;
            computerTurn();
            if(showResultIfOver(p2))
                break;
        }
    }

    /**
     * Sets up the initial screen for player1. Prompts player 1 to enter whether they
     * want to play against another player or the computer. Sets up appropriate game
     * according to the users selection. If player1 enters an incorrect value, it prompts
     * the player to enter the correct value.
     */
    public void playGame()
    {
        String opponentSelection = null;

        System.out.print("Enter 'P' if you want to play against another player or enter 'C' to play against the computer: ");
        opponentSelection = scan.next();

        while(!((opponentSelection.equalsIgnoreCase("p")) || (opponentSelection.equalsIgnoreCase("c"))))
        {
            System.out.println(opponentSelection);
            System.out.print("Sorry, I did not understand that. Enter 'P' to play against another player or enter 'C' to play against the computer: ");
            opponentSelection = scan.next();
        }

        if(opponentSelection.equalsIgnoreCase("c"))
        {
            playAgainstComputer();
        } else if(opponentSelection.equalsIgnoreCase("p"))
        {
            playAgainstPlayer();
        }
    }

    /**
     * Check if the user wants to play a text-based game
     * @return true if the user wants to play without the GUI, false if they want to use the GUI
     */
    public boolean textGame()
    {
        String userInput = null;
        System.out.print("Enter 'G' if you want to use a graphical interface or enter 'T' to play a text-based game: ");
        userInput = scan.next();

        while(!((userInput.equalsIgnoreCase("g")) || (userInput.equalsIgnoreCase("t"))))
        {
            System.out.println(userInput);
            System.out.print("Sorry, I did not understand that. Enter 'G' to play against another player or enter 'T' to play against the computer: ");
            userInput = scan.next();
        }

        if (userInput.equalsIgnoreCase("t"))
        {
            Connect4TextConsole game = new Connect4TextConsole();
            game.playGame();
            return true;
        } else
        {
            return false;
        }

    }

    public static void main(String[] args)
    {
        Connect4TextConsole game = new Connect4TextConsole();
        if(game.textGame())
        {
            game.playGame();
        } else
        {
            Application.launch(Connect4GUI.class, args);
        }
    }
}
