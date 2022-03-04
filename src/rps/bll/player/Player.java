package rps.bll.player;

//Project imports
import com.microsoft.sqlserver.jdbc.SQLServerException;
import javafx.util.Pair;
import rps.bll.game.IGameState;
import rps.bll.game.Move;
import rps.bll.game.Result;
import rps.dal.ConnectionManager;

//Java imports
import java.io.IOException;
import java.sql.Connection;
import java.util.*;

/**
 * Example implementation of a player.
 *
 * @author smsj
 */
public class Player implements IPlayer {

    private String name;
    private PlayerType type;
    private HashMap<String,Integer> mapTotalMoves = new HashMap<>();
    HashMap<Move,String> moveToString = new HashMap<>();
    HashMap<String,Move> stringToMove = new HashMap<>();
    HashMap<Move,Move> mapCounter = new HashMap<>();


    /**
     * @param name
     */
    public Player(String name, PlayerType type) {
        this.name = name;
        this.type = type;
        mapTotalMoves = getCombination();
        moveToString.clear();
        moveToString.put(Move.Rock,"R");
        moveToString.put(Move.Paper,"P");
        moveToString.put(Move.Scissor,"S");
        stringToMove.clear();
        stringToMove.put("R",Move.Rock);
        stringToMove.put("P",Move.Paper);
        stringToMove.put("S",Move.Scissor);
        mapCounter.put(Move.Rock,Move.Paper);
        mapCounter.put(Move.Paper,Move.Scissor);
        mapCounter.put(Move.Scissor,Move.Rock);

    }


    @Override
    public String getPlayerName() {
        return name;
    }


    @Override
    public PlayerType getPlayerType() {
        return type;
    }


    /**
     * Decides the next move for the bot...
     * @param state Contains the current game state including historic moves/results
     * @return Next move
     */
    @Override
    public Move doMove(IGameState state) {
        //Historic data to analyze and decide next move...
        ArrayList<Result> results = (ArrayList<Result>) state.getHistoricResults();

        Result result = null;
        Move movePlay=null;
        if(results.size()>5){
            result = results.get(results.size()-1);
            addMovePlayed(result,results);
            movePlay = mapCounter.get(searchNextMove(result,results));

        }
       if(movePlay==null)
            movePlay = randomPlay();
        return movePlay;
    }
    private Move searchNextMove(Result result,ArrayList<Result> results) {
        String strGameEntry =
                getHumanMove(results.get(results.size()-4))
                +"-"+getAIMove(results.get(results.size()-4))
                +"_"+getHumanMove(results.get(results.size()-3))
                +"-"+getAIMove(results.get(results.size()-3))
                +"_"+getHumanMove(results.get(results.size()-2))
                +"-"+getAIMove(results.get(results.size()-2));
        //Problem alternating between the Rock and PAper, the AI always plays according to the
        //last play record, the User play is stored but the counter is playing the
        String[] nextMovePossibility = {"R","P","S"};
        String nextMoveSearched =null;
        int occurences=0;
        for (int i = 0; i < nextMovePossibility.length; i++) {
            if(mapTotalMoves.get(strGameEntry+"+"+nextMovePossibility[i])>0) {
                if(occurences<mapTotalMoves.get(strGameEntry+"+"+nextMovePossibility[i])){
                    nextMoveSearched = nextMovePossibility[i];
                    occurences=mapTotalMoves.get(strGameEntry+"+"+nextMovePossibility[i]);
                    System.out.println(strGameEntry+"+"+nextMovePossibility[i]+" occurences: "+occurences);
                }
            }
        }
        System.out.println("Occurences: "+occurences);
        if(occurences==0) {
            return randomPlay();
        }
        System.out.println("Move Played: "+mapCounter.get(stringToMove.get(nextMoveSearched)));
        return stringToMove.get(nextMoveSearched);
    }
    private void addMovePlayed(Result result, ArrayList<Result> results){
        if(result.getRoundNumber()>2) {
            String strGameEntry =
                    getHumanMove(results.get(results.size()-3))
                    +"-"+getAIMove(results.get(results.size()-3))
                    +"_"+getHumanMove(results.get(results.size()-2))
                    +"-"+getAIMove(results.get(results.size()-2))
                    +"_"+getHumanMove(results.get(results.size()-1))
                    +"-"+getAIMove(results.get(results.size()-1))
                    +"+"+getHumanMove(result);
            //System.out.println("Move : "+strGameEntry);
            mapTotalMoves.put(strGameEntry,mapTotalMoves.get(strGameEntry)+1);
            //System.out.println("Move: "+strGameEntry+" entered "+" TotalMove in this category : "+mapTotalMoves.get(strGameEntry));
        }
    }
    private String getHumanMove(Result result) {
        String movePlayed=null;
        if(result.getWinnerPlayer().getPlayerType()==PlayerType.Human) {
            movePlayed = moveToString.get(result.getWinnerMove());
        }
        if(result.getLoserPlayer().getPlayerType()==PlayerType.Human) {
            movePlayed = moveToString.get(result.getLoserMove());
        }
        return movePlayed;
    }
    private String getAIMove(Result result) {
        String movePlayed=null;
        if(result.getWinnerPlayer().getPlayerType()==PlayerType.AI) {
            movePlayed = moveToString.get(result.getWinnerMove());
        }
        if(result.getLoserPlayer().getPlayerType()==PlayerType.AI) {
            movePlayed = moveToString.get(result.getLoserMove());
        }
        return movePlayed;
    }

