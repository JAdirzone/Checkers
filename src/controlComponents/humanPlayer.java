package controlComponents;

import checkerComponents.Game;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Jay on 4/13/2017.
 */
public class humanPlayer implements player{
    private Scanner scanner;
    private Game game;
    private boolean isWhitePlayer;

    public humanPlayer(Game game, boolean isWhitePlayer){
        this.scanner = new Scanner(System.in);
        this.game = game;
        this.isWhitePlayer = isWhitePlayer;
    }

    /**
     * At this stage, input is only validated for formatting.
     * (Can it be made into an arrayList of integers of an appropriate length)
     * @return An arrayList of integers representing the desired move (Not necessarily a valid move)
     */
    public ArrayList<Integer> getMove(){
        System.out.println("Input your move.");
        String[] input = scanner.next().split(",");
        if(input.length % 2 == 1 || input.length <= 3){
            System.out.println("Invalid move. (Improper length)");
            return getMove();
        }
        try {
            ArrayList<Integer> result =  new ArrayList<>();
            for (String string : input) {
                result.add(Integer.parseInt(string));
            }
            if(!game.checkStep(result)){
                return getMove();
            }
            if(!game.checkJump(result)){
                return getMove();
            }
            //add checkJump
            return result;
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid move. (None integer value)");
            return getMove();
        }
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
