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
    //private int bestMoveValue; //Best move value and value are the same thing.
    private int value;
    private boolean max; //Whether this node is trying to maximize of minimize. Max is the black player's move
    private ArrayList<Checker> jumpedCheckers;//The checkers that were jumped in this node's move, in the order they were jumped
    private boolean movedKing;
    private int currentDepth;

    //board should be a copy of the board actually being used by the game.
    public Node(Game game, Node parent, ArrayList<Integer> move, boolean max, int currentDepth){
        this.parent = parent;
        this.move = move;
        jumpedCheckers = game.move(move);
        workAround(game, max, currentDepth);
    }

    public Node(Game game, boolean max, int currentDepth){
        workAround(game, max, currentDepth);
    }

    //A work-around to allow me to use a constructor within another without putting it on the first line.
    public void workAround(Game game, boolean max, int currentDepth){
        bestMove  = new ArrayList<>();
        this.max = max;
        jumpedCheckers = new ArrayList<>();
        this.currentDepth = currentDepth;
        //TODO check the check that's about to move to see if it is a king.
        //TODO Check for pruning here, for if children are possible. This means the hueristic will have to run here too. No it does not
        if(this.currentDepth <= maxDepth) {
            if (game.forcedJump()) {
                generateJumpChildren(game);
            } else {
                generateStepChildren(game);
            }
        }
        else{
            value = game.heuristic();
        }
    }



    private boolean isBetterValue(int newValue){
        return (max && newValue > value) || (!max && newValue < value);
    }

    //Handles when this node is backtracked to.
    //TODO should undo its child's move, not its own.
    private void backTracked(Game game){
        if(isBetterValue(child.value)){
            value = child.value;
            bestMove = child.move;
        }
        //undo step
        if(Math.abs(child.bestMove.get(0) - child.bestMove.get(2)) == 1){
            game.undoStep(child.move);
        }else{ //undo jump
            game.undoJump(child.move, jumpedCheckers);
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
