package search;

import checkerComponents.Game;

import java.util.ArrayList;

/**
 * Created by Jay on 4/15/2017.
 */
public class Node {
    //It may be better not to store many of these as instance variables; Just take them as arguments.
    private static int maxDepth = 5;
    private Node parent;
    private ArrayList<Node> children;
    private ArrayList<Integer> move; //The move that this node represents
    private ArrayList<Integer> bestMove;
    private int bestMoveValue;
    private int value;
    private boolean max; //Whether this node is trying to maximize of minimize. Max is the black player's move

    //board should be a copy of the board actually being used by the game.
    public Node(Game game, Node par, ArrayList<Integer> causingMove, boolean maxMin){
        children  = new ArrayList<>();
        bestMove  = new ArrayList<>();
        max = maxMin;
        parent = par;
        move = causingMove;
        //TODO Check for pruning here, for if children are possible. This means the hueristic will have to run here too.
        game.forcedJump();                                              //No it does not
        if(game.forcedJump()){
            generateStepChildren(game);
        }
        else{
            generateJumpChildren(game);
        }
    }

    private boolean isBetterValue(int newValue){
        return (max && newValue > bestMoveValue) || (!max && newValue < bestMoveValue);
    }

    //Handles when this node is backtracked to.
    private void backTracked(){
        if(isBetterValue(children.get(children.size() - 1).value)){
            bestMove = children.get(children.size() - 1).bestMove;
        }
    }

    private void branch(Game game, ArrayList<Integer> nextMove){
        children.add(new Node(game, this, nextMove, !max));
        backTracked();
    }

    private void generateStepChildren(Game game){
        for(int row = 0; row <= 7; row++){
            int preCol = row % 2;
            for(int column = preCol; column <= 7; column += 2){
                if(game.checkerCheck(column + 1, row + 1, !max)) {
                    if(game.checkSubStep(column, row, column + 1, row + 1)){
                        ArrayList<Integer> nextMove = new ArrayList<>();
                        nextMove.add(column);
                        nextMove.add(row);
                        nextMove.add(column + 1);
                        nextMove.add(row + 1);
                        branch(game, nextMove);
                    }
                    if(game.checkSubStep(column, row, column + 1, row - 1)){
                        ArrayList<Integer> nextMove = new ArrayList<>();
                        nextMove.add(column);
                        nextMove.add(row);
                        nextMove.add(column + 1);
                        nextMove.add(row - 1);
                        children.add(new Node(game, this, nextMove, !max));
                    }
                    if(game.checkSubStep(column, row, column - 1, row + 1)){
                        ArrayList<Integer> nextMove = new ArrayList<>();
                        nextMove.add(column);
                        nextMove.add(row);
                        nextMove.add(column - 1);
                        nextMove.add(row + 1);
                        children.add(new Node(game, this, nextMove, !max));
                    }
                    if(game.checkSubStep(column, row, column - 1, row - 1)){
                        ArrayList<Integer> nextMove = new ArrayList<>();
                        nextMove.add(column);
                        nextMove.add(row);
                        nextMove.add(column - 1);
                        nextMove.add(row - 1);
                        children.add(new Node(game, this, nextMove, !max));
                    }
                }
            }
        }
    }

    public void generateJumpChildren(Game game){
        for(int row = 0; row <= 7; row++){
            int preCol = row % 2;
            for(int column = preCol; column <= 7; column += 2) {
                if (game.checkerCheck(column + 1, row + 1, !max)) {
                    JumpNode head = new JumpNode(game, column + 1, row + 1, null); //Potential Problem wiih Null?
                    while (head.hasChildren()) {
                        children.add(new Node(game, this, head.nextFullNode(), !max));
                    }
                }
            }
        }
    }
}
