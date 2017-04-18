package checkerComponents;

import java.util.ArrayList;

import static checkerComponents.Checker.EMPTY;

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
                     board[column - 1][row - 1] = EMPTY;
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
        result += "------------------------------\n";
        return result;
    }

    //Input must be validated before calling this.
    public ArrayList<Checker> move(ArrayList<Integer> move){
        if(Math.abs(move.get(0) - move.get(2)) == 1){
            return step(move.get(0), move.get(1), move.get(2), move.get(3));
        } else{
            return jump(move);
        }
    }

    //TODO Add check for "King Me"
    //Input must be validated before calling this.
    public ArrayList<Checker> step(int column, int row, int targetColumn, int targetRow){
        Checker movingChecker = board[column - 1][row -1];
        board[column - 1][row - 1] = EMPTY;
        //System.out.println("&&&&&& "+ column + "," + row + "," + targetColumn + "," + targetRow);
        board[targetColumn - 1][targetRow - 1] = movingChecker;//Pass by value, or reference?
        whiteTurn = !whiteTurn;
        System.out.println("step " + column + "," + row + " to " + targetColumn + "," + targetRow);
        //System.out.println("++++++++\n" + toString());
        return new ArrayList<Checker>();

    }

    //TODO Add check for "King Me" here?
    //Input must be validated before calling this.
    public Checker subJump(int column, int row, int targetColumn, int targetRow){
        Checker movingChecker = board[column - 1][row - 1];
        board[column - 1][row - 1] = EMPTY;
        board[targetColumn - 1][targetRow - 1] = movingChecker;
        Checker result = board[(targetColumn - column) / 2 + column - 1][(targetRow - row) / 2 + row - 1];
        board[(targetColumn - column) / 2 + column - 1][(targetRow - row) / 2 + row - 1] = EMPTY;
        //System.out.println("subjump " + column + "," + row + " to " + targetColumn + "," + targetRow);
        //System.out.println("++++++++\n" + toString());
        return result;
    }

    //TODO Add check for "King me" here?
    //Input must be validated before calling this.
    public ArrayList<Checker> jump(ArrayList<Integer> jump){
        ArrayList<Checker> jumpedCheckers = new ArrayList<>();
        for(int i = 4; i <= jump.size(); i += 2){
            jumpedCheckers.add(subJump(jump.get(i - 4), jump.get(i - 3), jump.get(i - 2), jump.get(i - 1)));
        }
        whiteTurn = !whiteTurn;
        System.out.println("jumped " + jump.toString());
        return jumpedCheckers;

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
                && board[targetColumn - 1][targetRow - 1] == EMPTY
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

    //TODO replace
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

    public boolean playerCanMove(boolean isWhite){
        for(int row = 0; row <= 7; row++){
            int preCol = row % 2;
            for(int column = preCol; column <= 7; column += 2){
                if(board[column][row].isChecker() && board[column][row].isWhite() == isWhite){
                    if(availableMove(column, row)){
                        return true;
                    }
                }
            }
        }
        return false;
    }


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

    //TODO use in checkJump.
    public boolean availableJump(int column, int row){
        return checkSubJump(column, row, column + 2, row + 2)
                || checkSubJump(column, row,column + 2, row - 2)
                || checkSubJump(column, row,column - 2, row + 2)
                || checkSubJump(column, row,column - 2, row - 2);
    }

    public boolean availableStep(int column, int row){
        return checkSubJump(column, row, column + 2, row + 2)
                || checkSubJump(column, row, column + 2, row - 2)
                || checkSubJump(column, row, column - 2, row + 2)
                || checkSubJump(column, row, column - 2, row - 2);

    }

    public boolean availableMove(int column, int row){
        return availableJump(column, row) || availableStep(column, row);
    }


    public boolean checkerCheck(int column, int row, boolean isWhite){
        return board[column - 1][row - 1].isChecker() && board[column - 1][row - 1].isWhite() == isWhite;
    }

    //Must validate input before calling
    //target values represent the place being moved back to
    //TODO need to be able to undo becoming a king.
    public void undoSubJump(int column, int row, int targetColumn, int targetRow, Checker jumpedChecker){
        board[targetColumn - 1][targetRow - 1] = board[column - 1][row - 1];
        board[column - 1][row - 1] = EMPTY;
        board[(targetColumn - column) / 2 + column - 1][(targetRow - row) / 2 + row - 1] = jumpedChecker;
        //System.out.println("undo subjump " + column + "," + row + " to " + targetColumn + "," + targetRow);
        //System.out.println("*******\n" + toString());
    }
    //TODO need to be able to undo becoming a king
    public void undoSubStep(int column, int row, int targetColumn, int targetRow){
        board[targetColumn - 1][targetRow - 1] = board[column - 1][row - 1];
        board[column - 1][row - 1] = EMPTY;
        //System.out.println("undo step " + column + "," + row + " to " + targetColumn + "," + targetRow);
        //System.out.println("*******\n" + toString());
    }

    public void undoStep(ArrayList<Integer> move){
        undoSubStep(move.get(2), move.get(3), move.get(0), move.get(1));
        whiteTurn = !whiteTurn;
    }

    public void undoJump(ArrayList<Integer> move, ArrayList<Checker> jumpedCheckers){
        int counter = 1;
        for(int i = move.size() - 4; i >= 0; i = i - 2){ //TODO, should i be >= 3, or some other value
            undoSubJump(move.get(i + 2), move.get(i + 3), move.get(i), move.get(i + 1),
                    jumpedCheckers.get(jumpedCheckers.size() - counter));
            counter++;
        }
        whiteTurn = !whiteTurn;

    }

    //For some reason, the AI isn't putting it back to the other player's turn when it's done. quick fix.
    public void setWhiteTurn(boolean isWhiteTurn){
        this.whiteTurn = isWhiteTurn ;
    }




    //Heuristic Below
    public int heuristic(){
        int result = 0;
        for(int row = 0; row <= 7; row++){
            int preCol = row % 2;
            for(int column = preCol; column <= 7; column += 2){
                if(board[column][row].isChecker()){
                    result += evaluateChecker(column + 1, row + 1, board[column][row]);
                }
            }
        }
        return result;
    }
    //range 0-7, not 1-8
    public int evaluateChecker(int column, int row, Checker checker){
        int result = 0;
        if(column == 0 && row % 2 == 0){
            result += 100;
        }
        if(column == 7 && row % 2 == 1){
            result += 100;
        }
        result += 10 * distanceFromBackRow(row, checker.isWhite());
        if(checker.isKing()){
            result = result * 3; //Maybe kings should be scored in a way that does not favor moving further from its start.
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

