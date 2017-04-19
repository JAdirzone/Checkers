package checkerComponents;


public enum Checker {
    BLACK(false, false),
    WHITE(true, false),
    BLACKKING(false, true),
    WHITEKING(true, true),
    EMPTY,
    UNUSABLE;

    private boolean isChecker; //Is it an actual playing piece?
    private boolean isWhite; //True if white, false if black, false otherwise. Be careful with this.
    private boolean isKing;

    private Checker(boolean isWhite, boolean isKing) {
        this.isChecker = true;
        this.isWhite = isWhite;
        this.isKing = isKing;
    }

    private Checker() {
        this.isChecker = false;
        this.isWhite = false;
    }

    public boolean isOppositeColor(Checker otherChecker) {
        return this.isChecker && otherChecker.isChecker && (this.isWhite != otherChecker.isWhite);
    }

    public boolean isChecker() {
        return isChecker;
    }

    public boolean isWhite() {
        return isWhite;
    }

    public boolean isKing() {
        return isKing;
    }

    public Checker kingMe() {
        if(isWhite){
            return WHITEKING;
        }else {
            return BLACKKING;
        }
    }

    public Checker revertKing() {
        if(isWhite) {
            return WHITE;
        }else {
            return BLACK;
        }
    }
}

