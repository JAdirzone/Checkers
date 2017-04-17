package search;

import checkerComponents.Checker;
import checkerComponents.Game;
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.ArrayList;

/**
 * Created by Jay on 4/15/2017.
 */
public class Node {
    //It may be better not to store many of these as instance variables; Just take them as arguments.
    private static int maxDepth = 5;
    private Node parent;
    //private ArrayList<Node> children; //I think it only ever has one child. If that is the case, switch
    private Node child; //should replace children
    private ArrayList<Integer> move; //The move that this node represents
    private ArrayList<Integer> bestMove;
    private int bestMoveValue; //Best move value and value are the same thing.
    private int value;
    private boolean max; //Whether this node is trying to maximize of minimize. Max is the black player's move
    private ArrayList<Checker> jumpedCheckers;//The checkers that were jumped in this node's move, in the order they were jumped
    private boolean movedKing;
    private int currentDepth;

    //board should be a copy of the board actually being used by the game.
    public Node(Game game, Node par, ArrayList<Integer> causingMove, boolean maxMin, int currentDepth){
        //children  = new ArrayList<>();
        bestMove  = new ArrayList<>();
        max = maxMin;
        parent = par;
        move = causingMove;
        jumpedCheckers = new ArrayList<>();
        this.currentDepth = currentDepth;
        //TODO perform the move that this node represents here
        //TODO Check for pruning here, for if children are possible. This means the hueristic will have to run here too. No it does not
        if(this.currentDepth <= maxDepth) {
            if (game.forcedJump()) {
                generateStepChildren(game);
            } else {
                generateJumpChildren(game);
            }
        }
    }

    private boolean isBetterValue(int newValue){
        return (max && newValue > bestMoveValue) || (!max && newValue < bestMoveValue);
    }

    //Handles when this node is backtracked to.
    private void backTracked(Game game){
        if(isBetterValue(child.value)){
            bestMove = child.move;
        }
        //undo step
        if(Math.abs(child.bestMove.get(0) - child.bestMove.get(2)) == 1){
            game.undoStep(move);
        }else{ //undo jump
            game.undoJump(move, jumpedCheckers);
        }
        // remove the child that just returned. May not be necessary
        child = null;
    }

    private void branch(Game game, ArrayList<Integer> nextMove){
        child = new Node(game, this, nextMove, !max, currentDepth + 1);
        backTracked(game);
    }

    //TODO values being passed to checkSubStep may be off by one. look into this.
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
                        branch(game, nextMove);
                    }
                    if(game.checkSubStep(column, row, column - 1, row + 1)){
                        ArrayList<Integer> nextMove = new ArrayList<>();
                        nextMove.add(column);
                        nextMove.add(row);
                        nextMove.add(column - 1);
                        nextMove.add(row + 1);
                        branch(game, nextMove);
                    }
                    if(game.checkSubStep(column, row, column - 1, row - 1)){
                        ArrayList<Integer> nextMove = new ArrayList<>();
                        nextMove.add(column);
                        nextMove.add(row);
                        nextMove.add(column - 1);
                        nextMove.add(row - 1);
                        branch(game, nextMove);
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
                    if(game.checkSubJump(column + 1, row + 1, column + 3, row + 3)){
                        JumpNodeHead head = new JumpNodeHead(game, column + 1, row + 1,
                                column + 3, row + 3);
                        while (head.hasChildren()) {
                            branch(game, head.nextFullNode());
                        }
                    }
                    if(game.checkSubJump(column + 1, row + 1, column + 3, row - 1)){
                        JumpNodeHead head = new JumpNodeHead(game, column + 1, row + 1,
                                column + 3, row - 1);
                        while (head.hasChildren()) {
                            branch(game, head.nextFullNode());
                        }
                    }
                    if(game.checkSubJump(column + 1, row + 1, column - 1, row + 3)){
                        JumpNodeHead head = new JumpNodeHead(game, column + 1, row + 1,
                                column - 1, row + 3);
                        while (head.hasChildren()) {
                            branch(game, head.nextFullNode());
                        }
                    }
                    if(game.checkSubJump(column + 1, row + 1, column - 1, row - 1)){
                        JumpNodeHead head = new JumpNodeHead(game, column + 1, row + 1,
                                column - 1, row - 1);
                        while (head.hasChildren()) {
                            branch(game, head.nextFullNode());
                        }
                    }
                }
            }
        }
    }

    public ArrayList<Integer> getBestMove() {
        return bestMove;
    }
}
