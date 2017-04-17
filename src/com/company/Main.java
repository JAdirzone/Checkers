package com.company;

import checkerComponents.Game;
import controlComponents.ComputerPlayer;
import controlComponents.HumanPlayer;
import controlComponents.Player;

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
            System.out.println(game.toString());
            game.move(players[turnNumber % 2].getMove());
            playing = !game.currentPlayerWins();
            game.nextTurn();
            turnNumber++;
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
