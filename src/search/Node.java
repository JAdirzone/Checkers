package search;

import checkerComponents.Checker;
import checkerComponents.Game;
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.ArrayList;

/**
 * Created by Jay on 4/15/2017.
 */
public class Node {
    private static int maxDepth = 4;
    private Node parent;
    private Node child; //should replace children
    private ArrayList<Integer> move; //The move that this node represents
    private ArrayList<Integer> bestMove;
    private int value;
    private boolean max; //Whether this node is trying to maximize of minimize. Max is the black player's move
    private ArrayList<Checker> jumpedCheckers;//The checkers that were jumped in this node's move, in the order they were jumped
    private boolean movedKing;
    private int currentDepth;

    //board should be a copy of the board actually being used by the game.
    public Node(Game game, Node parent, ArrayList<Integer> move, boolean max, int currentDepth){
        this.parent = parent;
        this.move = move;
        //TODO is the checker about o move a king?
        movedKing = game.isKing(move.get(0), move.get(1));
        this.jumpedCheckers = game.move(move);
        this.max = max; //I know this is also set in the sub-constructor, but I can change it later
        //System.out.print(game.toString());
        if(!game.playerCanMove(false)){
            value = -1000000; //Make sure this is low enough
        }
        else if(!game.playerCanMove(true)){
            value = 1000000;
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
        //jumpedCheckers = new ArrayList<>(); //THIS DAMN LINE
        this.currentDepth = currentDepth;
        if(max){
            value = Integer.MIN_VALUE;
        }
        else{
            value = Integer.MAX_VALUE;
        }
        //TODO check the checker that's about to move to see if it is a king. NOT HERE
        //TODO Check for pruning here, for if children are possible. This means the hueristic will have to run here too. No it does not
        if(this.currentDepth <= maxDepth) {
            if (game.forcedJump()) {
                //System.out.println("Jumped");
                generateJumpChildren(game);
            } else {
                //System.out.println("Stepped");
                generateStepChildren(game);
            }
        }
        else{
            value = game.heuristic();
            System.out.println("value " + value + " depth " + currentDepth);
            //System.out.println("Stall");
        }
    }



    private boolean isBetterValue(int newValue){
        return (max && newValue > value) || (!max && newValue < value);
    }

    //Handles when this node is backtracked to.
    //TODO should undo its child's move, not its own.
    private void backTracked(Game game){
        System.out.println("Backtracked");
        if(isBetterValue(child.value)){
            System.out.println("max:" + max + " took " + child.value + " over " + value + " depth " + currentDepth);
            value = child.value;
            bestMove = child.move;
        }
        //undo step
        if(Math.abs(child.move.get(0) - child.move.get(2)) == 1){
            game.undoStep(child.move, !child.movedKing
                    && game.isKing(child.move.get(child.move.size() - 2), child.move.get(child.move.size() - 1))); //TODO is this right?
        }else{ //undo jump
            game.undoJump(child.move, child.jumpedCheckers, !child.movedKing
                    && game.isKing(child.move.get(child.move.size() - 2), child.move.get(child.move.size() - 1))); //TODO is this right?
        }
        // remove the child that just returned. May not be necessary
        child = null;
        game.nextTurn(); //technically switchturn
    }

    private void branch(Game game, ArrayList<Integer> nextMove){
        child = new Node(game, this, nextMove, !max, currentDepth + 1);
        backTracked(game);
    }

    //TODO values being passed to checkSubStep may be off by one. look into this. MADE CHANGE
    //
    private void generateStepChildren(Game game){
        for(int row = 0; row <= 7; row++){
            int preCol = row % 2;
            for(int column = preCol; column <= 7; column += 2){
                if(game.checkerCheck(column + 1, row + 1, !max)) {
                    if(game.checkSubStep(column + 1, row + 1, column + 2, row + 2)){
                        ArrayList<Integer> nextMove = new ArrayList<>();
                        nextMove.add(column + 1);
                        nextMove.add(row + 1);
                        nextMove.add(column + 2);
                        nextMove.add(row + 2);
                        branch(game, nextMove);
                    }
                    if(game.checkSubStep(column + 1, row + 1, column + 2, row)){
                        ArrayList<Integer> nextMove = new ArrayList<>();
                        nextMove.add(column + 1);
                        nextMove.add(row + 1);
                        nextMove.add(column + 2);
                        nextMove.add(row);
                        branch(game, nextMove);
                    }
                    if(game.checkSubStep(column + 1, row + 1, column, row + 2)){
                        ArrayList<Integer> nextMove = new ArrayList<>();
                        nextMove.add(column + 1);
                        nextMove.add(row + 1);
                        nextMove.add(column);
                        nextMove.add(row + 2);
                        branch(game, nextMove);
                    }
                    if(game.checkSubStep(column + 1, row + 1, column, row)){
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

    public void generateJumpChildren(Game game){
        for(int row = 0; row <= 7; row++){
            int preCol = row % 2;
            for(int column = preCol; column <= 7; column += 2) {
                if (game.checkerCheck(column + 1, row + 1, !max)) {
                    if(game.checkSubJump(column + 1, row + 1, column + 3, row + 3)){
                        JumpNodeHead head = new JumpNodeHead(game, column + 1, row + 1,
                                column + 3, row + 3);
                        branch(game, head.nextFullNode());
                        while (head.hasChildren()) {
                            branch(game, head.nextFullNode());
                        }
                    }
                    if(game.checkSubJump(column + 1, row + 1, column + 3, row - 1)){
                        JumpNodeHead head = new JumpNodeHead(game, column + 1, row + 1,
                                column + 3, row - 1);
                        branch(game, head.nextFullNode());
                        while (head.hasChildren()) {
                            branch(game, head.nextFullNode());
                        }
                    }
                    if(game.checkSubJump(column + 1, row + 1, column - 1, row + 3)){
                        JumpNodeHead head = new JumpNodeHead(game, column + 1, row + 1,
                                column - 1, row + 3);
                        branch(game, head.nextFullNode());
                        while (head.hasChildren()) {
                            branch(game, head.nextFullNode());
                        }
                    }
                    if(game.checkSubJump(column + 1, row + 1, column - 1, row - 1)){
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

    public ArrayList<Integer> getBestMove() {
        return bestMove;
    }
}
