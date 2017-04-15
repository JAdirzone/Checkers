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

    //Input must be validated before calling this.
    public void move(ArrayList<Integer> move){
        if(Math.abs(move.get(0) - move.get(1)) == 1){
            step(move.get(0), move.get(1), move.get(2), move.get(3));
        } else{
            jump(move);
        }
    }

    //TODO Add check for "King Me"
    //Input must be validated before calling this.
    public void step(int column, int row, int targetColumn, int targetRow){
        Checker movingChecker = board[column - 1][row -1];
        board[column - 1][row - 1] = Checker.EMPTY;
        board[targetColumn - 1][targetRow - 1] = movingChecker;//Pass by value, or reference?
    }

    //TODO Add check for "King Me" here?
    //Input must be validated before calling this.
    private void subJump(int column, int row, int targetColumn, int targetRow){
        Checker movingChecker = board[column - 1][row - 1];
        board[column - 1][row - 1] = Checker.EMPTY;
        board[targetColumn - 1][targetRow - 1] = movingChecker;
        board[(targetColumn - column) / 2 + column - 1][(targetRow - row) / 2 + row - 1] = Checker.EMPTY;
    }

    //TODO Add check for "King me" here?
    //Input must be validated before calling this.
    public void jump(ArrayList<Integer> jump){
        for(int i = 4; i <= jump.size(); i += 2){
            subJump(jump.get(i - 4), jump.get(i - 3), jump.get(i - 2), jump.get(i - 1));
        }
    }

    private boolean checkDark(int column, int row){
        return ((column + row) % 2 == 0) && row >= 1 && row <= 8 && column >= 1 && column <= 8;
    }


    //TODO add check to allow for non-king to backwards double jump

    public boolean checkSubStep(int column, int row, int targetColumn, int targetRow){
        return checkDark(column, row)
                && checkDark(targetColumn, targetRow)
                && board[column - 1][row - 1].isChecker()
                && board[column - 1][row - 1].isWhite() == whiteTurn
                && board[targetColumn - 1][targetRow - 1] == Checker.EMPTY
                && Math.abs(targetColumn - column) == 1
                && (movingForward(row, targetRow) || board[column - 1][row -1].isKing())
                && Math.abs(targetColumn - column) == 1
                && Math.abs(targetRow - row) == 1;
    }

    public boolean checkSubJump(int column, int row, int targetColumn, int targetRow){
        return checkDark(column, row)
                && checkDark(targetColumn, targetRow)
                && board[column - 1][row - 1].isChecker()
                && board[column - 1][row - 1].isWhite() == whiteTurn
                && board[targetColumn - 1][targetRow - 1] == Checker.EMPTY
                && Math.abs(targetColumn - column) == 2
                && Math.abs(targetRow - row) == 2
                && (movingForward(row, targetRow) || board[column - 1][row - 1].isKing())
                && board[(targetColumn - column) / 2 + column - 1][(targetRow - row) / 2 + row - 1].isChecker()
                && board[(targetColumn - column) / 2 + column - 1][(targetRow - row) / 2 + row - 1].isWhite() != whiteTurn;
    }

    public boolean checkStep(ArrayList<Integer> step){
        return step.size() == 4 && checkSubStep(step.get(0), step.get(1), step.get(2), step.get(3));
    }

    public boolean checkJump(ArrayList<Integer> jump){
        for(int i = 4; i <= jump.size(); i += 2){
            if(!checkSubJump(jump.get(i - 4), jump.get(i - 3), jump.get(i - 2), jump.get(i - 1))){
                return false;
            }
        }
        return !checkSubJump(jump.get(jump.size() - 2), jump.get(jump.size() - 1),
                    jump.get(jump.size() - 2) + 2, jump.get(jump.size() - 1) + 2)
                && !checkSubJump(jump.get(jump.size() - 2), jump.get(jump.size() - 1),
                    jump.get(jump.size() - 2) + 2, jump.get(jump.size() - 1) - 2)
                && !checkSubJump(jump.get(jump.size() - 2), jump.get(jump.size() - 1),
                    jump.get(jump.size() - 2) - 2, jump.get(jump.size() - 1) + 2)
                && !checkSubJump(jump.get(jump.size() - 2), jump.get(jump.size() - 1),
                    jump.get(jump.size() - 2) - 2, jump.get(jump.size() - 1) - 2);
    }


    public boolean movingForward(int row, int targetRow){
        return (targetRow - row > 0 && whiteTurn) || (targetRow - row < 0 && !whiteTurn);
    }

    public void nextTurn(){
        whiteTurn = !whiteTurn;
    }

    public boolean isWhiteTurn(){
        return whiteTurn;
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

    public boolean forcedJump(){
        for(int row = 0; row <= 7; row++){
            int preCol = row % 2;
            for(int column = preCol; column <= 7; column += 2){
                if(board[column][row].isChecker()
                        && board[column ][row].isWhite() == isWhiteTurn()
                        && availableJump(column + 1, row + 1)) {
                    return true;
                }
            }
        }
        return false;
    }

    //TODO use in checkJump.
    public boolean availableJump(int column, int row){
        return checkSubJump(column, row, column + 2, row + 2)
                || checkSubJump(column, row,column + 2, row - 2)
                || checkSubJump(column, row,column - 2, row + 2)
                || checkSubJump(column, row,column - 2, row - 2);
    }

    public boolean checkerCheck(int column, int row, boolean isWhite){
        return board[column - 1][row - 1].isChecker() && board[column - 1][row - 1].isWhite() == isWhite;
    }
}

