package chessgui.pieces;

import chessgui.Board;
import java.awt.Point;
import java.awt.geom.Point2D;
import static java.lang.Math.abs;
import java.util.ArrayList;

public class Piece {
    private int x;
    private int y;
    private int last_x;
    private int last_y;
    final private boolean is_white;
    private String file_path;
    public Board board;
    private int moveCounter;
    private final int capturePoints;
    private String notationName;
    
    public Piece(int x, int y, boolean is_white, String file_path, Board board, int capturePoints, String notationName)
    {
        this.is_white = is_white;
        this.x = x;
        this.y = y;
        this.file_path = file_path;
        this.board = board;
        this.moveCounter = 0;
        this.capturePoints = capturePoints;
        this.notationName = notationName;
    }
    
    public int getCapturePoints(){
        return capturePoints;
    }

    public String getNotationName() {
        return notationName;
    }

    public void setNotationName(String notationName) {
        this.notationName = notationName;
    }

    public String getFilePath()
    {
        return file_path;
    }
    
    public void setFilePath(String path)
    {
        this.file_path = path;
    }
    
    public boolean isWhite()
    {
        return is_white;
    }
    
    public void setX(int x)
    {
        this.last_x = this.x;
        this.x = x;
    }
    
    public void setY(int y)
    {
        this.last_y = this.y;
        this.y = y;
    }
    
    public int getX()
    {
        return x;
    }
    
    public int getY()
    {
        return y;
    }
    
    public int canMove(int destination_x, int destination_y)
    {
        return 0;
    }

    public int getLast_x() {
        return last_x;
    }

    public void setLast_x(int last_x) {
        this.last_x = last_x;
    }

    public int getLast_y() {
        return last_y;
    }

    public void setLast_y(int last_y) {
        this.last_y = last_y;
    }
    public int getMoveCounter() {
        return moveCounter;
    }

    public void setMoveCounter(int moveCounter) {
        this.moveCounter = moveCounter;
    }
    
    
    
    @Override
    public String toString(){
        String toString = x + " - " + y;
        
        return toString;
    }
    public Boolean checkScan(int destination_x, int destination_y){
        return false;
    }
    public Boolean checkMateScan(){
        return false;
    }
    public Piece findCheck(){
        return null;
    }
    public Piece[] findChecks(){
        return null;
    }
    public ArrayList<Point2D> findAttackSquares(int kingX, int kingY) {
        ArrayList<Point2D> attackSquares = new ArrayList();
        attackSquares.add(new Point(getX(), getY()));
        
        int diffX = kingX - getX();
        int diffY = kingY - getY();
        
        int slopeX, slopeY;
        
        if(diffY == 0){
            slopeY = 0;
        }
        else {
            slopeY = diffY/abs(diffY); 
        }
        
        if(diffX == 0){
            slopeX = 0;
        }
        else {
            slopeX = diffX/abs(diffX); 
        }
        
        for(int i=1; i < Math.max(abs(diffX), abs(diffY)); i++){
            attackSquares.add(new Point(getX()+(slopeX*i), getY() + (slopeY*i)));
        }
        
        return attackSquares;
    }
}
