package search;

import checkerComponents.Checker;
import checkerComponents.Game;
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.ArrayList;


public class Node {
    private static int maxDepth = 10;
    private Node parent;
    private Node child; //should replace children
    private ArrayList<Integer> move; //The move that this node represents
    private ArrayList<Integer> bestMove;
    private int value;
    private boolean max; //Whether this node is trying to maximize of minimize. Max is the black player's move
    private ArrayList<Checker> jumpedCheckers;//The checkers that were jumped in this node's move, in the order they were jumped
    private boolean movedKing;
    private int currentDepth;
    private boolean rated; //For pruning
    private boolean pruned; //If a node is pruned, it can no longer create children.

    //board should be a copy of the board actually being used by the game.
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
            rated = true;
            //System.out.println("value " + value + " depth " + currentDepth);
            //System.out.println("Stall");
        }
    }



    private boolean isBetterValue(int newValue){
        return (max && newValue > value) || (!max && newValue < value);
    }

    //Handles when this node is backtracked to.
    //TODO should undo its child's move, not its own.
    private void backTracked(Game game){

        //test
        //System.out.println("Backtracked from considering " + (child.move.get(2) - child.move.get(0)) + " " + (child.move.get(3) - child.move.get(1)));
        if(isBetterValue(child.value)){
            //System.out.println("max:" + max + " took " + child.value + " over " + value + " depth " + currentDepth);
            value = child.value;
            bestMove = child.move;
            pruned = shouldPrune(value);
            System.out.println(pruned);
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
        //game.nextTurn(); //technically switchturn THIS was problem, switching it may cause some more
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
            if(currentDepth == 0) {
                System.out.println(row);
                System.out.println(game.isWhiteTurn());
            }
            for(int column = preCol; column <= 7; column += 2){
                if(currentDepth == 0) {
                    System.out.println("col " + column);
                }
                if(game.checkerCheck(column + 1, row + 1, !max)) {
                    if(!pruned && game.checkSubStep(column + 1, row + 1, column + 2, row + 2)){
                        if(currentDepth == 0) {
                            System.out.println("Generated child from depth 0 for " + (column + 1) + "," + (row + 1) + game.isWhiteTurn());
                        }
                        ArrayList<Integer> nextMove = new ArrayList<>();
                        nextMove.add(column + 1);
                        nextMove.add(row + 1);
                        nextMove.add(column + 2);
                        nextMove.add(row + 2);
                        branch(game, nextMove);
                    }
                    if(!pruned && game.checkSubStep(column + 1, row + 1, column + 2, row)){
                        if(currentDepth == 0) {
                            System.out.println("Generated child from depth 0 for " + (column + 1) + "," + (row + 1) + game.isWhiteTurn());
                        }
                        ArrayList<Integer> nextMove = new ArrayList<>();
                        nextMove.add(column + 1);
                        nextMove.add(row + 1);
                        nextMove.add(column + 2);
                        nextMove.add(row);
                        branch(game, nextMove);
                    }
                    if(!pruned && game.checkSubStep(column + 1, row + 1, column, row + 2)){
                        if(currentDepth == 0) {
                            System.out.println("Generated child from depth 0 for " + (column + 1) + "," + (row + 1) + game.isWhiteTurn());
                        }
                        ArrayList<Integer> nextMove = new ArrayList<>();
                        nextMove.add(column + 1);
                        nextMove.add(row + 1);
                        nextMove.add(column);
                        nextMove.add(row + 2);
                        branch(game, nextMove);
                    }
                    if(!pruned && game.checkSubStep(column + 1, row + 1, column, row)){
                        if(currentDepth == 0) {
                            System.out.println("Generated child from depth 0 for " + (column + 1) + "," + (row + 1) + game.isWhiteTurn());
                        }
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

    private boolean shouldPrune(int newValue){
        return (currentDepth > 0
                &&((!max && parent.rated && parent.value >= newValue)
                || (max && parent.rated && parent.value <= newValue)));
    }


    public ArrayList<Integer> getBestMove() {
        return bestMove;
    }
}
