package chessgui.pieces;

import chessgui.Board;
import static java.lang.Math.abs;
import static java.lang.Math.round;

public class Queen extends Piece {

    public Queen(int x, int y, boolean is_white, String file_path, Board board, int capturePoints)
    {
        super(x,y,is_white,file_path, board, capturePoints, "Q");
    }
    
    @Override
    public int canMove(int destination_x, int destination_y)
    {
        Piece collisionPiece;
        
        int x_difference = destination_x - getX();
        int y_difference = destination_y - getY();
        
        
        //dont allow 'non move' moves
        if(!(x_difference == 0 && y_difference == 0)){
            //diagonal movements
            if(abs(x_difference) == abs(y_difference)){
                
                boolean clearPath = true; 

                //direction variables to determine which direction to iterate the  pathfinding variables
                float x_direction = x_difference/abs(x_difference);
                float y_direction = y_difference/abs(y_difference);

                //iterations check each square in path of destination
                for(int i = 0; i < abs(x_difference)-1; i++){

                    collisionPiece = board.getPiece(round(getX()+x_direction), round(getY()+y_direction));               

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
                    else{
                        return 0;
                    }
                }
                //return false if path not clear
                else {
                    return -1;
                }
            }
            //lateral movement
            else if (x_difference == 0 || y_difference == 0){
                Boolean clearPath = true;

                //if moving vertically
                if(x_difference == 0){
                    //have to calculate direction in here to avoid divide by 0 error
                    int y_direction = y_difference / abs(y_difference);

                    //one iteration for every space inbetween start and end
                    for(int i = 0; i < abs(y_difference)-1; i++){
                        collisionPiece = board.getPiece(getX(), getY()+y_direction);               

                        //if piece is found in path, set clearPath to false
                        if(collisionPiece != null){
                            clearPath = false;
                        }
                    }

                    //if clearPath never falsed out, continue
                    if(clearPath){
                       collisionPiece = board.getPiece(destination_x, destination_y);

                       //if no piece sits on destination, return true
                       if(collisionPiece == null){
                           return 1;
                       }
                       //next 2 else ifs are to ensure captures only when opposite colors
                       else if(collisionPiece.isWhite() && !isWhite()){
                           return 1;
                       }
                       else if(!collisionPiece.isWhite() && isWhite()){
                           return 1;
                       }
                       else{
                           return 0;
                       }
                    }
                    //return false if path not clear
                    else{
                        return -1;
                    }
                }
                //moving horizontally
                else if(y_difference == 0){
                    //have to calculate direction in here to avoid divide by 0 error
                    int x_direction = x_difference / abs(x_difference);
                    //one iteration for every space inbetween start and end
                    for(int i = 0; i < abs(x_difference)-1; i++){
                        collisionPiece = board.getPiece(getX() + x_direction, getY());               

                        //if piece is found in path, set clearPath to false
                        if(collisionPiece != null){
                            clearPath = false;
                        }
                    }

                    //if clearPath never falsed out, continue
                    if(clearPath){
                       collisionPiece = board.getPiece(destination_x, destination_y);

                       //if no piece sits on destination, return true
                       if(collisionPiece == null){
                           return 1;
                       }
                       //next 2 else ifs are to ensure captures only when opposite colors
                       else if(collisionPiece.isWhite() && !isWhite()){
                           return 1;
                       }
                       else if(!collisionPiece.isWhite() && isWhite()){
                           return 1;
                       }
                       else{
                           return 0;
                       }
                    }
                    //return false if path not clear
                    else{
                        return -1;
                    }
                }
                else{
                    return -1;
                }
            }
            //shouldn't ever enter here
            else{
                return -1;
            }
        }
        //return false on non-moves
        else{
            return -1;
        }
    }
}
