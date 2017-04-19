package checkerComponents;

import java.util.ArrayList;
import java.util.Random;

import static checkerComponents.Checker.EMPTY;

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
                     board[column - 1][row - 1] = EMPTY;
                 }
             }
         }
         whiteTurn = true;
    }

    /**
     *
     * @return the board as a string.
     */
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
        result += "------------------------------\n";
        return result;
    }

    //Input must be validated before calling this.

    /**
     * Performs the move represented by the move ArrayList.
     * @param move
     * @return The checkers that were jumped
     */
    public ArrayList<Checker> move(ArrayList<Integer> move){
        if(Math.abs(move.get(0) - move.get(2)) == 1){
            return step(move.get(0), move.get(1), move.get(2), move.get(3));
        } else{
            return jump(move);
        }
    }

    //Input must be validated before calling this.
    /**
     *  Performs a step from (column, row) to (targetColumn, targetRow)
     * @param column
     * @param row
     * @param targetColumn
     * @param targetRow
     * @return
     */
    public ArrayList<Checker> step(int column, int row, int targetColumn, int targetRow){
        Checker movingChecker = board[column - 1][row -1];
        board[column - 1][row - 1] = EMPTY;
        board[targetColumn - 1][targetRow - 1] = movingChecker;//Pass by value, or reference?
        whiteTurn = !whiteTurn;
        if(distanceFromBackRow(targetRow - 1, board[targetColumn - 1][targetRow - 1].isWhite()) == 7){
            board[targetColumn - 1][targetRow - 1] = board[targetColumn -1 ][targetRow - 1].kingMe();
        }
        return new ArrayList<>();
    }

    //Input must be validated before calling this.

    /**
     * Performs a single hop that may be part of a longer move.
     * @param column
     * @param row
     * @param targetColumn
     * @param targetRow
     * @return The checker that was jumped over.
     */
    public Checker subJump(int column, int row, int targetColumn, int targetRow){
        Checker movingChecker = board[column - 1][row - 1];
        board[column - 1][row - 1] = EMPTY;
        board[targetColumn - 1][targetRow - 1] = movingChecker;
        Checker result = board[(targetColumn - column) / 2 + column - 1][(targetRow - row) / 2 + row - 1];
        board[(targetColumn - column) / 2 + column - 1][(targetRow - row) / 2 + row - 1] = EMPTY;
        if(distanceFromBackRow(targetRow - 1, board[targetColumn - 1][targetRow - 1].isWhite()) == 7)//More efficient to check against the turn than the checker in the position's color
        {
            board[targetColumn - 1][targetRow - 1] = board[targetColumn - 1][targetRow - 1].kingMe();
        }
        return result;
    }

    //Input must be validated before calling this.

    /**
     * Performs a full jump move
     * @param jump
     * @return An ArrayList of the checkers jumped over.
     */
    public ArrayList<Checker> jump(ArrayList<Integer> jump){
        ArrayList<Checker> jumpedCheckers = new ArrayList<>();
        for(int i = 4; i <= jump.size(); i += 2){
            jumpedCheckers.add(subJump(jump.get(i - 4), jump.get(i - 3), jump.get(i - 2), jump.get(i - 1)));
        }
        whiteTurn = !whiteTurn;
        return jumpedCheckers;

    }

    /**
     * @param column
     * @param row
     * @return whether the square specified by (column, row) is an dark square (usable). False if it is not a square
     * on the board at all.
     */
    private boolean checkDark(int column, int row){
        return ((column + row) % 2 == 0) && row >= 1 && row <= 8 && column >= 1 && column <= 8;
    }


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

    /**
     * Checks a hop to see if it is allowed
     * @param column
     * @param row
     * @param targetColumn
     * @param targetRow
     * @param doubleJump
     * @return
     */
    public boolean checkSubJump(int column, int row, int targetColumn, int targetRow, boolean doubleJump){
        return checkDark(column, row)
                && checkDark(targetColumn, targetRow)
                && board[column - 1][row - 1].isChecker()
                && board[column - 1][row - 1].isWhite() == whiteTurn
                && board[targetColumn - 1][targetRow - 1] == EMPTY
                && Math.abs(targetColumn - column) == 2
                && Math.abs(targetRow - row) == 2
                && (movingForward(row, targetRow) || board[column - 1][row - 1].isKing() || doubleJump)
                && board[(targetColumn - column) / 2 + column - 1][(targetRow - row) / 2 + row - 1].isChecker()
                && board[(targetColumn - column) / 2 + column - 1][(targetRow - row) / 2 + row - 1].isWhite() != whiteTurn;
    }

    /**
     * Checks a step to see if it is allowed
     * @param step
     * @return
     */
    public boolean checkStep(ArrayList<Integer> step){
        return step.size() == 4 && checkSubStep(step.get(0), step.get(1), step.get(2), step.get(3));
    }

    /**
     * checks an entire jump move to see if it is allowed
     * @param jump
     * @return
     */
    public boolean checkJump(ArrayList<Integer> jump){
        boolean crownedKing = false;
        boolean doubleJump = false;
        for(int i = 4; i <= jump.size(); i += 2){
            if(!checkSubJump(jump.get(i - 4), jump.get(i - 3), jump.get(i - 2), jump.get(i - 1), doubleJump)
                    || crownedKing){
                return false;
            }if(distanceFromBackRow(jump.get(i - 1) - 1, board[jump.get(0) - 1][jump.get(1) - 1].isWhite()) == 7){ //is this correct
                crownedKing = true;
            }
            doubleJump = true;
        }
        return !checkSubJump(jump.get(jump.size() - 2), jump.get(jump.size() - 1),
                    jump.get(jump.size() - 2) + 2, jump.get(jump.size() - 1) + 2, true)
                && !checkSubJump(jump.get(jump.size() - 2), jump.get(jump.size() - 1),
                    jump.get(jump.size() - 2) + 2, jump.get(jump.size() - 1) - 2, true)
                && !checkSubJump(jump.get(jump.size() - 2), jump.get(jump.size() - 1),
                    jump.get(jump.size() - 2) - 2, jump.get(jump.size() - 1) + 2, true)
                &&
                 !checkSubJump(jump.get(jump.size() - 2), jump.get(jump.size() - 1),
                    jump.get(jump.size() - 2) - 2, jump.get(jump.size() - 1) - 2, true);
    }

    /**
     * @param row
     * @param targetRow
     * @return whether a checker attempting to move from row to targetRow would be moving
     * forward, or the direction that at non-king piece must move.
     */
    public boolean movingForward(int row, int targetRow){
        return (targetRow - row > 0 && whiteTurn) || (targetRow - row < 0 && !whiteTurn);
    }

    public void nextTurn(){
        whiteTurn = !whiteTurn;
    }

    public boolean isWhiteTurn(){
        return whiteTurn;
    }

    //Don't use
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

    /**
     *
     * @param isWhite
     * @return whether the given player has a piece left which can move.
     */
    public boolean playerCanMove(boolean isWhite){
        for(int row = 0; row <= 7; row++){
            int preCol = row % 2;
            for(int column = preCol; column <= 7; column += 2){
                if(board[column][row].isChecker() && board[column][row].isWhite() == isWhite){
                    if(availableMove(column + 1, row + 1)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * @return whether the current player has a jump available, and is therefore forced to jump.
     */
    public boolean forcedJump(){
        for(int row = 0; row <= 7; row++){
            int preCol = row % 2;
            for(int column = preCol; column <= 7; column += 2){
                if(board[column][row].isChecker()
                        && board[column][row].isWhite() == isWhiteTurn()
                        && availableJump(column + 1, row + 1)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param column
     * @param row
     * @return whether a piece at (column, row) has an available jump move.
     */
    public boolean availableJump(int column, int row){
        return checkSubJumpTurnless(column, row, column + 2, row + 2)
                || checkSubJumpTurnless(column, row,column + 2, row - 2)
                || checkSubJumpTurnless(column, row,column - 2, row + 2)
                || checkSubJumpTurnless(column, row,column - 2, row - 2);
    }
    /**
     * @param column
     * @param row
     * @return whether a piece at (column, row) has an available step move.
     */
    public boolean availableStep(int column, int row){
        return checkSubStepTurnless(column, row, column + 1, row + 1)
                || checkSubStepTurnless(column, row, column + 1, row - 1)
                || checkSubStepTurnless(column, row, column - 1, row + 1)
                || checkSubStepTurnless(column, row, column - 1, row - 1);

    }

    /**
     * @param column
     * @param row
     * @return whether a piece at (column, row) has an available move.
     */
    public boolean availableMove(int column, int row){
        return availableJump(column, row) || availableStep(column, row);
    }


    public boolean checkerCheck(int column, int row, boolean isWhite){
        return board[column - 1][row - 1].isChecker() && board[column - 1][row - 1].isWhite() == isWhite;
    }

    //Must validate input before calling
    //target values represent the place being moved back to

    /**
     * Undoes a single hop
     * @param column
     * @param row
     * @param targetColumn
     * @param targetRow
     * @param jumpedChecker
     * @param becameKing
     */
    public void undoSubJump(int column, int row, int targetColumn, int targetRow, Checker jumpedChecker, boolean becameKing){
        if(becameKing){
            board[targetColumn - 1][targetRow - 1] = board[column - 1][row - 1].revertKing();
        }else {
            board[targetColumn - 1][targetRow - 1] = board[column - 1][row - 1];
        }
        board[column - 1][row - 1] = EMPTY;
        board[(targetColumn - column) / 2 + column - 1][(targetRow - row) / 2 + row - 1] = jumpedChecker;
    }

    /**
     * Undoes a single step
     * @param column
     * @param row
     * @param targetColumn
     * @param targetRow
     * @param becameKing
     */
    public void undoSubStep(int column, int row, int targetColumn, int targetRow, boolean becameKing){
        if(becameKing){
            board[targetColumn - 1][targetRow - 1] = board[column - 1][row - 1].revertKing();
        }
        else{
            board[targetColumn - 1][targetRow - 1] = board[column - 1][row - 1];
        }
        board[column - 1][row - 1] = EMPTY;
    }

    /**
     * Undoes a single step.
     * @param move
     * @param becameKing
     */
    public void undoStep(ArrayList<Integer> move, boolean becameKing){
        undoSubStep(move.get(2), move.get(3), move.get(0), move.get(1), becameKing);
        whiteTurn = !whiteTurn; //this may be the problem
    }

    /**
     * Undoes an entire jump move
     * @param move
     * @param jumpedCheckers
     * @param becameKing
     */
    public void undoJump(ArrayList<Integer> move, ArrayList<Checker> jumpedCheckers, boolean becameKing){
        int counter = 1;
        for(int i = move.size() - 4; i >= 0; i = i - 2){
            undoSubJump(move.get(i + 2), move.get(i + 3), move.get(i), move.get(i + 1),
                    jumpedCheckers.get(jumpedCheckers.size() - counter), becameKing); //Technically this "reverts king" every time but the outcome is the same.
            counter++;
        }
        whiteTurn = !whiteTurn;

    }

    public void setWhiteTurn(boolean isWhiteTurn){
        this.whiteTurn = isWhiteTurn ;
    }

    public boolean isKing(int column, int row){
        return board[column - 1][row - 1].isKing();
    }

    //like checkSubStep but it does no care whose turn it is.
    public boolean checkSubStepTurnless(int column, int row, int targetColumn, int targetRow){
        return checkDark(column, row)
                && checkDark(targetColumn, targetRow)
                && board[column - 1][row - 1].isChecker()
                //&& board[column - 1][row - 1].isWhite() == whiteTurn
                && board[targetColumn - 1][targetRow - 1] == Checker.EMPTY
                && (Math.abs(targetColumn - column) == 1)
                && (movingForward(row, targetRow) || board[column - 1][row - 1].isKing())
                && (Math.abs(targetColumn - column) == 1)
                && (Math.abs(targetRow - row) == 1);
    }

    //like checkSubJump but it does no care whose turn it is.
    public boolean checkSubJumpTurnless(int column, int row, int targetColumn, int targetRow){
        return checkDark(column, row)
                && checkDark(targetColumn, targetRow)
                && board[column - 1][row - 1].isChecker()
                //&& board[column - 1][row - 1].isWhite() == whiteTurn
                && board[targetColumn - 1][targetRow - 1] == EMPTY
                && Math.abs(targetColumn - column) == 2
                && Math.abs(targetRow - row) == 2
                && (movingForward(row, targetRow) || board[column - 1][row - 1].isKing())
                && board[(targetColumn - column) / 2 + column - 1][(targetRow - row) / 2 + row - 1].isChecker()
                && board[(targetColumn - column) / 2 + column - 1][(targetRow - row) / 2 + row - 1].isWhite() != board[column - 1][row - 1].isWhite();
    }


    //Heuristic Below
    //Note check for victory is done else where and the value is set to (-)1000000 accordingly.
    public int heuristic(){
        int result = 0;
        for(int row = 0; row <= 7; row++){
            int preCol = row % 2;
            for(int column = preCol; column <= 7; column += 2){
                if(board[column][row].isChecker()){
                    result += evaluateChecker(column, row, board[column][row]);
                }
            }
        }
        return result;
    }
    //range 0-7, not 1-8
    public int evaluateChecker(int column, int row, Checker checker){
        int result = 0;
        if(column == 0 && row % 2 == 0){
            result += 1;
        }
        if(column == 7 && row % 2 == 1){
            result += 1;
        }
        if(!checker.isKing()) {
            result += 5 * (distanceFromBackRow(row, checker.isWhite()) + 1);
        }
        else{
            result += 95;
        }
        if(checker.isWhite()){
            result = result * -1;
        }
        return result;
    }

    private int distanceFromBackRow(int row, boolean isWhite){
        if(isWhite){
            return row;
        }else{
            return 7 - row;
        }
    }
}

