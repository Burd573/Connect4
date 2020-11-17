package ui;

import core.Connect4;
import core.Connect4ComputerPlayer;
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


public class Connect4GUI extends Application
{

    Connect4 connect4;
    BorderPane game;
    HBox playerInfo;
    GridPane board;
    HBox playerSelection;
    int colSize;
    int rowSize;
    char player;
    Connect4ComputerPlayer computer;
    int colSelection;

    boolean pvp;

    public Connect4GUI()
    {
        connect4 = new Connect4();
        game = new BorderPane();
        playerInfo = new HBox();
        board = new GridPane();
        computer = new Connect4ComputerPlayer();
        colSize = connect4.getCOLS();
        rowSize = connect4.getROWS();
        player = connect4.getPlayer1Token();
        pvp = false;
        colSelection = 0;
    }

    @Override
    public void start(Stage stage) throws Exception
    {
        stage.setTitle("Connect4 Game");
        Scene scene = new Scene(game);

        game.setTop(playerInfo());
        game.setCenter(playerSelection());
        game.setBottom(showBoard());

        stage.setMinWidth(715);
        stage.setMinHeight(700);

        stage.setScene(scene);
        stage.show();

        getGameType();
    }

    /**
     * Creates a popup asking the user if they want to play against another player or against a computer.
     * When a button is clicked indicating the user's selection the popup is closed and the correct game type
     * is initiated via the pvp variable
     */
    public void getGameType()
    {
        Stage stage = new Stage();
        BorderPane pane = new BorderPane();
        HBox buttons = new HBox();
        Label label = new Label("Would you like to play against another player or against the computer?");
        Button playerOption = new Button("Player");
        playerOption.setOnAction(e -> gameSelection(true,stage));
        Button computerOption = new Button("Computer");
        computerOption.setOnAction(e -> gameSelection(false,stage));
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

    }

    /**
     * Determines the game type based off of button clicked and closes the popup
     *
     * @param selection player vs player or player vs computer
     * @param gameSelectionPopUp stage to be closed upson selection
     */
    public void gameSelection(boolean selection, Stage gameSelectionPopUp)
    {
        pvp = selection;
        gameSelectionPopUp.hide();
    }

