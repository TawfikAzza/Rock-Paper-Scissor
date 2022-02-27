package rps.bll.player;

//Project imports
import javafx.util.Pair;
import rps.bll.game.IGameState;
import rps.bll.game.Move;
import rps.bll.game.Result;

//Java imports
import java.util.*;

/**
 * Example implementation of a player.
 *
 * @author smsj
 */
public class Player implements IPlayer {

    private String name;
    private PlayerType type;

    /**
     * @param name
     */
    public Player(String name, PlayerType type) {
        this.name = name;
        this.type = type;
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
        HashMap<Integer,Pair<Result,Move>> moveMap = new HashMap<>();
        Pair<Result,Move> movePair;
        HashMap<Move,Integer> mapUsedMove = new HashMap<>();
        HashMap<Move,Move> mapCounter = new HashMap<>();

        mapCounter.put(Move.Rock,Move.Paper);
        mapCounter.put(Move.Paper,Move.Scissor);
        mapCounter.put(Move.Scissor,Move.Rock);

        mapUsedMove.put(Move.Rock,0);
        mapUsedMove.put(Move.Paper,0);
        mapUsedMove.put(Move.Scissor,0);

        //Historic data to analyze and decide next move...
        ArrayList<Result> results = (ArrayList<Result>) state.getHistoricResults();
        for (Result result: results) {
            movePair = new Pair<>(result, result.getWinnerMove());
            moveMap.put(result.getRoundNumber(),movePair);
        }
        Move move;
        for (Map.Entry entry:moveMap.entrySet()) {
            Integer roundNumber = (Integer) entry.getKey();
            Pair<Result,Move> movePairRound = (Pair<Result,Move>) entry.getValue();
            System.out.println("playerType: "+movePairRound.getKey().getWinnerPlayer().getPlayerType());
            if(movePairRound.getKey().getWinnerPlayer().getPlayerType()==PlayerType.Human)
                mapUsedMove.put(movePairRound.getValue(),mapUsedMove.get(movePairRound.getValue())+1);
        }
        for (Map.Entry entry:mapUsedMove.entrySet()){
            Integer numberOfMove = (Integer) entry.getValue();
            Move movePlayed = (Move) entry.getKey();
            System.out.println("Move: "+movePlayed+" occurences: "+numberOfMove);
        }
        System.out.println("Move map: "+moveMap.size()+" result : "+results.size());
        List<Move> moveList = new ArrayList<>();
        moveList.add(Move.Rock);
        moveList.add(Move.Paper);
        moveList.add(Move.Scissor);
        moveList.add(Move.Rock);
        moveList.add(Move.Paper);
        moveList.add(Move.Scissor);
        moveList.add(Move.Rock);
        moveList.add(Move.Paper);
        moveList.add(Move.Scissor);
        moveList.add(Move.Rock);
        moveList.add(Move.Paper);
        moveList.add(Move.Scissor);
        moveList.add(Move.Rock);
        moveList.add(Move.Paper);
        moveList.add(Move.Scissor);
        moveList.add(Move.Rock);
        moveList.add(Move.Paper);
        moveList.add(Move.Scissor);
        moveList.add(Move.Rock);
        moveList.add(Move.Paper);
        moveList.add(Move.Scissor);
        Random random = new Random();
        int randNumber = random.nextInt(moveList.size());
        System.out.println("Rand: "+randNumber);
        //Implement better AI here...
        Move movePlay = mapCounter.get(moveList.get(randNumber));
        System.out.println("move: "+movePlay);
        return movePlay;
    }
}
