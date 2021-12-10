package chessgui.pieces;

import chessgui.Board;
import static java.lang.Math.abs;

public class King extends Piece {

    int starting_x, starting_y;
    boolean mateScanning = false;
    
    public King(int x, int y, boolean is_white, String file_path, Board board, int capturePoints)
    {
        super(x,y,is_white,file_path, board, capturePoints, "K");
    }
    
    @Override
    public int canMove(int destination_x, int destination_y)
    {
        int x_difference = destination_x - getX();
        int y_difference = destination_y - getY();
        
        //are they moving a single square at a time
        //need to add in >0 for at least 1
        if (abs(x_difference) <= 1 && abs(y_difference) <= 1 && !(x_difference == 0 && y_difference == 0)){
            
            if(checkScan(destination_x, destination_y)){
                if(board.getPiece(destination_x, destination_y) != null){
                    if(board.getPiece(destination_x, destination_y).isWhite() == isWhite()){
                        return 0;
                    }
                    else{
                        return 1;
                    }
                }
                else{
                    return 1;
                }
            }
            else{
                return -1;
            }          
        }
        else if(getMoveCounter() == 0){
            
            
            Boolean clearPath = true;
            //castling right
            if(destination_x - getX() == 2 && destination_y - getY() == 0){
                
                Piece castlePiece = board.getPiece(7, getY());
                //can only be rook piece if in corner and movecounter = 0
                if(castlePiece.getMoveCounter() == 0){
                    
                    Piece collisionPiece;
                    //clear path check
                    for(int i = (getX()+1); i < 6; i++){
                        collisionPiece = board.getPiece(i, getY());
                        
                        if(collisionPiece != null){
                            clearPath = false;
                        }
                    }
                }
                else{
                    clearPath = false;
                }
            }
            //castling left
            else if(destination_x - getX() == (-2) && destination_y - getY() == 0){
                Piece castlePiece = board.getPiece(0, getY());
                //can only be rook piece if in corner and movecounter = 0
                if(castlePiece.getMoveCounter() == 0){
                    
                    Piece collisionPiece;
                    //clear path check
                    for(int i = (getX()-1); i > 1; i--){
                        collisionPiece = board.getPiece(i, getY());
                        
                        if(collisionPiece != null){
                            clearPath = false;
                        }
                    }
                }
                else{
                    clearPath = false;
                }
            }
            //illegal move
            else{
                return -1;
            }
//            //extra 
//            board.castleMove = true;
            
            if(clearPath){
                return 1;
            }
            else{
                return -1;
            }
        }
        //return false when trying to move move than 1 square at a time, or trying to castle without it available
        else{
            return -1;
        }
    }
    
    //return true if no opponent piece has the king in check at given location
    @Override
    public Boolean checkScan(int x, int y){
        
        boolean clearMove = true;
        
        
        //check for every attacker except horses
        for (int i=-1; i <= 1; i++){
            for(int j=-1; j<= 1; j++){
                
                boolean pieceFound = false;
                int tempX = x;
                int tempY = y;
                
                while(!pieceFound && !(i==0 && j==0)){
                    tempX += i;
                    tempY += j;
                    
                    if(!(tempX == getX() && tempY == getY())){
                        Piece attackingPiece = board.getPiece(tempX, tempY);
                        
            
                        //if target is within board bounds
                        if(tempX <=7 && tempX >=0 && tempY <=7 && tempY >=0){

                            //if piece is found
                            if(attackingPiece != null && !attackingPiece.getFilePath().equals("King.png")){ 
                                pieceFound = true;
                                if(attackingPiece.isWhite() != isWhite()){
                                    //attacking piece cant move when called bc friendly piece on square
                                    if(attackingPiece.canMove(x, y) > -1){
                                        clearMove= false;
                                    } 
                                }                                  
                            }
                            else if(attackingPiece != null && attackingPiece.getFilePath().equals("King.png")){
                                if((Math.abs(attackingPiece.getX()-x) <= 1) && (Math.abs(attackingPiece.getY()-y) <= 1)){
                                    clearMove = false;
                                }
                            }
                        }
                        //pieceFound = true when ran off board without colliding or targeting self
                        else{
                            pieceFound = true;
                        }
                    }
                }   
            }
        }
        
        //check for horses
        //this is gross but I don't imagine a clean algorithim will make it any more efficient
            if(board.getPiece(x+2, y+1) != null && board.getPiece(x+2, y+1).canMove(x, y) > -1 && board.getPiece(x + 2, y + 1).isWhite() != isWhite()){
                clearMove = false;
            }
            else if(board.getPiece(x+2, y-1) != null && board.getPiece(x+2, y-1).canMove(x, y) > -1 && board.getPiece(x + 2, y - 1).isWhite() != isWhite()){
                clearMove = false;
            }
            else if (board.getPiece(x - 2, y - 1) != null && board.getPiece(x-2, y-1).canMove(x, y)> -1 && board.getPiece(x - 2, y - 1).isWhite() != isWhite()) {
                clearMove = false;
            }
            else if(board.getPiece(x-2, y+1) != null && board.getPiece(x-2, y+1).canMove(x, y)> -1 && board.getPiece(x - 2, y + 1).isWhite() != isWhite()){
                clearMove = false;
            }
            else if(board.getPiece(x+1, y+2) != null && board.getPiece(x+1, y+2).canMove(x, y)> -1 && board.getPiece(x +1, y +2).isWhite() != isWhite()){
                clearMove = false;
            }
            else if(board.getPiece(x+1, y-2) != null && board.getPiece(x+1, y-2).canMove(x,y)> -1 && board.getPiece(x +1, y-2).isWhite() != isWhite()){
                clearMove = false;
            }
            else if(board.getPiece(x-1, y-2) != null && board.getPiece(x-1, y-2).canMove(x, y)> -1 && board.getPiece(x -1, y-2).isWhite() != isWhite()){
                clearMove = false;
            }
            else if(board.getPiece(x-1, y+2) != null && board.getPiece(x-1, y+2).canMove(x,y)> -1 && board.getPiece(x -1, y+2).isWhite() != isWhite()){
                clearMove = false;
            }

        return clearMove;
    }
    
