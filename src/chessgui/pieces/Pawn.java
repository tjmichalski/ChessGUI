package chessgui.pieces;

import chessgui.Board;

import static java.lang.Math.abs;
public class Pawn extends Piece {

    private boolean has_moved;
    
    public Pawn(int x, int y, boolean is_white, String file_path, Board board, int capturePoints)
    {
        //calls constructor of extended object
        //has move = false for double first move, possibly add third state for fancy take (first move, has moved, not moved)
        super(x,y,is_white,file_path, board, capturePoints, "P");
        has_moved = false;
    }
    
    public void setHasMoved(boolean has_moved)
    {
        this.has_moved = has_moved;
    }
    
    public boolean getHasMoved()
    {
        return has_moved;
    }
    
    @Override
    @SuppressWarnings("empty-statement")
    public int canMove(int destination_x, int destination_y)
    {
        Piece collisionPiece1 = board.getPiece(destination_x, destination_y);
        
       
        //is it the pawns first move of the game? if yes, proceed
        if (!getHasMoved()){
            
            //is the destination in a straight line? if yes proceed
            if (destination_x == getX()){
               
               //what color is pawn for direction calculations 
               if(isWhite()){
                   
                   //needs created here for direction
                   Piece collisionPiece2 = board.getPiece(destination_x, destination_y-1);
                   
                   //moving 1 forward and spot is empty, return true
                   if(destination_y == getY()+1 && collisionPiece1 == null){
                       return 1;
                   } 
                   //else if moving 2 spots forward and both are empty, return true
                   else if(destination_y == getY()+2 && collisionPiece1 == null && collisionPiece2 == null){
                       return 1;
                   }
                   ////denies any move great than 2 spots on first jump
                   else{
                       return -1;
                   }
               }
               //black piece first move, moving straight
               else{
                   
                   //needs created here for direction
                   Piece collisionPiece2 = board.getPiece(destination_x, destination_y+1);
                   
                   //moving 1 forward and spot is empty, return true
                   if(destination_y == getY()-1 && collisionPiece1 == null){
                       return 1;
                   }
                   
                   //else if moving 2 spots forward and both are empty, return true
                   else if(destination_y == getY()-2 && collisionPiece1 == null && collisionPiece2 == null){
                       return 1;
                   }
                   //denies any move great than 2 spots on first jump
                   else{
                       return -1;
                   }
               }
            }
            
            //diagonal movement nest
            //think fancy take would come next in hierarchy
            else{
                
                //separate colors for direction
                if(isWhite()){
                    
                    //if moving one spot diagonally forward
                    if(destination_y == getY()+1 && abs(destination_x-getX())==1){
                        

                        //is there a piece on the location
                        if(collisionPiece1 != null){
                            //is there a black piece on the destination
                            if(!collisionPiece1.isWhite()){
                              return 1;  
                            }
                            //tried to attack a white piece
                            else{
                                return 0;
                            }
                        }
                        //tried to move diagonally into no piece
                        else{
                            return -1;
                        }
                    }
                    
                    //return false when not moving diagonally one spot
                    else{
                        return -1;
                    }
                }
                
                //black pawn moving diagonally
                else{
                    if(destination_y == getY()-1 && abs(destination_x-getX())==1){
                        //is there a piece on the location
                        if(collisionPiece1 != null){
                            //is there a white piece on the destination
                            if(collisionPiece1.isWhite()){
                              return 1;  
                            }
                            //tried to attack a black piece
                            else{
                                return 0;
                            }
                        }
                        //tried to move diagonally into no piece
                        else{
                            return -1;
                        }
                    }
                    else{
                        return -1;
                    }
                }
            }
        }
        //not the pawns first move
        else{
            //separate colors
            if(isWhite()){
                //is the white pawn moving forward
                 if(destination_y == getY()+1){
                     //is the white pawn moving diagonally
                     if(abs(destination_x - getX()) == 1){
                         //if the spot is occupied by a black piece
                         if(collisionPiece1 != null){
                             if(!collisionPiece1.isWhite()){
                                 return 1;
                             }
                             else{
                                 //return false when diagonal attack is white
                                 return 0;
                             }
                         }
                         //return false when diagonal move is empty
                         else {
                            Piece collisionPiece3 = board.getPiece(destination_x, destination_y-1);
                            if(collisionPiece3 == null){
                                return -1;
                            }
                            //en passant
                            //checks several rules of the move
                            if(destination_y == 5 && collisionPiece3.getMoveCounter() == 1 && collisionPiece3.getClass().equals(Pawn.class) 
                                    && !(isWhite() == collisionPiece3.isWhite()) && board.lastMoved == collisionPiece3){
                                board.enPassantMove = true;
                                return 1;
                            }
                            else{
                                return -1;
                            }
                         }
                     }
                     //non-diagonal, forward 1 move
                     else if(destination_x == getX()){
                         //if the spot is empty
                         if(collisionPiece1 == null){
                             return 1;
                         }
                         //pawn tried to move forward 1 onto a piece
                         else{
                             return -1;
                         }
                     }
                     //tried moving sideways
                     else {
                         return -1;
                     }
                 }
                 else{
                     return -1;
                 }
            }  
            //black pawn, not first move
            else{
                //is the black pawn moving forward
                if(destination_y == getY()-1){
                    //is the black pawn moving diagonally
                     if(abs(destination_x - getX()) == 1){
                         //if the spot is occupied by a white piece
                         if(collisionPiece1 != null){
                             if(collisionPiece1.isWhite()){
                                 return 1;
                             }
                             else{
                                 //return false when diagonal attack is black
                                 return 0;
                             }
                         }
                         //return false when diagonal move is empty
                         else {
                            Piece collisionPiece3 = board.getPiece(destination_x, destination_y-1);
                            //en passant
                            if(collisionPiece3 == null){
                                return -1;
                            }
                            if(destination_y == 2 && collisionPiece3.getMoveCounter() == 1 && collisionPiece3.getClass().equals(Pawn.class) && !(isWhite() == collisionPiece3.isWhite())){
                                board.enPassantMove = true;
                                return 1;
                            }
                            else{
                                return -1;
                            }
                         }
                     }
                     //non-diagonal, forward 1 move
                     else if(destination_x == getX()){
                         //if the spot is empty
                         if(collisionPiece1 == null){
                             return 1;
                         }
                         //pawn tried to move forward 1 onto a piece
                         else{
                             return -1;
                         }
                     }
                     //tried moving sideways
                     else {
                         return -1;
                     }
                }
                else{
                    return -1;
                }
            }
        }
    }
}
