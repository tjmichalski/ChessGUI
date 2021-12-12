package chessgui.pieces;

import chessgui.Board;
import static java.lang.Math.abs;
import static java.lang.Math.round;

public class Bishop extends Piece {

    public Bishop(int x, int y, boolean is_white, String file_path, Board board, int capturePoints)
    {
        super(x,y,is_white,file_path, board, capturePoints, "B");
    }
    
    @Override
    public int canMove(int destination_x, int destination_y)
    {
        //used later for pathfinding and collision detection
        Piece collisionPiece;
        
        //difference variables to determine if valid diagonal move
        int x_difference = destination_x - getX();
        int y_difference = destination_y - getY();
        
        //if move is diagonal
        if(abs(x_difference) == abs(y_difference) && x_difference != 0 && y_difference != 0){
            
            //clearPath variable falses out if any piece is in the path of movement (does not evaluate on end point though)
            boolean clearPath = true; 
            
            //direction variables to determine which direction to iterate the  pathfinding variables
            int x_direction = x_difference/abs(x_difference);
            int y_direction = y_difference/abs(y_difference);
            
            //iterations check each square in path of destination
            for(int i = 1; i < abs(x_difference); i++){
                
                collisionPiece = board.getPiece(getX()+(x_direction*i), getY()+(y_direction*i));               
                
                //if piece is found in path, set clearPath to false
                if(collisionPiece != null){
                    clearPath = false;
                }
            }
            
            //collisionPiece set last time for attack detection
            collisionPiece = board.getPiece(destination_x, destination_y);
            
            //only proceed if clearPath never falsed out
            if(clearPath) {
                
                //if no piece occupies the square, return true
                if(collisionPiece == null){
                    return 1;
                }
                //next two else if's determine if pieces are of opposite color
                else if(collisionPiece.isWhite() && !isWhite()){
                    return 1;
                }
                else if(!collisionPiece.isWhite() && isWhite()){
                    return 1;
                }
                //else same color and can DEFEND that square
                else{
                    return 0;
                }
            }
            //return false if path not clear
            else {
                return -1;
            }
            
        }
        else{
            return -1;
        }
    }
}