    /**
     * Initializes the game board
     * @return current game board
     */
    public BorderPane showBoard()
    {
        BorderPane pane = new BorderPane();

        board.setStyle("-fx-background-color: blue");
        board.setMaxWidth(700);
        board.setMaxHeight(600);

        for (int i = 0; i < colSize; i++)
        {
            ColumnConstraints column = new ColumnConstraints(100);
            column.setHalignment(HPos.CENTER);
            board.getColumnConstraints().add(column);
        }

        for (int j = 0; j < rowSize; j++)
        {
            RowConstraints row = new RowConstraints(100);
            row.setValignment(VPos.CENTER);
            board.getRowConstraints().add(row);
        }

        for (int i = 0; i < colSize; i++)
        {
            for (int j = 0; j < rowSize; j++)
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
     * Creates a horizontal row of buttons with each button corresponding to a specific
     * column on the game board. When a button is clicked, a player piece gets
     * dropped into the connected column
     *
     * @return horizontal row of buttons
     */
    public HBox playerSelection()
    {
        playerSelection = new HBox();
        Button col1Button = new Button("Column 1");
        col1Button.setPrefWidth(100);
        col1Button.setPrefHeight(35);
        col1Button.setOnAction(e -> gameOfChoice(1));

        Button col2Button = new Button("Column 2");
        col2Button.setPrefWidth(100);
        col2Button.setPrefHeight(35);
        col2Button.setOnAction(e -> gameOfChoice(2));

        Button col3Button = new Button("Column 3");
        col3Button.setPrefWidth(100);
        col3Button.setPrefHeight(35);
        col3Button.setOnAction(e -> gameOfChoice(3));

        Button col4Button = new Button("Column 4");
        col4Button.setPrefWidth(100);
        col4Button.setPrefHeight(35);
        col4Button.setOnAction(e -> gameOfChoice(4));

        Button col5Button = new Button("Column 5");
        col5Button.setPrefWidth(100);
        col5Button.setPrefHeight(35);
        col5Button.setOnAction(e -> gameOfChoice(5));

        Button col6Button = new Button("Column 6");
        col6Button.setPrefWidth(100);
        col6Button.setPrefHeight(35);
        col6Button.setOnAction(e -> gameOfChoice(6));

        Button col7Button = new Button("Column 7");
        col7Button.setPrefWidth(100);
        col7Button.setPrefHeight(35);
        col7Button.setOnAction(e -> gameOfChoice(7));

        playerSelection.setAlignment(Pos.BOTTOM_CENTER);
        playerSelection.getChildren().addAll(col1Button, col2Button, col3Button, col4Button, col5Button, col6Button, col7Button);
        return playerSelection;
    }

    /**
     * Shows which piece is represented by the corresponding player
     *
     * @return HBox with player info
     */
    public HBox playerInfo()
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
     * Logic behind player vs player game. Player one goes first and then player 2.
     * If the player chooses a column that is currently full, they are instructed to
     * pick another column.
     *
     * @param col column that player wishes to place their game piece into
     */
    public void playerVsPlayer(int col)
    {
        try
        {
            if (player == connect4.getPlayer1Token())
            {
                playerOneTurn(col);
            } else if (player == connect4.getPlayer2Token())
            {
                playerTwoTurn(col);
            }
        } catch (ArrayIndexOutOfBoundsException e)
        {
            System.out.println("no");
        }
    }

    /**
     * Logic behind player vs computer. Player one goes first and then the computer
     * If the player chooses a column that is currently full, they are instructed to
     *  pick another column.
     *
     * @param col column that player wishes to place their game piece into
     */
    public void playerVsComputer(int col)
    {
        try
        {
            if (player == connect4.getPlayer1Token())
            {
                playerOneTurn(col);
            }
        } catch (ArrayIndexOutOfBoundsException e)
        {
            System.out.println("no");
        }

        if (player == connect4.getPlayer2Token())
        {
            computerAddPiece();
        }

    }

    /**
     * Uses the pvp variable to determine whether the game is player vs player or
     * player vs computer.
     *
     * @param col column that player wished to drop their piece into
     */
    public void gameOfChoice(int col)
    {
        if(pvp == true)
        {
            playerVsPlayer(col);
        } else
        {
            playerVsComputer(col);
        }
    }

    /**
     * Creates the game piece for player one and places it in the column of their
     * choosing. Checks to see if player one wins after placing their piece or
     * if the game ended in a tie. Displays a popup with he appropriate message
     * if either case happens.
     *
     * @param col column that player wished to drop their piece into
     * @throws ArrayIndexOutOfBoundsException
     */
    public void playerOneTurn(int col) throws ArrayIndexOutOfBoundsException
    {
        Circle player1Circle = new Circle(45);
        int colSelection = col;

        connect4.player1Turn(colSelection);
        player1Circle.setFill(Color.RED);
        if (connect4.winner(player))
        {
            displayResult(connect4.getPlayer1Token());
        }
        board.add(player1Circle, connect4.getRowDrop(), connect4.getColDrop());
        switchPlayer();
    }

    /**
     * Creates the game piece for player two and places it in the column of their
     * choosing. Checks to see if player two wins after placing their piece or
     * if the game ended in a tie. Displays a popup with he appropriate message
     * if either case happens.
     *
     * @param col column that player wished to drop their piece into
     * @throws ArrayIndexOutOfBoundsException
     */
    public void playerTwoTurn(int col) throws ArrayIndexOutOfBoundsException
    {
        Circle player2Circle = new Circle(45);
        int colSelection = col;

        connect4.player2Turn(colSelection);

        player2Circle.setFill(Color.BLACK);
        if (connect4.winner(player))
        {
            displayResult(connect4.getPlayer2Token());
        }
        board.add(player2Circle, connect4.getRowDrop(), connect4.getColDrop());
        switchPlayer();

    }

    /**
     * Creates the game piece for the computer and places it in the column of its
     * choosing. Checks to see if the computer wins after placing its piece or
     * if the game ended in a tie. Displays a popup with he appropriate message
     * if either case happens.
     *
     */
    public void computerAddPiece()
    {
        if (player == connect4.getPlayer2Token() && pvp == false)
        {
            Exception exception;
            Circle computerCircle = new Circle(45);
            do
            {
                exception = null;
                int computerSelection = computer.chooseRandomCol();

                try
                {
                    connect4.player2Turn(computerSelection);
                    computerCircle.setFill(Color.BLACK);
                    if (connect4.winner(player))
                    {
                        displayResult(connect4.getPlayer2Token());
                    }
                    board.add(computerCircle, connect4.getRowDrop(), connect4.getColDrop());
                    switchPlayer();

                } catch (IllegalArgumentException e)
                {
                    exception = e;
                } catch (ArrayIndexOutOfBoundsException e)
                {
                    exception = e;
                }
            } while (exception != null);
        }

    }

    /**
     * Switches to the current player's turn
     */
    public void switchPlayer()
    {
        if (connect4.getTurn() % 2 == 1)
        {
            player = connect4.getPlayer1Token();
        } else
        {
            player = connect4.getPlayer2Token();
        }
    }

    /**
     * Displays the result of the game to the appropriate player
     *
     * @param player player to show result to
     */
    public void displayResult(char player)
    {
        Stage stage = new Stage();
        Label label = new Label();
        if(player == connect4.getPlayer1Token())
        {
            label.setText("Player 1 Wins!");
        } else if(player == connect4.getPlayer2Token() && pvp == true)
        {
            label.setText("Player 2 Wins!");
        } else if(player == connect4.getPlayer2Token() && pvp == false)
        {
            label.setText("The Computer Wins!");
        } else if(connect4.tieGame())
        {
            label.setText("This game ended in a tie!");
        }
        label.setFont(new Font("Arial", 30));

        Button okButton = new Button("OK");

        BorderPane pane = new BorderPane();
        pane.setTop(label);
        pane.setBottom(okButton);
        BorderPane.setAlignment(okButton,Pos.CENTER);

        Scene scene = new Scene(pane);
        stage.setMinHeight(100);
        stage.setMinWidth(250);
        stage.setTitle("Game Over");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UTILITY);
        stage.setScene(scene);
        stage.show();

        okButton.setOnAction(e -> Platform.exit());

    }

}

