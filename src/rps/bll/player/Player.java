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
        /**
         * In this Method, which I am still working on and putting everything in.
         * meaning I didn't refactor a thing about it,so it can looks messy at first (and second) glance.
         * I retrieve the datas I judge necessary for the AI to do its job.
         * After some researchs, the top Rock Paper Scissor seems to all acknowledge the fact that
         * Randomness is the deciding factor between win and loss in the game of Rock Paper Scissor.
         * However, I think we can add a little thinking to the randomness, by adding into it a
         * sort of pattern recognition in the moves the human player makes throughout the game.
         * For that purpose, I created two HashMAp based on the result Collection of the GameState class.
         *
         * moveMap: will store the entirety of the moves of the game with a first argument being the
         * Round number of the game, and the second the result (containing a lot of things, please be sure to
         * check the Result class) which will give us the winner, the winner type, the winning move and so on.
         *
         * mapUsedMove: will store the amount of times a move has been used by the Human player, this will gives
         * us information on which move does the Human player tends to use the most.
         *
         * mapCounter: is a map used to store the natural counter to a move (for example, Rock against Scissor etc...)
         *
         * This section is bound to evolve as for now the only thing I do is responding in a random manner to
         * a request to play by the system, I still haven't used the Maps I described before, (aside from the mapCounter
         * to add another element of randomness to the random response)
         * */

        HashMap<Integer,Pair<Result,Move>> moveMap = new HashMap<>();
        HashMap<Pair<Move,Move>,Integer> mapPredictedMoveAfterWinning = new HashMap<>();
        HashMap<Pair<Move,Move>,Integer> mapPredictedMoveAfterLosing = new HashMap<>();
        Pair<Result,Move> movePair;
        HashMap<Move,Integer> mapUsedMove = new HashMap<>();
        HashMap<Move,Move> mapCounter = new HashMap<>();

        mapCounter.put(Move.Rock,Move.Paper);
        mapCounter.put(Move.Paper,Move.Scissor);
        mapCounter.put(Move.Scissor,Move.Rock);

        mapUsedMove.put(Move.Rock,0);
        mapUsedMove.put(Move.Paper,0);
        mapUsedMove.put(Move.Scissor,0);

        mapPredictedMoveAfterWinning = fillMapPrediction();
        mapPredictedMoveAfterLosing = fillMapPrediction();




        //Historic data to analyze and decide next move...
        ArrayList<Result> results = (ArrayList<Result>) state.getHistoricResults();
        for (Result result: results) {
            movePair = new Pair<>(result, result.getWinnerMove());
            moveMap.put(result.getRoundNumber(),movePair);
            if(result.getRoundNumber()>0 && results.get(result.getRoundNumber()-1).getLoserPlayer().getPlayerType()==PlayerType.Human) {
                if (result.getWinnerPlayer().getPlayerType() == PlayerType.Human) {
                    Pair<Move, Move> movePlayed = new Pair<>(results.get(result.getRoundNumber() - 1).getLoserMove(), result.getWinnerMove());
                    mapPredictedMoveAfterLosing.put(movePlayed,mapPredictedMoveAfterLosing.get(movePlayed)+1);
                }
                if (result.getLoserPlayer().getPlayerType() == PlayerType.Human) {
                    Pair<Move, Move> movePlayed = new Pair<>(results.get(result.getRoundNumber() - 1).getLoserMove(), result.getLoserMove());
                    mapPredictedMoveAfterLosing.put(movePlayed,mapPredictedMoveAfterLosing.get(movePlayed)+1);
                }
            }
            if(result.getRoundNumber()>0 && results.get(result.getRoundNumber()-1).getWinnerPlayer().getPlayerType()==PlayerType.Human) {
                if (result.getWinnerPlayer().getPlayerType() == PlayerType.Human) {
                    Pair<Move, Move> movePlayed = new Pair<>(results.get(result.getRoundNumber() - 1).getWinnerMove(), result.getWinnerMove());
                    mapPredictedMoveAfterWinning.put(movePlayed,mapPredictedMoveAfterWinning.get(movePlayed)+1);
                }
                if (result.getLoserPlayer().getPlayerType() == PlayerType.Human) {
                    Pair<Move, Move> movePlayed = new Pair<>(results.get(result.getRoundNumber() - 1).getWinnerMove(), result.getLoserMove());
                    mapPredictedMoveAfterWinning.put(movePlayed,mapPredictedMoveAfterWinning.get(movePlayed)+1);
                }
            }
        }
       // chooseNextMove(state,moveMap);

        for (Map.Entry entry:moveMap.entrySet()) {
            Integer roundNumber = (Integer) entry.getKey();
            Pair<Result,Move> movePairRound = (Pair<Result,Move>) entry.getValue();
            if(movePairRound.getKey().getWinnerPlayer().getPlayerType()==PlayerType.Human)
                mapUsedMove.put(movePairRound.getValue(),mapUsedMove.get(movePairRound.getValue())+1);
        }
        for (Map.Entry entry:mapUsedMove.entrySet()){
            Integer numberOfMove = (Integer) entry.getValue();
            Move movePlayed = (Move) entry.getKey();
           // System.out.println("Move: "+movePlayed+" occurences: "+numberOfMove);
        }

        getCombination();
        Move movePlay = randomPlay(mapCounter);
        return movePlay;
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
    private Move randomPlay(HashMap<Move,Move> mapCounter) {
        List<Move> moveList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            moveList.add(Move.Rock);
            moveList.add(Move.Paper);
            moveList.add(Move.Scissor);
        }

        Random random = new Random();
        int randNumber = random.nextInt(moveList.size());

        Move movePlay = mapCounter.get(moveList.get(randNumber));
        return movePlay;
    }
    private String[] getCombination() {
        String[] firstMove = {"R","P","S"};
        String[] secondMove = {"R","P","S"};
        String[] thirdMove = {"R","P","S"};
        String[] totalMoves = new String[27];
        int index = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    totalMoves[index] = firstMove[i]+"_"+secondMove[j]+"_"+thirdMove[k];
                    index++;
                }

            }

        }
        for (int i = 0; i < totalMoves.length; i++) {
            System.out.println(totalMoves[i]);
        }
        return null;
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
