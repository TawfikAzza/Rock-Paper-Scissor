package rps.gui.controller;

// Java imports
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BackgroundImage;
import rps.bll.game.GameManager;
import rps.bll.game.Move;
import rps.bll.game.Result;
import rps.bll.game.ResultType;
import rps.bll.player.IPlayer;
import rps.bll.player.Player;
import rps.bll.player.PlayerType;

import java.net.URL;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;

/**
 *
 * @author smsj
 */
public class GameViewController implements Initializable {
    @FXML
    private Label lblResult;
    @FXML
    private Label lblWinLossRatio;
    @FXML
    private ImageView imgPlayerMove,imgAiMove;
    @FXML
    private Button btnRock,btnPaper,btnScissor;
    @FXML
    private SplitPane topPane;

    private IPlayer human;
    private IPlayer bot;
    private GameManager ge;
    private Move playerMove;
    public GameViewController() {
        human = new Player("Player", PlayerType.Human);
        bot = new Player(getRandomBotName(), PlayerType.AI);
        ge = new GameManager(human, bot);
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        topPane.getStylesheets().add(getClass().getResource("/css/game.css").toExternalForm());
        // TODO
    }

    private String getRandomBotName() {
        String[] botNames = new String[] {
                "R2D2",
                "Mr. Data",
                "3PO",
                "Bender",
                "Marvin the Paranoid Android",
                "Bishop",
                "Robot B-9",
                "HAL"
        };
        int randomNumber = new Random().nextInt(botNames.length - 1);
        return botNames[randomNumber];
    }
    public String getResult(Result result) {
        String statusText = result.getType() == ResultType.Win ? "wins over " : "ties ";

        return "Round #" + result.getRoundNumber() + ":" +
                result.getWinnerPlayer().getPlayerName() +
                " (" + result.getWinnerMove() + ") " +
                statusText + result.getLoserPlayer().getPlayerName() +
                " (" + result.getLoserMove() + ")!";
    }

    public void playMove(ActionEvent actionEvent) {
        Button btnPressed = (Button) actionEvent.getSource();
        BackgroundImage img = btnPressed.getBackground().getImages().get(0);
        imgPlayerMove.setImage(img.getImage());
        //System.out.println(img.getImage().getUrl().toString().toLowerCase(Locale.ROOT));
        if(img.getImage().getUrl().toString().toLowerCase(Locale.ROOT).contains("rock.png")) {
            playerMove= Move.Rock;
        }
        if(img.getImage().getUrl().toString().toLowerCase(Locale.ROOT).contains("paper.png")) {
            playerMove= Move.Paper;
        }
        if(img.getImage().getUrl().toString().toLowerCase(Locale.ROOT).contains("scissor.png")) {
            playerMove= Move.Scissor;
        }
        playGame();
    }

    private void playGame() {
        ge.playRound(playerMove);
        Result result = null;
        int aiWinNumber=0;
        int humanWinNumber=0;
        for (Result result1:ge.getGameState().getHistoricResults()) {
            if(result1.getWinnerPlayer().getPlayerType()==PlayerType.Human && result1.getType()!=ResultType.Tie)
                humanWinNumber++;
            if(result1.getWinnerPlayer().getPlayerType()==PlayerType.AI && result1.getType()!=ResultType.Tie)
                aiWinNumber++;
            result = result1;
        }
        if(result.getType()==ResultType.Win) {
            lblResult.setText(result.getWinnerPlayer().getPlayerType()+" Wins");
        }
        if(result.getType()== ResultType.Tie) {
            lblResult.setText("It's a tie.");
        }
        if(result.getWinnerPlayer().getPlayerType()==PlayerType.Human) {
            if(result.getLoserMove()==Move.Rock) {
                BackgroundImage img = btnRock.getBackground().getImages().get(0);
                imgAiMove.setImage(img.getImage());
            }
            if(result.getLoserMove()==Move.Paper) {
                BackgroundImage img = btnPaper.getBackground().getImages().get(0);
                imgAiMove.setImage(img.getImage());
            }
            if(result.getLoserMove()==Move.Scissor) {
                BackgroundImage img = btnScissor.getBackground().getImages().get(0);
                imgAiMove.setImage(img.getImage());
            }

        }
        if(result.getWinnerPlayer().getPlayerType()==PlayerType.AI) {
            if(result.getWinnerMove()==Move.Rock) {
                BackgroundImage img = btnRock.getBackground().getImages().get(0);
                imgAiMove.setImage(img.getImage());
            }
            if(result.getWinnerMove()==Move.Paper) {
                BackgroundImage img = btnPaper.getBackground().getImages().get(0);
                imgAiMove.setImage(img.getImage());
            }
            if(result.getWinnerMove()==Move.Scissor) {
                BackgroundImage img = btnScissor.getBackground().getImages().get(0);
                imgAiMove.setImage(img.getImage());
            }
        }
        lblWinLossRatio.setText("Human wins total: "+humanWinNumber+" Ai wins total: "+aiWinNumber);
    }
}