    @Override 
    public Piece findCheck(){
        Piece checkPiece = null;
        
        
        //check for every attacker except horses
        for (int i=-1; i <= 1; i++){
            for(int j=-1; j<= 1; j++){
                
                boolean pieceFound = false;
                int tempX = getX();
                int tempY = getY();
                
                while(!pieceFound){
                    tempX += i;
                    tempY += j;
                    Piece attackingPiece = board.getPiece(tempX, tempY);
                    
                    if(!(i == 0 && j==0)){
                        //if target is within board bounds
                        if(tempX <=7 && tempX >=0 && tempY <=7 && tempY >=0){

                            //if piece is found
                            if(attackingPiece != null){                           
                                pieceFound = true;

                                //return false if located piece is able to attack destination (self discovered check)
                                if(attackingPiece.canMove(getX(), getY()) > -1){
                                   checkPiece = attackingPiece;
                                }   
                            }
                        }
                        //pieceFound = true when ran off board without colliding
                        else{
                            pieceFound = true;
                        }
                    }
                    //self targeting
                    else{
                        pieceFound = true;
                    }
                }
            }
        }
        
        //check for horses
//        for(int i=-2; i < 2; i++){
//            for(int j=-2; j < 2; j++)
//        }
        return checkPiece;
    }
    
    @Override
    public Boolean checkMateScan(){
        
        //double loop to check every square around the king
        for(int i = -1; i < 2; i++){
            for(int j = -1; j < 2; j++){
                
                //removes exceptions for out of bounds and checking current location
                if(getX() + i <= 7 && getX() + i >= 0 && getY() + j <= 7 && getY() + j >= 0 && !(i == 0 && j == 0)){
                    //if null, no attacker
                    if(board.getPiece(getX() + i, getY() + j) != null){
                        //make sure opposite colors if attacking
                        if(board.getPiece(getX() + i, getY() + j).isWhite() != isWhite()){
                            if(checkScan(getX() + i, getY() + j)){
                                return true;
                            }
                        }
                    }
                    else{
                        if(checkScan(getX() + i, getY() + j)){
                            return true;
                        }
                    }
                }
            }
            
        }
        
        //need to check if any friendly piece can block
        //was easier to do in board object bc had access to all pieces 
        if(board.canBeBlocked(getX(), getY(), isWhite())){
            return true;
        }       
        
        return false;
    }

    public Piece findPiece(int x_direction, int y_direction, int starting_x, int starting_y){
        
       Piece collisionPiece = board.getPiece(starting_x, starting_y);
        
        while(collisionPiece == null && starting_x <= 8 && starting_y <=8){
            starting_x += x_direction;
            starting_y += y_direction;
            collisionPiece = board.getPiece(starting_x, starting_y);           
        }
        return collisionPiece;
    }
}
