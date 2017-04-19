package search;

import checkerComponents.Game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * The head of a tree of jump nodes.
 */
public class JumpNodeHead extends JumpNode{

    //Because this node must represent a full jump itself (unlike regular JumpNodes, which rely on their parent to
    //provide their origin coordinates), these variables are needed.
    private int originColumn;
    private int originRow;

    public JumpNodeHead(Game game, int column, int row, int targetColumn, int targetRow){
        this.column = targetColumn;
        this.row = targetRow;
        this.originColumn = column;
        this.originRow = row;
        this.children = new LinkedList<>();
        boolean movedKing = game.isKing(column, row);
        jumpedChecker = game.subJump(column, row, targetColumn, targetRow);
        if(movedKing || !game.isKing(targetColumn, targetRow)) {
            generateChildren(game);
        }
        game.undoSubJump(targetColumn, targetRow, originColumn, originRow, jumpedChecker, !movedKing && game.isKing(targetColumn, targetRow)); //TODO just changed after dennis encountered bug
    }

    /**
     * @return An ArrayList of Integers representing a full move. Because of the possibility to double jump (or multi jump),
     * this list will be at least four integers long, but could be longer.
     */
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

    /**
     * Creates children for this node.
     * @param game
     */
    protected void generateChildren(Game game){
        if(game.checkSubJump(column, row, column + 2, row + 2, false)){
            branch(game, column + 2, row + 2);
        }
        if(game.checkSubJump(column, row, column + 2, row - 2, false)){
            branch(game, column + 2, row - 2);
        }
        if(game.checkSubJump(column, row, column - 2, row + 2, false)){
            branch(game, column - 2, row + 2);
        }
        if(game.checkSubJump(column, row, column - 2, row - 2, false)){
            branch(game, column - 2, row - 2);
        }
    }
}
