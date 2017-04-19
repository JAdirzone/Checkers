package search;

import checkerComponents.Checker;
import checkerComponents.Game;
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.ArrayList;
import java.util.Random;

/**
 * A Node represents a node in the tree that is being used to before the minimax algorithm.
 */
public class Node {
    private static int maxDepth = 8;
    private Node parent;
    private Node child;
    private ArrayList<Integer> move; //The move that this node represents
    private ArrayList<Integer> bestMove;
    private int value;
    private boolean max; //Whether this node is trying to maximize of minimize. Max is the black player's move
    private ArrayList<Checker> jumpedCheckers;//The checkers jumped in this node's move, in the order they were jumped
    private boolean movedKing;//Whether this node's move moved a king, so it can undo it when it back tracks back up.
    private int currentDepth;
    private boolean rated; //For pruning
    private boolean pruned; //If a node is pruned, it can no longer create children.


    public Node(Game game, Node parent, ArrayList<Integer> move, boolean max, int currentDepth){
        this.parent = parent;
        this.move = move;
        //TODO is the checker about o move a king?
        movedKing = game.isKing(move.get(0), move.get(1));
        this.jumpedCheckers = game.move(move);
        this.max = max; //I know this is also set in the sub-constructor
        //System.out.print(game.toString());
        if(!game.playerCanMove(false)){
            value = -1000000; //Make sure this is low enough
            rated = true;
        }
        else if(!game.playerCanMove(true)){
            value = 1000000;
            rated = true;
        }
        else {
            workAround(game, max, currentDepth);
        }
    }

    public Node(Game game, boolean max, int currentDepth){
        workAround(game, max, currentDepth);
    }

    //A work-around to allow me to use a constructor within another without putting it on the first line.
    public void workAround(Game game, boolean max, int currentDepth){
        bestMove  = new ArrayList<>();
        this.max = max;
        this.rated = false;
        this.pruned = false;
        //jumpedCheckers = new ArrayList<>(); //THIS DAMN LINE
        this.currentDepth = currentDepth;
        if(max){
            value = Integer.MIN_VALUE;
        }
        else{
            value = Integer.MAX_VALUE;
        }
        if(this.currentDepth <= maxDepth) {
            if (game.forcedJump()) {
                generateJumpChildren(game);
            } else {
                generateStepChildren(game);
            }
        }
        else{
            value = game.heuristic();
            //rated = true; //TODO shouldn't this be uncomment? I don't actually think it effects anything because only parent nodes are checked to see if they are rated, but I guess it should be marked rated anyway.
        }
    }


    /**
     *
     * @param newValue
     * @return Whether this node would prefer a given value over its current value.
     */
    private boolean isBetterValue(int newValue){
        // If two values are equal the new value will be judged as better half of the time.
        // This should help to vary the AI players moves.
        if(newValue == value){
            Random rand = new Random();
            return rand.nextInt(2) == 1;
        }
        return (max && newValue > value) || (!max && newValue < value);
    }

    /**
     * Handles when the node is back tracked to (when its child is done being evaluated).
     * @param game
     */
    private void backTracked(Game game){
        if(isBetterValue(child.value)){
            value = child.value;
            rated = true;
            bestMove = child.move;
            pruned = shouldPrune(value);
            //if(pruned){
            //    System.out.println("Max: " + max + " value " + value + " parent value " + parent.value + " parent rated " + parent.rated);
            //}
        }
        //undo step
        if(Math.abs(child.move.get(0) - child.move.get(2)) == 1){
            game.undoStep(child.move, !child.movedKing
                    && game.isKing(child.move.get(child.move.size() - 2), child.move.get(child.move.size() - 1))); //TODO is this right?
        }else{ //undo jump
            game.undoJump(child.move, child.jumpedCheckers, !child.movedKing
                    && game.isKing(child.move.get(child.move.size() - 2), child.move.get(child.move.size() - 1))); //TODO is this right?
        }
        // remove the child that just returned.
        child = null;
    }

    /**
     * Creates a child node for this node
     * @param game
     * @param nextMove The move that the child node represents.
     */
    private void branch(Game game, ArrayList<Integer> nextMove){
        child = new Node(game, this, nextMove, !max, currentDepth + 1);
        backTracked(game);
    }

