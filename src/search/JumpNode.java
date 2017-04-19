package search;

import checkerComponents.Checker;
import checkerComponents.Game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * A node on a tree that finds all of the possible jumps that a single piece can make.
 * This is necessary because double jumps count as one move, and is used by Node to create its children.
 * However, Node and JumpNode (paired with jumpNodeHead) are separate from one another.
 * One is never the parent or child of the other. They form two separate tree structures.
 */
public class JumpNode {
    protected int column;
    protected int row;
    private JumpNode parent;
    protected LinkedList<JumpNode> children;
    protected Checker jumpedChecker;
    //protected boolean movedKing;

    public JumpNode(Game game, int column, int row, JumpNode parent){
        this.column = column;
        this.row = row;
        this.parent = parent;
        this.children = new LinkedList<>();
        //TODO check if the checker that is about to move is a king
        boolean movedKing = game.isKing(parent.column, parent.row);
        jumpedChecker = game.subJump(parent.column, parent.row, column, row);
        if(movedKing || !game.isKing(column, row)){
            generateChildren(game);
        }
        game.undoSubJump(column, row, parent.column, parent.row, jumpedChecker, !movedKing && game.isKing(column, row)); //add moved king here, remember to rework JumpNodeHead as well

    }

    public JumpNode(){
        //To satisfy Java's demand for a default constructor
    }

    /**
     * Creates children for this node.
     * @param game
     */
    protected void generateChildren(Game game){
        if(game.checkSubJump(column, row, column + 2, row + 2, true)){
            branch(game, column + 2, row + 2);
        }
        if(game.checkSubJump(column, row, column + 2, row - 2, true)){
            branch(game, column + 2, row - 2);
        }
        if(game.checkSubJump(column, row, column - 2, row + 2, true)){
            branch(game, column - 2, row + 2);
        }
        if(game.checkSubJump(column, row, column - 2, row - 2, true)){
            branch(game, column - 2, row - 2);
        }
    }

    /**
     * @return An ArrayList of Integers representing a move. Because of the possibility to double jump (or multi jump),
     * this list will be atleast four integers long, but could be longer.
     */
    public ArrayList<Integer> nextFullNode(){
        ArrayList<Integer> result = new ArrayList<>();
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

    public boolean hasChildren(){
        return !children.isEmpty();
    }

    /**
     * Creates a child for this node. The child will represent a single hop (not necessarily a full move because of
     * double jumping) from tis node's column and row values to the position specified by the column and row values
     * passed into this.
     * @param game
     * @param column column jumped to by the child node.
     * @param row row jumped to by the child node.
     */
    protected void branch(Game game, int column, int row){
        children.add(new JumpNode(game, column, row, this));
    }

    //private void backTracked(Game game){
        //game.undoSubJump(column, row, parent.column, parent.row, jumpedChecker);
    //}

}
