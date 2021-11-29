package chessgui.pieces;

import chessgui.Board;
import static java.lang.Math.abs;

public class Knight extends Piece {

    public Knight(int x, int y, boolean is_white, String file_path, Board board, int capturePoints)
    {
        super(x,y,is_white,file_path, board, capturePoints, "N");
    }
    
    @Override
    public int canMove(int destination_x, int destination_y)
    {    
        //used for determining if using an attack or just a movement
        Piece collisionPiece = board.getPiece(destination_x, destination_y);
        
        //absolute values for determining knight movement rules
        if ((abs(destination_x - getX()) == 1 && abs(destination_y - getY()) == 2) || abs(destination_x - getX()) == 2 && abs(destination_y - getY()) == 1){
            //if no piece present on square then return true
            if(collisionPiece == null){
                return 1;
            }
            //if attacking piece is white and defeding is black, return true
            else if (isWhite() && !collisionPiece.isWhite()){
                return 1;
            }
            //if attacking piece is black and defending is white, return true
            else if(!isWhite() && collisionPiece.isWhite()){
                return 1;
            }
            else {
                return 0;
            }
        }
        else{
            return -1;
        }
    }
}