    /**
     * Generate child for this node. Each child created will be the result of a "step", or a non-jump move.
     * @param game
     */
    private void generateStepChildren(Game game){
        for(int row = 0; row <= 7; row++){
            int preCol = row % 2;
            //if(currentDepth == 0) {
                //System.out.println(row);
                //System.out.println(game.isWhiteTurn());
            //}
            for(int column = preCol; column <= 7; column += 2){
                if(currentDepth == 0) {
                    //System.out.println("col " + column);
                }
                if(game.checkerCheck(column + 1, row + 1, !max)) {
                    if(!pruned && game.checkSubStep(column + 1, row + 1, column + 2, row + 2)){
                        if(currentDepth == 0) {
                            //System.out.println("Generated child from depth 0 for " + (column + 1) + "," + (row + 1) + game.isWhiteTurn());
                        }
                        ArrayList<Integer> nextMove = new ArrayList<>();
                        nextMove.add(column + 1);
                        nextMove.add(row + 1);
                        nextMove.add(column + 2);
                        nextMove.add(row + 2);
                        branch(game, nextMove);
                    }
                    if(!pruned && game.checkSubStep(column + 1, row + 1, column + 2, row)){
                        //if(currentDepth == 0) {
                        //    System.out.println("Generated child from depth 0 for " + (column + 1) + "," + (row + 1) + game.isWhiteTurn());
                        //}
                        ArrayList<Integer> nextMove = new ArrayList<>();
                        nextMove.add(column + 1);
                        nextMove.add(row + 1);
                        nextMove.add(column + 2);
                        nextMove.add(row);
                        branch(game, nextMove);
                    }
                    if(!pruned && game.checkSubStep(column + 1, row + 1, column, row + 2)){
                        //if(currentDepth == 0) {
                            //System.out.println("Generated child from depth 0 for " + (column + 1) + "," + (row + 1) + game.isWhiteTurn());
                        //}
                        ArrayList<Integer> nextMove = new ArrayList<>();
                        nextMove.add(column + 1);
                        nextMove.add(row + 1);
                        nextMove.add(column);
                        nextMove.add(row + 2);
                        branch(game, nextMove);
                    }
                    if(!pruned && game.checkSubStep(column + 1, row + 1, column, row)){
                        //if(currentDepth == 0) {
                            //System.out.println("Generated child from depth 0 for " + (column + 1) + "," + (row + 1) + game.isWhiteTurn());
                        //}
                        //System.out.println("Considered down left");
                        ArrayList<Integer> nextMove = new ArrayList<>();
                        nextMove.add(column + 1);
                        nextMove.add(row + 1);
                        nextMove.add(column);
                        nextMove.add(row);
                        branch(game, nextMove);
                    }
                }
            }
        }
    }

    /**
     * Generate children for this node. Each child will be the result of a jump move.
     * @param game
     */
    public void generateJumpChildren(Game game){
        for(int row = 0; row <= 7; row++){
            int preCol = row % 2;
            for(int column = preCol; column <= 7; column += 2) {
                if (game.checkerCheck(column + 1, row + 1, !max)) {
                    if(!pruned && game.checkSubJump(column + 1, row + 1, column + 3, row + 3, false)){
                        JumpNodeHead head = new JumpNodeHead(game, column + 1, row + 1,
                                column + 3, row + 3);
                        branch(game, head.nextFullNode());
                        while (head.hasChildren()) {
                            branch(game, head.nextFullNode());
                        }
                    }
                    if(!pruned && game.checkSubJump(column + 1, row + 1, column + 3, row - 1, false)){
                        JumpNodeHead head = new JumpNodeHead(game, column + 1, row + 1,
                                column + 3, row - 1);
                        branch(game, head.nextFullNode());
                        while (head.hasChildren()) {
                            branch(game, head.nextFullNode());
                        }
                    }
                    if(!pruned && game.checkSubJump(column + 1, row + 1, column - 1, row + 3, false)){
                        JumpNodeHead head = new JumpNodeHead(game, column + 1, row + 1,
                                column - 1, row + 3);
                        branch(game, head.nextFullNode());
                        while (head.hasChildren()) {
                            branch(game, head.nextFullNode());
                        }
                    }
                    if(!pruned && game.checkSubJump(column + 1, row + 1, column - 1, row - 1, false)){
                        JumpNodeHead head = new JumpNodeHead(game, column + 1, row + 1,
                                column - 1, row - 1);
                        branch(game, head.nextFullNode());
                        while (head.hasChildren()) {
                            branch(game, head.nextFullNode());
                        }
                    }
                }
            }
        }
    }

    /**
     * @param newValue
     * @return Whether a node should be pruned based its new value as well as the value of its
     * opposite (max -> min, min -> max) ancestors.
     */
    private boolean shouldPrune(int newValue){
        if(this.currentDepth > 0) {
            Node interestNode = this.parent;
            while (interestNode.parent != null) {
                if ((!max && interestNode.max && interestNode.rated && interestNode.value >= newValue)
                        || (max && !interestNode.max && interestNode.rated && interestNode.value <= newValue)) {
                    System.out.println("Max: " + max + " value " + value + " parent value " + interestNode.value + " ancestor max " + interestNode.max);
                    return true;
                }
                if(interestNode.currentDepth >= 0){
                    interestNode = interestNode.parent;
                }

            }
        }
        return false;
    }


    public ArrayList<Integer> getBestMove() {
        return bestMove;
    }
}