    private Move chooseNextMove(IGameState state, HashMap<Pair<Move,Move>,Integer> moveMap) {
        int roundChecked=0;
        int highestOccurence = 0;
        Pair<Move,Move> selectedPlay = null;
        for (Map.Entry entry:moveMap.entrySet()) {
            Pair<Move,Move> movePair = (Pair<Move,Move>) entry.getKey();
            Integer roundNumber = (Integer) entry.getValue();
            HashMap<Move,Integer> mapMoveUsedAfterWin = new HashMap<>();
            mapMoveUsedAfterWin.put(Move.Rock,0);
            mapMoveUsedAfterWin.put(Move.Paper,0);
            mapMoveUsedAfterWin.put(Move.Scissor,0);
         /*   if(movePair.getKey().getWinnerPlayer().getPlayerType()==PlayerType.AI && roundChecked < roundNumber) {
               // mapMoveUsedAfterWin
            }*/
        }
        return Move.Paper;
    }
    private Move randomPlay() {
        List<Move> moveList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            moveList.add(Move.Rock);
            moveList.add(Move.Paper);
            moveList.add(Move.Scissor);
        }

        Random random = new Random();
        int randNumber = random.nextInt(moveList.size());

        return moveList.get(randNumber);
    }
    private HashMap<String,Integer> getCombination() {
        String[] firstMove = {"R","P","S"};
        String[] secondMove = {"R","P","S"};
        String[] thirdMove = {"R","P","S"};
        String[] fourthMove = {"R","P","S"};
        String[] fifthMove = {"R","P","S"};
        String[] sixthMove = {"R","P","S"};
        String[] finalMove = {"R","P","S"};
        String[] totalMoves = new String[2187];

        HashMap<String,Integer> mapTotalMove = new HashMap<>();
        int index = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    for (int l = 0; l < 3; l++) {
                        for (int m = 0; m < 3; m++) {
                            for (int n = 0; n < 3; n++) {
                                for (int o = 0; o < 3; o++) {
                                    totalMoves[index] = firstMove[i]+"-"+secondMove[j]+"_"
                                                        +thirdMove[k]+"-"+fourthMove[l]+"_"
                                                        +fifthMove[m]+"-"+sixthMove[n]+"+"+finalMove[o];
                                    index++;
                                }
                            }

                        }

                    }

                }
            }
        }

        for (int i = 0; i < totalMoves.length; i++) {
          //  System.out.println("index: "+i+" "+totalMoves[i]);
            mapTotalMove.put(totalMoves[i],0);
        }
        //System.out.println("Size: "+mapTotalMove.size());
        return mapTotalMove;
    }
    private HashMap<Pair<Move,Move>,Integer> fillMapPrediction() {
        HashMap<Pair<Move,Move>,Integer> mapToFill = new HashMap<>();
        mapToFill.put(new Pair<>(Move.Rock,Move.Rock),0);
        mapToFill.put(new Pair<>(Move.Rock,Move.Paper),0);
        mapToFill.put(new Pair<>(Move.Rock,Move.Scissor),0);
        mapToFill.put(new Pair<>(Move.Paper,Move.Rock),0);
        mapToFill.put(new Pair<>(Move.Paper,Move.Paper),0);
        mapToFill.put(new Pair<>(Move.Paper,Move.Scissor),0);
        mapToFill.put(new Pair<>(Move.Scissor,Move.Rock),0);
        mapToFill.put(new Pair<>(Move.Scissor,Move.Paper),0);
        mapToFill.put(new Pair<>(Move.Scissor,Move.Scissor),0);

        return mapToFill;
    }
}
