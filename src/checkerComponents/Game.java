package checkerComponents;

import java.util.ArrayList;

/**
 * Created by Jay on 3/22/2017.
 */

public class Game {
    private Checker[][]board;
    private boolean whiteTurn;

    public Game(){
         board = new Checker[8][8];
         for(int row = 1; row <= 8; row++){
             for(int column = 1; column <= 8; column++){
                 if(!checkDark(column, row)){
                     board[column - 1][row - 1] = Checker.UNUSABLE;
                 }
                 else if(row <= 3){
                     board[column - 1][row - 1] = Checker.WHITE;
                 }
                 else if(row >= 6){
                     board[column - 1][row - 1] = Checker.BLACK;
                 }
                 else{
                     board[column - 1][row - 1] = Checker.EMPTY;
                 }
             }
         }
         whiteTurn = true;
    }

    public String toString(){
        String result = new String();
        for(int row = 7; row >= 0; row--){
            for(int column = 0; column <= 7; column++){
                switch(board[column][row]){
                    case BLACK : result += "B"; break;
                    case WHITE : result +="W"; break;
                    case BLACKKING : result += "D"; break;
                    case WHITEKING : result += "V"; break;
                    case EMPTY : result += "E"; break;
                    case UNUSABLE : result += " "; break;
                    default : result += "$";
                }
                result += "|";
            }
            result += "\n";
        }
        return result;
    }

    //Don't use. Will remove when comfortable
    public boolean attemptStep(int column, int row, int targetColumn, int targetRow){
        if(checkStep(column, row, targetColumn, targetRow)){
            Checker movingChecker = board[column - 1][row -1];
            board[column - 1][row - 1] = Checker.EMPTY;
            board[targetColumn - 1][targetRow - 1] = movingChecker;//Pass by value, or reference?
            return true;
        }
        return false;
    }
    //Don't use! Will remove when comfortable
    public boolean attemptJump(int column, int row, int targetColumn, int targetRow){
        if(checkJump(column, row, targetColumn, targetRow)){
            Checker movingChecker = board[column - 1][row - 1];
            board[column - 1][row - 1] = Checker.EMPTY;
            board[targetColumn - 1][targetRow - 1] = movingChecker;
            board[(targetColumn - column) / 2 + column - 1][(targetRow - row) / 2 + row - 1] = Checker.EMPTY;
            return true;
        }
        return false;
    }

    //TODO And check for "King Me"
    //TODO Convert this.
    public boolean Step(int column, int row, int targetColumn, int targetRow){
        if(checkStep(column, row, targetColumn, targetRow)){
            Checker movingChecker = board[column - 1][row -1];
            board[column - 1][row - 1] = Checker.EMPTY;
            board[targetColumn - 1][targetRow - 1] = movingChecker;//Pass by value, or reference?
            return true;
        }
        return false;
    }

    //TODO And check for "King Me"
    //TODO Convert this
    public boolean Jump(int column, int row, int targetColumn, int targetRow){
        if(checkJump(column, row, targetColumn, targetRow)){
            Checker movingChecker = board[column - 1][row - 1];
            board[column - 1][row - 1] = Checker.EMPTY;
            board[targetColumn - 1][targetRow - 1] = movingChecker;
            board[(targetColumn - column) / 2 + column - 1][(targetRow - row) / 2 + row - 1] = Checker.EMPTY;
            return true;
        }
        return false;
    }

    private boolean checkDark(int column, int row){
        return ((column + row) % 2 == 0) && row >= 1 && row <= 8 && column >= 1 && column <= 8;
    }

    //TODO Add check for only 4 integers(Maybe elsewhere).
    public boolean checkStep(ArrayList<Integer> move){
        return checkDark(move.get(0), move.get(1))
                && checkDark(move.get(2), move.get(3))
                && board[move.get(0) - 1][move.get(1) - 1].isChecker()
                && board[move.get(0) - 1][move.get(1) - 1].isWhite() == whiteTurn
                && board[move.get(2) - 1][move.get(3) - 1] == Checker.EMPTY
                && Math.abs(move.get(2) - move.get(0)) == 1
                && (movingForward(move.get(1), move.get(3)) || board[move.get(0) - 1][move.get(1) -1].isKing())
                && Math.abs(move.get(2) - move.get(0)) == 1
                && Math.abs(move.get(3) - move.get(1)) == 1;
    }
    //TODO convert this as well.
    public boolean checkJump(int column, int row, int targetColumn, int targetRow){
        return checkDark(move.get(0), move.get(1))
                && checkDark(move.get(2), move.get(3))
                && board[move.get(0) - 1][move.get(1) - 1].isChecker()
                && board[move.get(0) - 1][move.get(1) - 1].isWhite() == whiteTurn
                && board[move.get(2) - 1][move.get(3) - 1] == Checker.EMPTY
                && Math.abs(move.get(2) - column) == 2
                && Math.abs(targetRow - row) == 2
                && (movingForward(row, targetRow) || board[column - 1][row - 1].isKing())
                && board[(targetColumn - column) / 2 + column - 1][(targetRow - row) / 2 + row - 1].isChecker()
                && board[(targetColumn - column) / 2 + column - 1][(targetRow - row) / 2 + row - 1].isWhite() != whiteTurn;
    }

    public boolean movingForward(int row, int targetRow){
        return (targetRow - row > 0 && whiteTurn) || (targetRow - row < 0 && !whiteTurn);
    }

    public void nextTurn(){
        whiteTurn = !whiteTurn;
    }

    public boolean currentPlayerWins(){
        for(int row = 0; row <= 7; row++){
            int preCol = row % 2;
            for(int column = preCol; column <= 7; column += 2){
                if(board[column][row].isChecker() && board[column][row].isWhite() != whiteTurn){
                    return false;
                }
            }
        }
        return true;
    }
}

