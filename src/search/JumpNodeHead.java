package search;

import checkerComponents.Game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Created by Jay on 4/16/2017.
 */
public class JumpNodeHead extends JumpNode{

    private int originColumn;
    private int originRow;

    public JumpNodeHead(Game game, int column, int row, int targetColumn, int targetRow){
        this.column = targetColumn;
        this.row = targetRow;
        this.originColumn = column;
        this.originRow = row;
        this.children = new LinkedList<>();
        //TODO check if the checker that is about to move is a king
        jumpedChecker = game.subJump(column, row, targetColumn, targetRow);
        generateChildren(game);
        game.undoSubJump(targetColumn, targetRow, originColumn, originRow, jumpedChecker); //TODO fis this line
    }

    public ArrayList<Integer> nextFullNode(){
        ArrayList<Integer> result = new ArrayList<>();
        result.add(originColumn);
        result.add(originRow);
        result.add(column);
        result.add(row);

        if(!children.isEmpty()){
            result.addAll(children.get(0).nextFullNode());
            if(children.get(0).children.isEmpty()){
                children.remove(0);
            }
            return result;
        }
        return result;
    }


    //public void backTracked(Game game){
        //game.undoSubJump(column, row, originColumn, originRow, jumpedChecker);
    //}
}
