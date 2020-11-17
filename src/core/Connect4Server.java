package core;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class Connect4Server extends Application implements Connect4Constants
{
    sendData send = new sendData();
    private int sessionNum = 1;
    private boolean textGame;
    private boolean pvp;

    /**
     * Initializes the server. Lets player1 join a session first. That player chooses if they want to play a text-based game or a game
     * with a GUI. Player one them chooses if they want to play against another player or against the computer. If they choose the play
     * against another player, the server waits for a second player to join before starting a new thread. If player one chooses to play
     * against the computer, a new thread is created right away.
     *
     * @param stage primary stage
     * @throws Exception
     */

    @Override
    public void start(Stage stage) throws Exception
    {
        TextArea log = new TextArea();

        Scene scene = new Scene(new ScrollPane(log), 450, 200);
        stage.setTitle("Connect4Server");
        stage.setScene(scene);
        stage.show();

        new Thread(() -> {
            try
            {
                ServerSocket serverSocket = new ServerSocket(8004);
                Platform.runLater(() -> log.appendText(new Date() + ": Server started at socket 8004\n"));

                while (true)
                {
                    Platform.runLater(() -> log.appendText(new Date() + ": Wait for players to join session " + sessionNum + "\n"));
                    Socket player1 = serverSocket.accept();

                    Platform.runLater(() -> {
                        log.appendText(new Date() + ": Player 1 joined session " + sessionNum + '\n');
                        log.appendText("Player 1's IP address" + player1.getInetAddress().getHostAddress() + '\n');
                    });

                    DataInputStream fromPlayer1 = new DataInputStream(player1.getInputStream());
                    DataOutputStream toPlayer1 = new DataOutputStream(player1.getOutputStream());
                    toPlayer1.writeInt(PLAYER1);

                    textGame = fromPlayer1.readBoolean(); //Get the UI type from player1: Text Game or GUI game
                    pvp = fromPlayer1.readBoolean(); //Get the game type from player1: player vs player or player vs computer

                    if (pvp)
                    {
                        Socket player2 = serverSocket.accept();
                        Platform.runLater(() -> {
                            log.appendText(new Date() + ": Player 2 joined session " + sessionNum + '\n');
                            log.appendText("Player 2's IP address" + player2.getInetAddress().getHostAddress() + '\n');
                        });
                        DataInputStream fromPlayer2 = new DataInputStream(player2.getInputStream());
                        DataOutputStream toPlayer2 = new DataOutputStream(player2.getOutputStream());
                        toPlayer2.writeInt(PLAYER2); //Send player number to player2
                        toPlayer2.writeBoolean(textGame); //Let player2 know what type of UI the game is

                        Platform.runLater(() -> log.appendText(new Date() + ": Start a thread for session " + sessionNum++ + " \n"));
                        new Thread(new HandleASessionPVP(player1, player2)).start();
                    } else
                    {
                        Platform.runLater(() -> log.appendText(new Date() + ": Start a thread for session " + sessionNum++ + " \n"));
                        new Thread(new HandleASessionPVC(player1)).start();
                    }
                }
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * New thread handling a connect4 game with two players
     *
     */
    class HandleASessionPVP implements Runnable
    {
        private final Socket player1;
        private final Socket player2;
        private final Connect4 game;

        /**
         * Constructor initializing two players and a connect4 game
         * @param player1 player one
         * @param player2 player two
         */
        public HandleASessionPVP(Socket player1, Socket player2)
        {
            this.player1 = player1;
            this.player2 = player2;
            game = new Connect4();

        }

        /**
         * Handles a player vs player game. Gets the column selection from each player, places it in the connect4 game to determine
         * the correct placement of the player piece and the current status of the game and sends the piece placement and game
         * status to both players
         */
        @Override
        public void run()
        {
            int turn = 1;
            try
            {
                DataInputStream fromPlayer1 = new DataInputStream(player1.getInputStream());
                DataOutputStream toPlayer1 = new DataOutputStream(player1.getOutputStream());
                DataInputStream fromPlayer2 = new DataInputStream(player2.getInputStream());
                DataOutputStream toPlayer2 = new DataOutputStream(player2.getOutputStream());
                toPlayer1.writeInt(PLAYER1);

                while (true)
                {
                    if (game.getCurPlayer() == game.getPlayer1())
                    {
                        validatePlayerMove(game,fromPlayer1,toPlayer1);
                        send.sendMoveToBothPlayers(toPlayer1,toPlayer2, game.getColDrop(), game.getRowDrop(), true,false);
                        if (game.winner(game.getPlayer1Token()))
                        {
                            send.sendStatusToBothPlayers(toPlayer1,toPlayer2,PLAYER1WIN);
                            break;
                        } else if (game.winner(game.getPlayer2Token()))
                        {
                            send.sendStatusToBothPlayers(toPlayer1,toPlayer2,PLAYER2WIN);
                            break;
                        } else if (game.tieGame())
                        {
                            send.sendStatusToBothPlayers(toPlayer1,toPlayer2,TIEGAME);
                            break;
                        } else
                        {
                            send.sendStatusToBothPlayers(toPlayer1,toPlayer2,CONTINUE);
                        }
                    }
                    if (game.getCurPlayer() == game.getPlayer2())
                    {
                        validatePlayerMove(game,fromPlayer2,toPlayer2);
                        send.sendMoveToBothPlayers(toPlayer1,toPlayer2, game.getColDrop(), game.getRowDrop(), false,true);
                        if (game.winner(game.getPlayer2Token()))
                        {
                            send.sendStatusToBothPlayers(toPlayer1,toPlayer2,PLAYER2WIN);
                            break;
                        } else if (game.winner(game.getPlayer1Token()))
                        {
                            send.sendStatusToBothPlayers(toPlayer1,toPlayer2,PLAYER1WIN);
                            break;
                        } else if (game.tieGame())
                        {
                            send.sendStatusToBothPlayers(toPlayer1,toPlayer2,TIEGAME);
                            break;
                        } else
                        {
                            send.sendStatusToBothPlayers(toPlayer1,toPlayer2,CONTINUE);
                        }
                    }
                }
            } catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }
    }

    /**
     * New thread handling a connect4 game with a single player playing against a computer
     */
    class HandleASessionPVC implements Runnable
    {
        private final Socket player1;
        private final Connect4 game;
        private final Connect4ComputerPlayer computer;
        Exception exception;

        /**
         * Constructor initializing one player, a computer player, and a connect4 game
         * @param player1 player one
         */
        public HandleASessionPVC(Socket player1)
        {
            this.player1 = player1;
            computer = new Connect4ComputerPlayer();
            game = new Connect4();
        }

        /**
         * Handles a player vs computer game. Gets the column selection from player one and places it in the connect4 game to determine the correct
         * placement of the player piece and the status of the game. It also gets the move from the computer and does the same. It sends the placement
         * of the piece and the status of the game to player one.
         */
        @Override
        public void run()
        {
            int turn = 1;
            try
            {
                DataInputStream fromPlayer1 = new DataInputStream(player1.getInputStream());
                DataOutputStream toPlayer1 = new DataOutputStream(player1.getOutputStream());
                int colSelection;

                while (true)
                {
                    if (game.getCurPlayer() == game.getPlayer1())
                    {
                        validatePlayerMove(game,fromPlayer1,toPlayer1);
                        send.sendToPlayer(toPlayer1, game.getColDrop(), game.getRowDrop(), true);

                        if (game.winner(game.getPlayer1Token()))
                        {
                            toPlayer1.writeInt(PLAYER1WIN);
                            break;
                        } else if (game.winner(game.getPlayer2Token()))
                        {
                            toPlayer1.writeInt(PLAYER2WIN);
                            break;
                        } else if (game.tieGame())
                        {
                            toPlayer1.writeInt(TIEGAME);
                            break;
                        } else
                        {
                            toPlayer1.writeInt(CONTINUE);
                        }
                    }

                    if (game.getCurPlayer() == game.getPlayer2())
                    {
                        do
                        {
                            exception = null;
                            try
                            {
                                colSelection = computer.chooseRandomCol();
                                game.player2Turn(colSelection);
                            } catch (ArrayIndexOutOfBoundsException e)
                            {
                                exception = e;
                            }

                        } while(exception != null);

                        send.sendToPlayer(toPlayer1, game.getColDrop(), game.getRowDrop(), false);
                        if (game.winner(game.getPlayer2Token()))
                        {
                            toPlayer1.writeInt(PLAYER2WIN);
                            break;
                        } else if (game.winner(game.getPlayer1Token()))
                        {
                            toPlayer1.writeInt(PLAYER1WIN);
                            break;
                        } else if (game.tieGame())
                        {
                            toPlayer1.writeInt(TIEGAME);
                            break;
                        } else
                        {
                            toPlayer1.writeInt(CONTINUE);
                        }
                    }
                }
            } catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }
    }

    public void validatePlayerMove(Connect4 game, DataInputStream fromPlayer, DataOutputStream toPlayer) throws IOException
    {
        Exception exception;
        int column;
        boolean validMove;

        do
        {
            exception = null;
            try
            {
                column = fromPlayer.readInt(); //Get column selection from player
                if(game.getCurPlayer() == PLAYER1)
                    game.player1Turn(column); //attempt to place the piece in the selected column of the game
                else
                    game.player2Turn(column);
                validMove = true; //the move is valid
                toPlayer.writeBoolean(validMove); //let player know their move is valid and the piece can be placed onto the game board
            } catch (ArrayIndexOutOfBoundsException e)
            {
                exception = e;
                validMove = false; //The move is not valid
                toPlayer.writeBoolean(validMove); //Let player know the piece cannot be placed into the desired column and to pick a different column
            }

        } while(exception != null);
    }

    /**
     * Class containing helper methods to send current game information to one or both players
     */
    static class sendData
    {
        /**
         * Sends the current piece placement to both players
         *
         * @param player1 player one
         * @param player2 player two
         * @param col column the piece is placed into
         * @param row row the piece is placed into
         * @param player1Piece the current piece: true if player one piece, false if not
         * @param player2Piece the current piece: true if player two piece, false if not
         */
        public void sendMoveToBothPlayers(DataOutputStream player1, DataOutputStream player2, int col, int row, boolean player1Piece, boolean player2Piece)
        {
            try
            {
                sendToPlayer(player1, col, row, player1Piece);
                sendToPlayer(player2, col, row, player2Piece);
            } catch(IOException e)
            {
                e.printStackTrace();
            }
        }

        /**
         * Sends the current game status to both players
         *
         * @param player1 player one
         * @param player2 player two
         * @param status current game status
         */
        private void sendStatusToBothPlayers( DataOutputStream player1, DataOutputStream player2, int status)
        {
            try
            {
                player1.writeInt(status);
                player2.writeInt(status);
            } catch(IOException e)
            {
                e.printStackTrace();
            }
        }

        /**
         * Sends the current piece placement to a specified player
         *
         * @param player player to send the placement info to
         * @param col column the piece is being placed in
         * @param row row the piece is being placed in
         * @param myPiece the current piece: true if player's piece, false if not
         * @throws IOException
         */
        private void sendToPlayer(DataOutputStream player, int col, int row, boolean myPiece) throws IOException
        {
            sendCol(player, col);
            sendRow(player, row);
            player.writeBoolean(myPiece);
        }

        /**
         * Sends the column the current piece is being placed into
         *
         * @param out player the piece is being sent to
         * @param column column the piece is being placed into
         * @throws IOException
         */
        private void sendCol(DataOutputStream out, int column) throws IOException
        {
            out.writeInt(column);
        }

        /**
         * Sends the row the current piece is being placed into
         *
         * @param out player the piece is being sent to
         * @param row row the piece is being placed into
         * @throws IOException
         */
        private void sendRow(DataOutputStream out, int row) throws IOException
        {
            out.writeInt(row);
        }
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
