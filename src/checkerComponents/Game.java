package checkerComponents;

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

    //TODO And check for "King Me"
    public boolean attemptStep(int column, int row, int targetColumn, int targetRow){
        if(checkStep(column, row, targetColumn, targetRow)){
            Checker movingChecker = board[column - 1][row -1];
            board[column - 1][row - 1] = Checker.EMPTY;
            board[targetColumn - 1][targetRow - 1] = movingChecker;//Pass by value, or reference?
            return true;
        }
        return false;
    }

    public boolean attemptJump(int column, int row, int targetColumn, int targetRow){
        if(checkJump(column, row, targetColumn, targetRow)){
            Checker movingChecker = board[column - 1][row - 1];
            board[column - 1][row - 1] = Checker.EMPTY;
            board[targetColumn - 1][targetRow - 1] = movingChecker;
            board[(targetColumn - column) / 2 + column][(targetRow - row) / 2 + row] = Checker.EMPTY;
            return true;
        }
        return false;
    }


    /**
     * Verifies that a given coordinate is a usable square on a checker board(That it is a dark square)
     * Does not check if the square is occupied, or reachable, etc.
     *
     * I think I'll just use (int, int) internally from now on.
     * //TODO Remove this one.
     */
    private boolean checkDark(char column, int row){
        column = Character.toLowerCase(column);
        int columnNum;
        switch(column){
            case 'a' : columnNum = 1; break;
            case 'b' : columnNum = 2; break;
            case 'c' : columnNum = 3; break;
            case 'd' : columnNum = 4; break;
            case 'e' : columnNum = 5; break;
            case 'f' : columnNum = 6; break;
            case 'g' : columnNum = 7; break;
            case 'h' : columnNum = 8; break;
            default  : columnNum = 0;
        }
        return checkDark(columnNum, row);
    }

    private boolean checkDark(int column, int row){
        return ((column + row) % 2 == 0) && row >= 1 && row <= 8 && column >= 1 && column <= 8;
    }

    //TODO Fix to work for black player (still need to flip the whiteTurn value elsewhere)
    private boolean checkStep(int column, int row, int targetColumn, int targetRow){
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
    //TODO Fix to work for black player (still need to flip the whiteTurn value elsewhere)
    public boolean checkJump(int column, int row, int targetColumn, int targetRow){
        return checkDark(column, row)
                && checkDark(targetColumn, targetRow)
                && board[column - 1][row - 1].isChecker()
                && board[column - 1][row - 1].isWhite() == whiteTurn
                && board[targetColumn - 1][targetRow - 1] == Checker.EMPTY
                && Math.abs(targetColumn - column) == 2
                && (targetRow - row == 2 ||
                board[column - 1][row - 1].isKing() && Math.abs(targetRow - row) == 2)
                && board[(targetColumn - column) / 2 + column][(targetRow - row) / 2 + row].isChecker()
                && board[(targetColumn - column) / 2 + column][(targetRow - row) / 2 + row].isWhite() != whiteTurn;
    }

    public boolean movingForward(int row, int targetRow){
        return (targetRow - row > 0 && whiteTurn) || (targetRow - row < 0 && !whiteTurn);
    }

    public void nextTurn(){
        whiteTurn = !whiteTurn;
    }

}

