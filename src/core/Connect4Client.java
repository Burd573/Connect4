package core;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Creates a new client for a connect4 game
 */
public class Connect4Client extends Application implements Connect4Constants
{
    private boolean myTurn;
    private char myToken;
    private char otherToken;
    private DataInputStream fromServer;
    private DataOutputStream toServer;
    private boolean continueToPlay;
    private boolean waiting;
    private int colSelection;
    private boolean turn1;
    private int player;
    private int status;
    private boolean pvp;
    private boolean textGame;
    private final char[][] textBoard;
    private int row;
    boolean validMove;

    GridPane board;
    BorderPane game;
    HBox playerSelection;
    Scanner scan;

    /**
     * Constructor initializing the variables in the game for each player
     */
    public Connect4Client()
    {
        myTurn = false;
        myToken = ' ';
        otherToken = ' ';
        continueToPlay = true;
        waiting = true;
        turn1 = true;
        row = 1;
        board = new GridPane();
        game = new BorderPane();
        playerSelection = new HBox();
        scan = new Scanner(System.in);

        textBoard = new char[ROWS][COLUMNS];
        for (char[] chars : textBoard)
        {
            Arrays.fill(chars, ' ');
        }
    }

    @Override
    public void start(Stage primaryStage)
    {
        connectToServer();
    }

