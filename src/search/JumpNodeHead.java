package search;

import checkerComponents.Game;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Jay on 4/16/2017.
 */
public class JumpNodeHead extends JumpNode{

    private int originColumn;
    private int originRow;

    public JumpNodeHead(Game game, int column, int row, int targetColumn, int targetRow){
        super(game, targetColumn, targetRow, null);
        this.originColumn = column;
        this.originRow = row;
        //TODO check if the checker that is about to move is a king
        jumpedChecker = game.subJump(column, row, targetColumn, targetRow);
        generateChildren(game);
    }

    public void backTracked(Game game){
        game.undoSubJump(column, row, originColumn, originRow, jumpedChecker);
    }
}
