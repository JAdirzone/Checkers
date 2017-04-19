package controlComponents;

import checkerComponents.Game;

import java.util.ArrayList;
import java.util.Scanner;


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
     * Prompts the human player for a move. Continues until it gets valid input.
     * @return A move represented by an ArrayList of Integers.
     */
    public ArrayList<Integer> getMove(){
        System.out.println("Input your move.");
        String[] input = scanner.next().split(",");
        if(input.length % 2 == 0 && input.length >= 4){ //NOTE change to AND
            try {
                ArrayList<Integer> result =  new ArrayList<>();
                for (String string : input) {
                    result.add(Integer.parseInt(string));
                }
                if(!game.forcedJump() && game.checkStep(result)){ //Add check for forced jump here.
                    return result;
                }
                if(game.checkJump(result)){
                    return result;
                }
                System.out.println("Impossible move.");
                return getMove(); //impossible move.
            } catch (NumberFormatException nfe) {
                System.out.println("Invalid move.");
                return getMove(); //Invalid input
            }
        }
        System.out.println("Wrong number of inputs.");
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
