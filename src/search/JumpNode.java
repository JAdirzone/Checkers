package search;

import checkerComponents.Game;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Jay on 4/15/2017.
 */
public class JumpNode {
    private int column;
    private int row;
    private JumpNode parent;
    private LinkedList<JumpNode> children;

    public JumpNode(Game game, int column, int row, JumpNode parent){
        this.column = column;
        this.row = row;
        this.parent = parent;
        this.children = new LinkedList<>();
        generateChildren(game);
    }

    private void generateChildren(Game game){
        if(game.checkSubJump(column, row, column + 2, row + 2)){
            children.add(new JumpNode(game, column + 2, row + 2, this));
        }
        if(game.checkSubJump(column, row, column + 2, row - 2)){
            children.add(new JumpNode(game, column + 2, row - 2, this));
        }
        if(game.checkSubJump(column, row, column - 2, row + 2)){
            children.add(new JumpNode(game, column - 2, row + 2, this));
        }
        if(game.checkSubJump(column, row, column - 2, row - 2)){
            children.add(new JumpNode(game, column - 2, row - 2, this));
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






}
