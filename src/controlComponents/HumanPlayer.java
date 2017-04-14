package controlComponents;

import checkerComponents.Game;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Jay on 4/13/2017.
 */
public class HumanPlayer implements Player {
    private Scanner scanner;
    private Game game;
    private boolean isWhitePlayer;

    public HumanPlayer(Game game, boolean isWhitePlayer){
        this.scanner = new Scanner(System.in);
        this.game = game;
        this.isWhitePlayer = isWhitePlayer;
    }

    /**
     * @return An arrayList of integers representing a valid move
     */
    //TODO Clean up. Add check for forced jump.
    public ArrayList<Integer> getMoveOld(){
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
            return result; //successful case
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid move. (None integer value)");
            return getMove();
        }
    }

    public ArrayList<Integer> getMove(){
        System.out.println("Input your move.");
        String[] input = scanner.next().split(",");
        if(input.length % 2 == 0 || input.length >= 4){
            try {
                ArrayList<Integer> result =  new ArrayList<>();
                for (String string : input) {
                    result.add(Integer.parseInt(string));
                }
                if(game.checkStep(result)){ //Add check for forced jump here.
                    return result;
                }
                if(game.checkJump(result)){
                    return result;
                }
                System.out.println("Invalid move.");
                return getMove(); //impossible move.
            } catch (NumberFormatException nfe) {
                System.out.println("Invalid move.");
                return getMove(); //Invalid input
            }
        }
        System.out.println("Invalid Move.");
        return getMove(); //Wrong number of inputs.
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
