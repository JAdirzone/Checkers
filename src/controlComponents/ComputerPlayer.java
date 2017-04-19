package controlComponents;

import checkerComponents.Game;
import search.Node;

import java.util.ArrayList;

/**
 * The AI controlled player
 */
public class ComputerPlayer implements Player {
    private Game game; //Reference to original game
    private boolean isWhitePlayer;

    public ComputerPlayer(Game game, boolean isWhitePlayer){
        this.game = game;
        this.isWhitePlayer = isWhitePlayer;
    }

    /**
     * Creates and return the AI player's next move.
     * @return
     */
    public ArrayList<Integer> getMove(){
        Node tree = new Node(game, true, 0);
        ArrayList<Integer> result = tree.getBestMove(); //TODO for debugging purposes. Condense
        return result;
    }

    public String getColorString(){
        if(isWhitePlayer){
            return "white";
        }
        return "black";
    }

    public String getColorStringCapital(){
        if(isWhitePlayer){
            return "White";
        }
        return "Black";
    }
}
