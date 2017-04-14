package controlComponents;

import checkerComponents.Game;

import java.util.ArrayList;

/**
 * Created by Jay on 4/13/2017.
 */
public class aiPlayer implements player{
    private Game game; //Reference to original game
    private boolean isWhitePlayer;

    public aiPlayer(Game game, boolean isWhitePlayer){
        this.game = game;
        this.isWhitePlayer = isWhitePlayer;
    }

    public ArrayList<Integer> getMove(){
        return new ArrayList<>();
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
