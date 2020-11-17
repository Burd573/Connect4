package core;

/**
 * Implements logic behind a game of ConnectFour
 *
 * @author Chris Burdett
 * @version  1.0
 */
public class Connect4 implements Connect4Constants
{
    private final int ROWS = 6;
    private final int COLS = 7;

    private char[][] matrix;
    private int turn;
    private char player1Token, player2Token;
    private int player1, player2;
    private int colDrop,rowDrop;

    /**
     * Create a Connect4 object with an empty board, two players, and
     * no one has made a move yet
     */
    public Connect4()
    {
        matrix = new char[ROWS][COLS];
        turn = 1;
        player1 = PLAYER1;
        player2 = PLAYER2;
        player1Token = PLAYER1TOKEN;
        player2Token = PLAYER2TOKEN;

        for(int i = 0; i <matrix.length; i++)
        {
            for(int j = 0; j < matrix[0].length; j++)
            {
                matrix[i][j] = ' ';
            }
        }
    }

    /**
     * Get the number of rows on the game board
     * @return number of rows on the game board
     */
    public int getROWS()
    {
        return ROWS;
    }

    /**
     * Get the number of columns on the game board
     * @return number of columns on the game board
     */
    public int getCOLS()
    {
        return COLS;
    }

    /**
     * Get the column that the piece will be dropped into
     * @return the column that the piece will be dropped into
     */
    public int getColDrop()
    {
        return colDrop;
    }

    /**
     * Get the row that the piece will be dropped into
     * @return the row that the piece will be dropped into
     */
    public int getRowDrop()
    {
        return rowDrop;
    }

    /**
     * Increment the turn to keep current with the game
     */
    public void incrementTurn()
    {
        turn++;
    }

    /**
     * get the current turn
     * @return current turn
     */
    public int getTurn()
    {
        return turn;
    }

    /**
     * Get the board containing all of the current pieces
     *
     * @return the current state of the game board
     */
    public char[][] getMatrix()
    {
        return matrix;
    }

    /**
     * Get player one
     * @return player one
     */
    public int getPlayer1()
    {
        return player1;
    }

    /**
     * Get player 2
     * @return player two
     */
    public int getPlayer2()
    {
        return player2;
    }

    public char getPlayer1Token()
    {
        return player1Token;
    }

    /**
     * Get the second player
     *
     * @return player 2
     */
    public char getPlayer2Token()
    {
        return player2Token;
    }

    /**
     * Player1 drops their piece into a column of their choice. The piece
     * drops in the lowest available row in that column. The turn count
     * then increments. Throws exception if column is full.
     *
     * @param colSelection column the player drops their piece into
     * @throws ArrayIndexOutOfBoundsException
     */
    public void player1Turn(int colSelection) throws ArrayIndexOutOfBoundsException
    {
        if(!isValid(colSelection-1))
        {
            throw new ArrayIndexOutOfBoundsException();
        }

        for(int i = matrix.length-1; i >= 0; i--)
        {

            if (matrix[i][colSelection-1] == ' ')
            {
                matrix[i][colSelection-1] = player1Token;
                colDrop = i;
                rowDrop = colSelection-1;
                break;
            }
        }
        incrementTurn();
    }


    /**
     * Player2 drops their piece into a column of their choice. The piece
     * drops in the lowest available row in that column. The turn count
     * then increments. Throws exception if column is full.
     *
     * @param colSelection column the player drops their piece into
     * @throws ArrayIndexOutOfBoundsException
     */
    public void player2Turn(int colSelection) throws ArrayIndexOutOfBoundsException
    {
        if(!isValid(colSelection-1))
        {
            throw new ArrayIndexOutOfBoundsException();
        }

        for(int i = matrix.length-1; i >= 0; i--)
        {
            if (matrix[i][colSelection-1] == ' ')
            {
                matrix[i][colSelection-1] = player2Token;
                colDrop = i;
                rowDrop = colSelection-1;
                break;
            }
        }
        incrementTurn();
    }

    /**
     * Verifies that the column the player chooses is valid and has an
     * open spot available to place the game piece.
     *
     * @param col column the player want to drop their piece into
     * @return true if column is valid, false if column is not valid
     */
    public boolean isValid(int col)
    {
        if((col >= 0 && col < matrix[0].length) && (matrix[0][col] == ' '))
        {
            return true;
        } else
        {
            return false;
        }
    }

    /**
     * Checks if there are no more moves left to make
     * @return true if there are no moves available, false if there are
     */
    public boolean tieGame()
    {
        return turn == 43;
    }

    /**
     * Checks the game board to see if there are four player pieces in a row.
     * Checks for horizontal, vertical, up-right diagonal, and down-right
     * diagonal
     *
     * @param player to check if they have four in a row
     * @return true if player has 4 in a row, false if they don't
     */
    public boolean winner(char player)
    {
        //check horizontal
        for(int i = 0; i < matrix.length; i++)
        {
            for(int j = 0; j < matrix[0].length-3; j++)
            {
                if (matrix[i][j] == player &&
                        matrix[i][j + 1] == player &&
                        matrix[i][j + 2] == player &&
                        matrix[i][j + 3] == player)
                {
                    return true;
                }
            }
        }
        //check vertical
        for(int i = 0; i < matrix.length - 3; i++)
        {
            for (int j = 0; j < matrix[0].length; j++)
            {
                if (matrix[i][j] == player &&
                        matrix[i + 1][j] == player &&
                        matrix[i + 2][j] == player &&
                        matrix[i + 3][j] == player)
                {
                    return true;
                }
            }
        }
        //check diagonal up, right
        for(int i = 3; i < matrix.length; i++)
        {
            for (int j = 0; j < matrix[0].length-3; j++)
            {
                if (matrix[i][j] == player &&
                        matrix[i - 1][j + 1] == player &&
                        matrix[i - 2][j + 2] == player &&
                        matrix[i - 3][j + 3] == player)
                {
                    return true;
                }
            }
        }
        //check diagonal down, right
        for(int i = 0; i < matrix.length - 3; i++)
        {
            for (int j = 0; j < matrix[0].length-3; j++)
            {
                if (matrix[i][j] == player &&
                        matrix[i + 1][j + 1] == player &&
                        matrix[i + 2][j + 2] == player &&
                        matrix[i + 3][j + 3] == player)
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Get the current player
     *
     * @return current player
     */
    public int getCurPlayer()
    {
        if (getTurn() % 2 == 1)
        {
            return getPlayer1();
        } else
        {
            return getPlayer2();
        }
    }
}