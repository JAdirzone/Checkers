package search;

import checkerComponents.Checker;
import checkerComponents.Game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;


public class JumpNode {
    protected int column;
    protected int row;
    private JumpNode parent;
    private LinkedList<JumpNode> children;
    protected Checker jumpedChecker;

    public JumpNode(Game game, int column, int row, JumpNode parent){
        this.column = column;
        this.row = row;
        this.parent = parent;
        this.children = new LinkedList<>();
        //TODO check if the checker that is about to move is a king
        jumpedChecker = game.subJump(parent.column, parent.row, column, row);
        generateChildren(game);
    }
    //game must be a COPY of the one used by the main Nodes?
    protected void generateChildren(Game game){
        if(game.checkSubJump(column, row, column + 2, row + 2)){
            branch(game, column + 2, row + 2);
        }
        if(game.checkSubJump(column, row, column + 2, row - 2)){
            branch(game, column + 2, row - 2);
        }
        if(game.checkSubJump(column, row, column - 2, row + 2)){
            branch(game, column - 2, row + 2);
        }
        if(game.checkSubJump(column, row, column - 2, row - 2)){
            branch(game, column - 2, row - 2);
        }
    }


    public ArrayList<Integer> nextFullNode(){
        if(!children.isEmpty()){
            ArrayList<Integer> result = children.get(0).nextFullNode();
            result.add(column, row);
            if(children.get(0).children.isEmpty()){
                children.remove(0);
            }
            return result;
        }
        ArrayList<Integer> result = new ArrayList<>();
        result.add(column, row);
        return result;
    }

    public boolean hasChildren(){
        return !children.isEmpty();
    }

    private void branch(Game game, int column, int row){
        children.add(new JumpNode(game, column, row, this));
        backTracked(game);
    }

    private void backTracked(Game game){
        game.undoSubJump(column, row, parent.column, parent.row, jumpedChecker);
    }

}