    /**
     * Start a new thread handling the gameplay
     */
    private void connectToServer()
    {
        try
        {
            String host = "localhost";
            Socket socket = new Socket(host, 8004);
            fromServer = new DataInputStream(socket.getInputStream());
            toServer = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        new Thread(() -> {
            try
            {
                player = fromServer.readInt(); //Get my player number from server
                if (player == PLAYER1)
                {
                    myToken = PLAYER1TOKEN;
                    textGame(); //Ask player1 which UI they want to use
                    toServer.writeBoolean(textGame); //Let the server know which UI it will be using for this session
                    if (turn1)
                    {
                        myTurn = true; //Player1 always goes first
                    }
                    if (textGame)
                    {
                        getTextGameType(); //If it is a text game, get the game type from player1 from the console
                    }
                    else
                    {
                        getGameType(); //If a GUI game, get the game type from player1 through the GUI
                        waitForPlayerAction();
                    }
                    toServer.writeBoolean(pvp); //Let the server know which game type this session will be: player vs player or player vs computer
                    if (pvp)
                    {
                        otherToken = PLAYER2TOKEN;
                        fromServer.readInt(); //Get player number again?
                    }
                } else //if player2
                {
                    pvp = true; //if player 2, it is always a pvp game
                    myToken = PLAYER2TOKEN;
                    otherToken = PLAYER1TOKEN;
                    textGame = fromServer.readBoolean(); //Get UI for this session from server
                    if (textGame)
                    {
                        System.out.println("Waiting for player1 to move...");
                    } else
                    {
                        showGUIBoard();
                    }
                }

                while (continueToPlay)
                {
                    if (pvp)
                    {
                        switch (player)
                        {
                            case PLAYER1 -> {
                                if (textGame)
                                    playerTextGameMove(); //Get player selection from console and update/show game board
                                else
                                {
                                    playerGUIGameMove(); //Get player selection from GUI and update game board
                                }
                            }
                            case PLAYER2 -> {
                                if (textGame)
                                    playerTextGameMove(); //Get player selection from console and update/show game board
                                else
                                    moveAndUpdateGUI(); //Get player selection from GUI and update game board
                            }
                        }
                    } else //PVC game
                    {
                        if (textGame)
                        {
                            playerTextGameMove(); //Get player selection from console and update/show game board
                        } else //GUI game
                        {
                            playerGUIGameMove(); //Get player selection from GUI and update game board
                        }
                    }
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Wait for player to make a move
     *
     * @throws InterruptedException
     */
    private void waitForPlayerAction() throws InterruptedException
    {
        while (waiting)
        {
            Thread.sleep(100);
        }
        waiting = true;
    }

    /**
     * Get the last move from the server and if there is a winner show the correct message
     * depending on the game type
     *
     * @throws IOException
     */
    private void receiveInfoFromServer() throws IOException
    {
        receiveMove();
        status = fromServer.readInt(); //Get the current game status from the server

        if(textGame)
            playerWinTextGame(status); //If the game is over show result via console
        else
            playerWinGUI(status); //If the game is over show result via GUI
    }

    /**
     * Get the last move from the server and place the piece in the correct spot on the game board
     * @throws IOException
     */
    private void receiveMove() throws IOException
    {
        row = fromServer.readInt(); //Get the row the piece is to be placed in from the server
        int column = fromServer.readInt(); //Get the column the piece is to be placed in from the server
        boolean myPiece = fromServer.readBoolean(); //Get the last move's piece from the server, true if my piece, false if other player's piece

        if (!textGame)
        {
            updateBoardGUI(column, row, myPiece); //Update the GUI game board by placing the correct player piece onto the board. Note that rows/columns are reversed in gridPane vs 2d array
        } else
        {
            System.out.println("New piece placed in column: "+(column+1));
            updateTextBoard(column, myPiece); //Update the text game board by placing the piece in the selected column
        }
    }

    /**
     * Get the type of interface for the game from player one
     */
    public void getGameType()
    {
        if (textGame)
            getTextGameType(); //Get the game type(pvp or pvc) via the console
        else
            getGameTypeGUI(); //Get the game type(pvp or pvc) via the GUI
    }

    /**
     * If it is the current player's turn, set the column that the piece will be placed into
     * @param num
     */
    public void setColSelection(int num)
    {
        if (myTurn)
        {
            colSelection = num;
            myTurn = false;
            waiting = false;
        }
    }

    /**
     * Show the board for a text-based game
     */
    public void showTextBoard()
    {
        for (char[] chars : textBoard)
        {
            System.out.print("|");
            for (int j = 0; j < textBoard[0].length; j++)
            {
                System.out.print(chars[j]);
                System.out.print("|");
            }
            System.out.println();
        }
    }

    /**
     * Ask the player if the want to play with a GUI or play a text-based game. If the player enters a bad response, prompt them again
     */
    public void textGame()
    {
        Scanner scan = new Scanner(System.in);
        String userInput;
        System.out.print("Enter 'G' if you want to use a graphical interface or enter 'T' to play a text-based game: ");
        userInput = scan.next();

        while (!((userInput.equalsIgnoreCase("g")) || (userInput.equalsIgnoreCase("t"))))
        {
            System.out.println(userInput);
            System.out.print("Sorry, I did not understand that. Enter 'G' for a graphical interface or enter 'T' to play a text-based game: ");
            userInput = scan.next();
        }

        textGame = userInput.equalsIgnoreCase("t");
    }

    /**
     * If the game is text-based, ask the user if they want to play against another player or the computer. If the player enters a bad response, prompt them again
     */
    public void getTextGameType()
    {
        Scanner scan = new Scanner(System.in);
        String opponentSelection;

        System.out.print("Enter 'P' if you want to play against another player or enter 'C' to play against the computer: ");
        opponentSelection = scan.next();

        while (!((opponentSelection.equalsIgnoreCase("p")) || (opponentSelection.equalsIgnoreCase("c"))))
        {
            System.out.println(opponentSelection);
            System.out.print("Sorry, I did not understand that. Enter 'P' to play against another player or enter 'C' to play against the computer: ");
            opponentSelection = scan.next();
        }

        if (opponentSelection.equalsIgnoreCase("c"))
        {
            pvp = false;
        } else if (opponentSelection.equalsIgnoreCase("p"))
        {
            pvp = true;
        }
    }

    /**
     * Update the text-game board from the previous move and get the next move from the appropriate player
     */
    public void playerTextGameMove()
    {
        try
        {
            if (turn1 && player == PLAYER1)//on the first turn, do not receive previous move from server
            {
                getTextGameInput();
                receiveInfoFromServer();
                turn1 = false;
            } else
            {
                receiveInfoFromServer();
                if (status == CONTINUE && myTurn)
                {
                    getTextGameInput();
                    receiveInfoFromServer();
                }
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Prompt the player for their column selection in a text-based game. If the column is full or if they enter an invalid
     * value, prompt them again
     *
     * @throws IOException
     */
    public void getTextGameInput() throws IOException
    {
        int num = -1;
        while ((num < 1) || (num > COLUMNS))
        {
            System.out.print("Enter an valid column number between 1 and 7: ");
            num = scan.nextInt();
        }

        sendTextMove(num); //send the desired column to the server
        validMove = fromServer.readBoolean(); //Get info from server if move is valid or not

        while(!validMove) //if the move is not valid, get another column selection from the user. Continue until user gives a valid column
        {
            myTurn = true;
            System.out.print("That row is full. Please select another column between 1 and 7: ");
            num = scan.nextInt();
            sendTextMove(num);
            validMove = fromServer.readBoolean();
        }
        myTurn = false;
    }

    /**
     * Send the column selection in a text-based game to the server
     *
     * @param col column selection to be sent to the server
     * @throws IOException
     */
    private void sendTextMove(int col) throws IOException
    {
        toServer.writeInt(col); //Send the desired column selection to the server
    }

    /**
     * Update the text-board with the latest move
     * @param colPlaced column the piece is to be placed into
     * @param myPiece current players piece
     */
    public void updateTextBoard(int colPlaced, boolean myPiece)
    {
        if (player == PLAYER1 && myPiece)
        {
            for (int i = textBoard.length - 1; i >= 0; i--)
            {
                if (textBoard[i][colPlaced] == ' ')
                {
                    textBoard[i][colPlaced] = PLAYER1TOKEN;
                    break;
                }
            }
            myTurn = false;
            showTextBoard();
        } else if (player == PLAYER1 && !myPiece)
        {
            for (int i = textBoard.length - 1; i >= 0; i--)
            {
                if (textBoard[i][colPlaced] == ' ')
                {
                    textBoard[i][colPlaced] = PLAYER2TOKEN;
                    break;
                }
            }
            myTurn = true;
            showTextBoard();
        } else if (player == PLAYER2 && myPiece)
        {
            for (int i = textBoard.length - 1; i >= 0; i--)
            {
                if (textBoard[i][colPlaced] == ' ')
                {
                    textBoard[i][colPlaced] = PLAYER2TOKEN;
                    break;
                }
            }
            myTurn = false;
            showTextBoard();
        } else if (player == PLAYER2 && !myPiece)
        {
            for (int i = textBoard.length - 1; i >= 0; i--)
            {
                if (textBoard[i][colPlaced] == ' ')
                {
                    textBoard[i][colPlaced] = PLAYER1TOKEN;
                    break;
                }
            }
            myTurn = true;
            showTextBoard();
        }
    }

    /**
     * Display the result of the text-based game once the game is over
     * @param winner status of the game
     */
    public void playerWinTextGame(int winner)
    {
        switch (winner)
        {
            case PLAYER1WIN:
            {
                System.out.println("Player1 Wins!");
                continueToPlay = false;
                break;
            }
            case PLAYER2WIN:
            {
                System.out.println("Player2Wins!");
                continueToPlay = false;
                break;
            }
            case TIEGAME:
            {
                System.out.println("Tie Game!");
                continueToPlay = false;
                break;
            }
        }
    }

    /**
     * Get the move from the player in a GUI game
     */
    public void playerGUIGameMove()
    {
        try
        {
            if (turn1) //on the first turn, do not receive previous move from server
            {
                waitForPlayerAction();
                sendMoveGUI();
                receiveInfoFromServer();
                turn1 = false;
            } else
            {
                moveAndUpdateGUI();
            }
        } catch (IOException | InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Update the GUI board from the previous move and get the next move from the appropriate player
     */
    private void moveAndUpdateGUI()
    {
        try
        {
            receiveInfoFromServer();
            waitForPlayerAction();
            sendMoveGUI();
            receiveInfoFromServer();
        } catch (IOException | InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Send the player's move in a GUI game to the server
     *
     * @throws IOException
     */
    private void sendMoveGUI() throws IOException, InterruptedException
    {
        toServer.writeInt(colSelection); //Send the desired column selection to the server via GUI
        validMove = fromServer.readBoolean(); //Get info from server if move is valid or not

        while(!validMove) //if the move is not valid, get another column selection from the user. Continue until user gives a valid column
        {
            myTurn = true;
            waitForPlayerAction();
            toServer.writeInt(colSelection);
            validMove = fromServer.readBoolean();
        }
    }

    /**
     * If the game is over, show the appropriate result to the appropriate players
     *
     * @param winner status of the game
     */
    public void playerWinGUI(int winner)
    {
        switch (winner)
        {
            case PLAYER1WIN:
            case PLAYER2WIN:
            {
                if (myToken == PLAYER1TOKEN)
                {
                    displayResultGUI();
                } else if (myToken == PLAYER2TOKEN)
                {
                    displayResultGUI();
                }
                continueToPlay = false;
                break;
            }
            case TIEGAME:
            {
                displayResultGUI();
                continueToPlay = false;
                break;
            }
        }
    }

    /**
     * Update the GUI board with the last move made
     *
     * @param rowPlaced row that the piece is being placed into
     * @param colPlaced column that the piece is being placed into
     * @param myPiece current player's piece
     */
    public void updateBoardGUI(int rowPlaced, int colPlaced, boolean myPiece)
    {
        if (player == PLAYER1 && myPiece)
        {
            Platform.runLater(() -> board.add(getPlayer1PieceGUI(), rowPlaced, colPlaced));
            myTurn = false;
        } else if (player == PLAYER1 && !myPiece)
        {
            Platform.runLater(() -> board.add(getPlayer2PieceGUI(), rowPlaced, colPlaced));
            myTurn = true;
        } else if (player == PLAYER2 && myPiece)
        {
            Platform.runLater(() -> board.add(getPlayer2PieceGUI(), rowPlaced, colPlaced));
            myTurn = false;
        } else
        {
            Platform.runLater(() -> board.add(getPlayer1PieceGUI(), rowPlaced, colPlaced));
            myTurn = true;
        }
    }

    /**
     * Get the status of the game and determine which result will be showed to which player
     */
    public void displayResultGUI()
    {
        Platform.runLater(() -> {

            Stage stage = new Stage();
            Label label = new Label();
            if (status == PLAYER1WIN)
            {
                label.setText("Player 1 Wins!");
            } else if (status == PLAYER2WIN && !pvp)
            {
                label.setText("The Computer Wins!");
            } else if (status == PLAYER2WIN)
            {
                label.setText("Player 2 Wins!");
            } else if (status == TIEGAME)
            {
                label.setText("This game ended in a tie!");
            }
            label.setFont(new Font("Arial", 30));

            Button okButton = new Button("OK");

            BorderPane pane = new BorderPane();
            pane.setTop(label);
            pane.setBottom(okButton);
            BorderPane.setAlignment(okButton, Pos.CENTER);

            Scene scene = new Scene(pane);
            stage.setMinHeight(100);
            stage.setMinWidth(250);
            stage.setTitle("Game Over");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setScene(scene);
            stage.show();

            okButton.setOnAction(e -> Platform.exit());

        });
    }

    /**
     * Show the GUI game board
     *
     * @return the game board
     */
    public BorderPane showBoardGUI()
    {
        BorderPane pane = new BorderPane();
        board.setStyle("-fx-background-color: blue");
        board.setMaxWidth(700);
        board.setMaxHeight(600);
        for (int i = 0; i < COLUMNS; i++)
        {
            ColumnConstraints column = new ColumnConstraints(100);
            column.setHalignment(HPos.CENTER);
            board.getColumnConstraints().add(column);
        }
        for (int j = 0; j < ROWS; j++)
        {
            RowConstraints row = new RowConstraints(100);
            row.setValignment(VPos.CENTER);
            board.getRowConstraints().add(row);
        }
        for (int i = 0; i < COLUMNS; i++)
        {
            for (int j = 0; j < ROWS; j++)
            {
                Circle blankCircle = new Circle(45);
                blankCircle.setFill(Color.rgb(242, 242, 242));
                board.add(blankCircle, i, j);
                board.setAlignment(Pos.TOP_CENTER);
            }
        }
        pane.setCenter(board);
        return pane;
    }

    /**
     * Show the player information and their corresponding player piece
     *
     * @return player information
     */
    public HBox playerInfoGUI()
    {
        HBox displayPlayerInfo = new HBox(5);

        Label player1 = new Label("Player 1:");
        player1.setFont(new Font("Arial", 30));
        Label player2 = new Label("Player 2:");
        player2.setFont(new Font("Arial", 30));

        Circle player1Circle = new Circle(20);
        player1Circle.setFill(Color.RED);

        Circle player2Circle = new Circle(20);
        player2Circle.setFill(Color.BLACK);

        displayPlayerInfo.getChildren().addAll(player1, player1Circle, player2, player2Circle);
        displayPlayerInfo.setAlignment(Pos.TOP_CENTER);

        return displayPlayerInfo;
    }

    /**
     * Show the buttons representing each column and set the column selection when
     * a button is pressed
     *
     * @return row of buttons
     */
    public HBox playerSelectionGUI()
    {
        Button col1Button = new Button("Column 1");
        col1Button.setPrefWidth(100);
        col1Button.setPrefHeight(35);
        col1Button.setOnAction(e -> setColSelection(1));

        Button col2Button = new Button("Column 2");
        col2Button.setPrefWidth(100);
        col2Button.setPrefHeight(35);
        col2Button.setOnAction(e -> setColSelection(2));

        Button col3Button = new Button("Column 3");
        col3Button.setPrefWidth(100);
        col3Button.setPrefHeight(35);
        col3Button.setOnAction(e -> setColSelection(3));

        Button col4Button = new Button("Column 4");
        col4Button.setPrefWidth(100);
        col4Button.setPrefHeight(35);
        col4Button.setOnAction(e -> setColSelection(4));

        Button col5Button = new Button("Column 5");
        col5Button.setPrefWidth(100);
        col5Button.setPrefHeight(35);
        col5Button.setOnAction(e -> setColSelection(5));

        Button col6Button = new Button("Column 6");
        col6Button.setPrefWidth(100);
        col6Button.setPrefHeight(35);
        col6Button.setOnAction(e -> setColSelection(6));

        Button col7Button = new Button("Column 7");
        col7Button.setPrefWidth(100);
        col7Button.setPrefHeight(35);
        col7Button.setOnAction(e -> setColSelection(7));

        playerSelection.setAlignment(Pos.BOTTOM_CENTER);
        playerSelection.getChildren().addAll(col1Button, col2Button, col3Button, col4Button, col5Button, col6Button, col7Button);
        return playerSelection;

    }

    /**
     * Show the board of a GUI game
     */
    public void showGUIBoard()
    {
        Platform.runLater(() -> {
            Stage stage = new Stage();
            Scene scene = new Scene(game);

            game.setTop(playerInfoGUI());
            game.setCenter(playerSelectionGUI());
            game.setBottom(showBoardGUI());

            stage.setMinWidth(715);
            stage.setMinHeight(700);
            stage.setScene(scene);
            stage.show();
        });
    }

    /**
     * Determines the game type based off of button clicked and closes the popup
     *
     * @param selection          player vs player or player vs computer
     * @param gameSelectionPopUp stage to be closed upon selection
     */
    public void gameSelectionGUI(boolean selection, Stage gameSelectionPopUp)
    {
        pvp = selection;
        waiting = false;
        gameSelectionPopUp.hide();
    }

    /**
     * Get the game type of a gui game. Lets the player select if they want to play against
     * another player or the computer
     */
    public void getGameTypeGUI()
    {
        showGUIBoard();
        Platform.runLater(() -> {
            Stage stage = new Stage();
            BorderPane pane = new BorderPane();
            HBox buttons = new HBox();
            Label label = new Label("Would you like to play against another player or against the computer?");
            Button playerOption = new Button("Player");
            playerOption.setOnAction(e -> gameSelectionGUI(true, stage));
            Button computerOption = new Button("Computer");
            computerOption.setOnAction(e -> gameSelectionGUI(false, stage));
            buttons.getChildren().addAll(playerOption, computerOption);
            label.setAlignment(Pos.CENTER);
            buttons.setAlignment(Pos.CENTER);

            pane.setTop(label);
            pane.setBottom(buttons);

            Scene scene = new Scene(pane);
            stage.setTitle("Select Game Type");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setScene(scene);

            stage.show();
        });
    }

    /**
     * Get the piece representing player 1
     *
     * @return piece representing player 1
     */
    public Circle getPlayer1PieceGUI()
    {
        Circle playerCircle = new Circle(45);
        playerCircle.setFill(Color.RED);

        return playerCircle;
    }

    /**
     * Get the piece representing player 2
     *
     * @return piece representing player 2
     */
    public Circle getPlayer2PieceGUI()
    {
        Circle playerCircle = new Circle(45);
        playerCircle.setFill(Color.BLACK);

        return playerCircle;
    }

    public static void main(String[] args)
    {
        launch(args);
    }

}
