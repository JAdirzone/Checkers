package com.company;

import checkerComponents.Game;
import controlComponents.ComputerPlayer;
import controlComponents.HumanPlayer;
import controlComponents.Player;
import search.JumpNodeHead;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Game game = new Game();

        Player[] players = new Player[2];
        players[0] = new HumanPlayer(game, true);
        players[1] = new ComputerPlayer(game, false);
        //players[1] = new HumanPlayer(game, false);
        int turnNumber = 0;
        boolean playing =  true;
        while(playing){
            game.setWhiteTurn(turnNumber % 2 == 0);
            //System.out.println(game.isWhiteTurn());
            System.out.println(game.toString());
            game.move(players[turnNumber % 2].getMove());
            playing = !game.currentPlayerWins(); //Replace this line.
            if(!game.playerCanMove(true)){
                System.out.println("The black player wins!");
                playing = false;
            }if(!game.playerCanMove(false)){
                System.out.println("The white player wins!");
                playing = false;
            }
            //game.nextTurn();
            turnNumber++;
        }
        //TODO should move to next turn in the AI (also remember to undo)

        /**
        game.move(new ArrayList<>(Arrays.asList(1, 3, 2, 4)));
        game.move(new ArrayList<>(Arrays.asList(4, 6, 3, 5)));
        //game.move(new ArrayList<>(Arrays.asList(4, 6, 3, 5)));
        new JumpNodeHead(game, 2, 4, 4, 6);
        System.out.println("FINAL \n" + game.toString());
         **/
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
