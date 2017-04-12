package com.company;

import checkerComponents.Game;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
	    Game game = new Game();
        String[] input;
        boolean moveTaken = false;
        while(true){
            System.out.print(game.toString());
            System.out.print("\n------------------------------\n");
            System.out.print("Input your move\n");
            input = scanner.next().split(",");
            if(input.length == 4){
                if(game.attemptStep(toInt(input[0]), toInt(input[1]), toInt(input[2]), toInt(input[3]))){
                    moveTaken = true;
                }
                else if(game.attemptJump(toInt(input[0]), toInt(input[1]), toInt(input[2]), toInt(input[3]))){
                    //TODO test double jump.
                    int currentColumn = toInt(input[2]);
                    int currentRow = toInt(input[3]);
                    while(game.checkJump(currentColumn, currentRow, currentColumn + 2, currentRow + 2)
                            || game.checkJump(currentColumn, currentRow, currentColumn + 2, currentRow - 2)
                            || game.checkJump(currentColumn, currentRow, currentColumn - 2, currentRow + 2)
                            || game.checkJump(currentColumn, currentRow, currentColumn - 2, currentRow - 2)){
                        System.out.print("A double jump (or more) is available. You must make another jump.\n");
                        input = scanner.next().split(",");
                        //TODO Add check for validity of input.
                        if(game.attemptJump(toInt(input[0]), toInt(input[1]), toInt(input[2]), toInt(input[3]))){
                            currentColumn = toInt(input[2]);
                            currentRow = toInt(input[3]);
                        }
                    }
                    moveTaken = true;
                }
            }
            if(!moveTaken){
                System.out.print("Invalid Input\n");
            }else{
                game.nextTurn();
                moveTaken = false;
            }
        }
    }

    private static int toInt(String string){
        try
        {
            return Integer.parseInt(string);
        }
        catch(NumberFormatException nfe)
        {
            return -1;
        }
    }


}
