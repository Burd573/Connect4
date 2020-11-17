package test;

import core.Connect4;
import core.Connect4ComputerPlayer;
import core.Connect4Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class Connect4Testing implements Connect4Constants
{
    Connect4 game;
    Connect4ComputerPlayer computer;

    @BeforeEach
    void setUp()
    {
        game = new Connect4();
        computer = new Connect4ComputerPlayer();
    }

    @Test
    void numRows()
    {
        assertEquals(ROWS, game.getROWS());
    }

    @Test
    void numCols()
    {
        assertEquals(COLUMNS, game.getCOLS());
    }

    @Test
    void playerOne()
    {
        assertEquals(PLAYER1,game.getPlayer1());
    }

    @Test
    void playerTwo()
    {
        assertEquals(PLAYER2,game.getPlayer2());
    }

    @Test
    void player1Token()
    {
        assertEquals(PLAYER1TOKEN, game.getPlayer1Token());
    }

    @Test
    void player2Token()
    {
        assertEquals(PLAYER2TOKEN, game.getPlayer2Token());
    }

    @Test
    void invalidMovePlayer1()
    {
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            for (int i = 8; i < 50; i++)
            {
                game.player1Turn(i);
            }
        });

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            for (int i = -50; i <= 0; i++)
            {
                game.player1Turn(i);
            }
        });
    }

    @Test
    void invalidMovePlayer2()
    {
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            for (int i = 8; i < 50; i++)
            {
                game.player2Turn(i);
            }
        });

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            for (int i = -50; i <= 0; i++)
            {
                game.player2Turn(i);
            }
        });
    }

    @Test
    void validMove()
    {
        for (int i = COLUMNS; i < 50; i++)
        {
            assertFalse(game.isValid(i));
        }

        for (int i = -50; i < 0; i++)
        {
            assertFalse(game.isValid(i));
        }

        for(int i = 1; i < COLUMNS; i++)
        {
            assertTrue(game.isValid(i));
        }
    }

    @Test
    void winnerVertical()
    {
        game.player1Turn(1);
        assertFalse(game.winner(PLAYER1TOKEN));
        game.incrementTurn();
        game.player1Turn(1);
        assertFalse(game.winner(PLAYER1TOKEN));
        game.incrementTurn();
        game.player1Turn(1);
        assertFalse(game.winner(PLAYER1TOKEN));
        game.incrementTurn();
        game.player1Turn(1);
        assertTrue(game.winner(PLAYER1TOKEN));

        game = new Connect4();
        game.player2Turn(7);
        assertFalse(game.winner(PLAYER2TOKEN));
        game.incrementTurn();
        game.player2Turn(7);
        assertFalse(game.winner(PLAYER2TOKEN));
        game.incrementTurn();
        game.player2Turn(7);
        assertFalse(game.winner(PLAYER2TOKEN));
        game.incrementTurn();
        game.player2Turn(7);
        assertTrue(game.winner(PLAYER2TOKEN));
    }

    @Test
    void winnerHorizontal()
    {
        game.player1Turn(1);
        assertFalse(game.winner(PLAYER1TOKEN));
        assertFalse(game.winner(PLAYER2TOKEN));
        game.incrementTurn();
        game.player1Turn(2);
        assertFalse(game.winner(PLAYER1TOKEN));
        assertFalse(game.winner(PLAYER2TOKEN));
        game.incrementTurn();
        game.player1Turn(3);
        assertFalse(game.winner(PLAYER1TOKEN));
        assertFalse(game.winner(PLAYER2TOKEN));
        game.incrementTurn();
        game.player1Turn(4);
        assertTrue(game.winner(PLAYER1TOKEN));
        assertFalse(game.winner(PLAYER2TOKEN));

        game = new Connect4();
        game.player2Turn(7);
        assertFalse(game.winner(PLAYER1TOKEN));
        assertFalse(game.winner(PLAYER2TOKEN));
        game.incrementTurn();
        game.player2Turn(6);
        assertFalse(game.winner(PLAYER1TOKEN));
        assertFalse(game.winner(PLAYER2TOKEN));
        game.incrementTurn();
        game.player2Turn(5);
        assertFalse(game.winner(PLAYER1TOKEN));
        assertFalse(game.winner(PLAYER2TOKEN));
        game.incrementTurn();
        game.player2Turn(4);
        assertFalse(game.winner(PLAYER1TOKEN));
        assertTrue(game.winner(PLAYER2TOKEN));
    }

    @Test
    void winnerUpRightDiagonal()
    {
        game.player1Turn(1);
        assertFalse(game.winner(PLAYER1TOKEN));
        assertFalse(game.winner(PLAYER2TOKEN));
        game.player2Turn(2);
        assertFalse(game.winner(PLAYER1TOKEN));
        assertFalse(game.winner(PLAYER2TOKEN));
        game.player1Turn(2);
        assertFalse(game.winner(PLAYER1TOKEN));
        assertFalse(game.winner(PLAYER2TOKEN));
        game.player2Turn(3);
        assertFalse(game.winner(PLAYER1TOKEN));
        assertFalse(game.winner(PLAYER2TOKEN));
        game.player1Turn(3);
        assertFalse(game.winner(PLAYER1TOKEN));
        assertFalse(game.winner(PLAYER2TOKEN));
        game.player2Turn(4);
        assertFalse(game.winner(PLAYER1TOKEN));
        assertFalse(game.winner(PLAYER2TOKEN));
        game.player1Turn(3);
        assertFalse(game.winner(PLAYER1TOKEN));
        assertFalse(game.winner(PLAYER2TOKEN));
        game.player2Turn(4);
        assertFalse(game.winner(PLAYER1TOKEN));
        assertFalse(game.winner(PLAYER2TOKEN));
        game.player1Turn(4);
        assertFalse(game.winner(PLAYER1TOKEN));
        assertFalse(game.winner(PLAYER2TOKEN));
        game.incrementTurn();
        game.player1Turn(4);
        assertTrue(game.winner(PLAYER1TOKEN));
        assertFalse(game.winner(PLAYER2TOKEN));
    }

    @Test
    void winnerDownRightDiagonal()
    {
        game.player1Turn(1);
        assertFalse(game.winner(PLAYER1TOKEN));
        assertFalse(game.winner(PLAYER2TOKEN));
        game.player2Turn(1);
        assertFalse(game.winner(PLAYER1TOKEN));
        assertFalse(game.winner(PLAYER2TOKEN));
        game.player1Turn(1);
        assertFalse(game.winner(PLAYER1TOKEN));
        assertFalse(game.winner(PLAYER2TOKEN));
        game.player2Turn(1);
        assertFalse(game.winner(PLAYER1TOKEN));
        assertFalse(game.winner(PLAYER2TOKEN));
        game.player1Turn(2);
        assertFalse(game.winner(PLAYER1TOKEN));
        assertFalse(game.winner(PLAYER2TOKEN));
        game.player2Turn(2);
        assertFalse(game.winner(PLAYER1TOKEN));
        assertFalse(game.winner(PLAYER2TOKEN));
        game.player1Turn(3);
        assertFalse(game.winner(PLAYER1TOKEN));
        assertFalse(game.winner(PLAYER2TOKEN));
        game.player2Turn(2);
        assertFalse(game.winner(PLAYER1TOKEN));
        assertFalse(game.winner(PLAYER2TOKEN));
        game.player1Turn(5);
        assertFalse(game.winner(PLAYER1TOKEN));
        assertFalse(game.winner(PLAYER2TOKEN));
        game.player2Turn(3);
        assertFalse(game.winner(PLAYER1TOKEN));
        assertFalse(game.winner(PLAYER2TOKEN));
        game.incrementTurn();
        game.player2Turn(4);
        assertFalse(game.winner(PLAYER1TOKEN));
        assertTrue(game.winner(PLAYER2TOKEN));
    }

    @Test
    void getCurPlayer()
    {
        assertEquals(PLAYER1, game.getCurPlayer());
        game.incrementTurn();
        assertEquals(PLAYER2, game.getCurPlayer());
        game.incrementTurn();
        assertEquals(PLAYER1, game.getCurPlayer());
        game.incrementTurn();
        assertEquals(PLAYER2, game.getCurPlayer());
    }

    @Test
    void testPiecePlacement()
    {
        game.player1Turn(1);
        assertEquals(5,game.getColDrop());
        assertEquals(0,game.getRowDrop());
        game.player2Turn(1);
        assertEquals(4,game.getColDrop());
        assertEquals(0,game.getRowDrop());
        game.player1Turn(2);
        assertEquals(5,game.getColDrop());
        assertEquals(1,game.getRowDrop());
        game.player2Turn(2);
        assertEquals(4,game.getColDrop());
        assertEquals(1,game.getRowDrop());
    }

    @Test
    void gameTied()
    {
        assertFalse(game.tieGame());

        for(int i= 0; i < 42; i++)
        {
            game.incrementTurn();
        }
        assertTrue(game.tieGame());
    }

    @Test
    void computerSelection()
    {
        int itr = 300;
        int num = 0;
        for(int i = 0; i < itr; i++)
        {
            int colSelection = computer.chooseRandomCol();
            if(colSelection >= 1 && colSelection <= COLUMNS)
            {
                num++;
            }
        }

        assertEquals(itr,num);
    }

}
