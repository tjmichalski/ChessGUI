package chessgui;

import chessgui.pieces.Pawn;
import chessgui.pieces.Piece;
import java.awt.geom.Point2D;
import java.io.IOException;
import static java.lang.Math.abs;
import static java.lang.Math.round;
import static java.lang.Math.sqrt;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tylar
 */
public class AutoPlayer {
    
    private final Board board;
    private String[] selectedOpening = {"3133", "6052", "2123"};
    public int moveCounter;
    public ArrayList<String> bannedMoves;
    private boolean mateScan;
    
    public AutoPlayer (Board board){
        this.moveCounter = 0;
        this.board = board;
        this.bannedMoves = new ArrayList();
        this.mateScan = false;
        try {
            getNextMove();
        } catch (IOException ex) {
            Logger.getLogger(AutoPlayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //order of checks to decide next move
    public void getNextMove() throws IOException{ 
            if(board.whiteKing.checkScan(board.whiteKing.getX(), board.whiteKing.getY())){
                    if(moveCounter < selectedOpening.length && !bannedMoves.contains("" + selectedOpening[moveCounter].charAt(0) + selectedOpening[moveCounter].charAt(1) + 
                                selectedOpening[moveCounter].charAt(2) + selectedOpening[moveCounter].charAt(3)) && board.getPiece(Integer.parseInt(selectedOpening[moveCounter].charAt(0) + ""), Integer.parseInt(selectedOpening[moveCounter].charAt(1) + "")).canMove(Integer.parseInt(selectedOpening[moveCounter].charAt(2) + ""), Integer.parseInt(selectedOpening[moveCounter].charAt(3) + "")) > 0){
                            board.Active_Piece = board.getPiece(Integer.parseInt(selectedOpening[moveCounter].charAt(0) + ""), Integer.parseInt(selectedOpening[moveCounter].charAt(1) + ""));
                            board.movePiece(board.getPiece(Integer.parseInt(selectedOpening[moveCounter].charAt(0) + ""), Integer.parseInt(selectedOpening[moveCounter].charAt(1) + "")), 
                                    Integer.parseInt(selectedOpening[moveCounter].charAt(2) + ""), Integer.parseInt(selectedOpening[moveCounter].charAt(3) + ""), true);

                    }
                    else if(!canMate()){
                        
                        if (!canTake()){
                            if(board.whiteKing.getMoveCounter() == 0){
                                if(!canCastle()){
                                    if(!canDevelop()){
                                        randomMove();
                                    }
                                }
                            }
                            else if(!canDevelop()){
                                randomMove();
                            }

                        }
                    }
        }
        //get out of check
        else{
            clearCheck();
        }
        
        moveCounter++;
    }
    
    //performs a 'random' move, stepping through all possible moves until one sticks
    private void randomMove() throws IOException{
        this.mateScan = true;
        for(Piece piece : board.White_Pieces){
            for(int x=0; x<8; x++){
                for(int y=0; y<8; y++){
                    if(piece.canMove(x, y) > 0){
                        board.Active_Piece = board.getPiece(piece.getX(), piece.getY());
                        board.movePiece(piece, x, y, true);
                        if(!board.whiteKing.checkScan(board.whiteKing.getX(), board.whiteKing.getY())){
                            board.revertMove();
                        }
                        else{
                            this.mateScan = false;
                            return;
                        }
                    }
                }
            }
        }
        this.mateScan = false;
        
    }
    
    //tries every possible move, if can mate - do so and end game
    //else if can check the black kind without being attacked, do so
    private boolean canMate() throws IOException{
        this.mateScan = true;
        for(Piece piece : board.White_Pieces){
            if(!piece.getNotationName().equals("K")){
                for(int x=0; x<8; x++){
                    for(int y=0; y<8; y++){
                        Piece foundPiece = board.getPiece(x, y);
                        if(foundPiece == null || (foundPiece.isWhite() != piece.isWhite())){
                            if(!(piece.getX() == x && piece.getY() == y)){
                                if(piece.canMove(x, y) > 0 && !bannedMoves.contains("" + piece.getX() + piece.getY() + x + y)){
                                    board.Active_Piece = board.getPiece(piece.getX(), piece.getY());
                                    board.movePiece(piece, x, y, true);
                                    boolean attacked = false;
                                    if(!board.blackKing.checkScan(board.blackKing.getX(), board.blackKing.getY())){
                                        for(Piece attacker : board.Black_Pieces){
                                           if (attacker.canMove(x, y) > 0){
                                                attacked = true;
                                           }
                                        }
                                        
                                        if(!attacked){
                                            this.mateScan = false;
                                            return true;
                                        }
                                        else if(!board.lastMoveReverted){
                                            board.revertMove();
                                        }
                                    }
                                    else if(!board.lastMoveReverted){
                                        board.revertMove();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        this.mateScan = false;
        return false;
    }
    
    //find a move that releases check on king
    private boolean clearCheck(){
            for(int x= 1; x> -2; x--){
              for(int y= 1; y > -2; y--){
                  if(board.whiteKing.getX() + x < 8 && board.whiteKing.getX() + x >= 0 && board.whiteKing.getY() + y < 8 && board.whiteKing.getY() + y > 0){
                      if(board.whiteKing.canMove(board.whiteKing.getX() + x, board.whiteKing.getY() + y) > 0 && !bannedMoves.contains("" + board.whiteKing.getX() + board.whiteKing.getY() + (board.whiteKing.getX()+x) + (board.whiteKing.getY()+ y))){
                          board.Active_Piece = board.whiteKing;
                          board.movePiece(board.whiteKing, board.whiteKing.getX() + x, board.whiteKing.getY() + y, true); 
                          return true;
                      }
                  }
              }
            if(!findBlock(board.whiteKing.getX(), board.whiteKing.getY())){
                  return false;  
            }
        }
            
        return false;
    }
    
    //find if king can be blocked - variables recieved pertain to king needing needing defending
    private Boolean findBlock(int kingX, int kingY){ 
        ArrayList<Piece> canMovers = new ArrayList();
        ArrayList<Point2D> attackSquares;

            for(int i=0; i < board.Black_Pieces.size(); i++){
                if(board.Black_Pieces.get(i).canMove(kingX, kingY) > 0){
                    canMovers.add(board.Black_Pieces.get(i));
                }
            }
            
            if(canMovers.size() == 1){
            attackSquares = canMovers.get(0).findAttackSquares(kingX, kingY);                      
                for(int k=0; k<board.White_Pieces.size(); k++){
                    for(int j=0; j<attackSquares.size(); j++){
                        if(board.White_Pieces.get(k).canMove((int)attackSquares.get(j).getX(), (int)attackSquares.get(j).getY()) > 0 && !bannedMoves.contains("" + board.White_Pieces.get(k).getX() + board.White_Pieces.get(k).getY() + attackSquares.get(j).getX() + attackSquares.get(j).getY())){
                            board.Active_Piece = board.White_Pieces.get(k);
                            board.movePiece(board.White_Pieces.get(k), (int)attackSquares.get(j).getX(), (int)attackSquares.get(j).getY(), true);
                            return true;
                        }
                    }
                }
            }

        return false;
    }
    
    //checks if caslting is currently available, if so then do it
    //prioritizes short castle over long
    private boolean canCastle(){
        board.castleScan = true;
        if(board.whiteKing.getMoveCounter() == 0){
            if(board.whiteKing.canMove(1, 0) > 0 && !bannedMoves.contains("" + board.whiteKing.getX() + board.whiteKing.getY() + 1 + 0)){
                board.castleMove = true;
                board.Active_Piece = board.whiteKing;
                board.movePiece(board.whiteKing, 1, 0, true);
                return true;
            }
            else if(board.whiteKing.canMove(6, 0) > 0 && !bannedMoves.contains("" + board.whiteKing.getX() + board.whiteKing.getY() + 6 + 0)){
                board.castleMove = true;
                board.Active_Piece = board.whiteKing;
                board.movePiece(board.whiteKing, 6, 0, true);
                return true;
            }
            
        }
        board.castleScan = false;
        return false;
    }
    
    //checks if knights/bishops/queen can move towards center without being attacked
    //checks if pawn can move forward without being attacked
    //checks if rook can move to a space where it attacks more ground than currently
    private boolean canDevelop(){
        
        for(Piece piece : board.White_Pieces){
            if(piece.getNotationName().equals("N") || piece.getNotationName().equals("B") || piece.getNotationName().equals("Q")){
                double distance = getDistance(piece.getX(), piece.getY());
                double bestDistance = distance;
                int destinationX=0, destinationY=0;

                for(int x=0; x<8; x++){
                    for(int y=0; y<8; y++){
                        if(piece.canMove(x, y) > 0 && getDistance(x, y) < bestDistance){
                            boolean attacked = false;

                            for(Piece attackingPiece : board.Black_Pieces){
                                if(attackingPiece.canMove(x, y) >= 0){
                                    attacked = true;
                                }
                                else if(attackingPiece.getNotationName().equals("P") && abs(y - attackingPiece.getY()) == 1 && abs(x-attackingPiece.getX()) == 1){
                                    attacked = true;
                                }
                            }

                            if(!attacked){
                                bestDistance = getDistance(x, y);
                                destinationX = x;
                                destinationY = y;   
                            }

                        }
                    }
                }
                
                if(bestDistance < distance){
                    board.Active_Piece = board.getPiece(piece.getX(), piece.getY());
                    board.movePiece(piece, destinationX, destinationY, true);
                    
                    return true;
                }
            }
            else if(piece.getNotationName().equals("P")){
                Pawn pawn = (Pawn) (piece);
                if(!pawn.getHasMoved() && piece.canMove(piece.getX(), piece.getY()+2) > 0){
                    boolean attacked = false;
                    for(Piece attacker : board.Black_Pieces){
                        if (attacker.canMove(piece.getX(), piece.getY()+2) >= 0){
                            attacked = true;
                        }
                    }
                    if(!attacked){
                        board.Active_Piece = board.getPiece(piece.getX(), piece.getY());
                        System.out.println(board.Active_Piece);
                        board.movePiece(piece, piece.getX(), piece.getY()+2, true);
                        return true;
                    }
                }
                else if(piece.canMove(piece.getX(), piece.getY()+1) > 0){
                    boolean attacked = false;
                    for(Piece attacker : board.Black_Pieces){
                        if (attacker.canMove(piece.getX(), piece.getY()+1) >= 0){
                            attacked = true;
                        }
                    }
                    if(!attacked){
                        board.Active_Piece = board.getPiece(piece.getX(), piece.getY());
                        System.out.println("X" + board.Active_Piece);
                        board.movePiece(piece, piece.getX(), piece.getY()+1, true);
                        return true;
                    }
                }
            }
            else if(piece.getNotationName().equals("R")){
                int counter = 0;
                int counter2 = 0;
                int originalAttack = 0;
                int finalX = -1, finalY = -1;
                ArrayList<Integer> xMoves = new ArrayList(), yMoves = new ArrayList();
                
                for (int x=0; x<8; x++){
                    for(int y=0; y<8; y++){
                        if(!(y == piece.getY() && x == piece.getX())){
                            if(piece.canMove(x, y) >= 0){
                                counter++;
                                if(piece.canMove(x, y) > 0){
                                    boolean attacked = false;
                                    for(Piece attacker : board.Black_Pieces){
                                        if(attacker.canMove(x, y) >=0){
                                            attacked = true;
                                        }
                                    }

                                    if(!attacked){
                                        xMoves.add(x);
                                        yMoves.add(y);
                                    }
                                }
                            }
                        }
                    }
                }
                if(piece.getX() == 7 || piece.getX() == 0){
                    counter++;
                }
                if(piece.getY() == 7 || piece.getY() == 0){
                    counter++;
                }
                
                originalAttack = counter;
                
                for(int i=0; i<xMoves.size(); i++){
                    counter2=0;
                    boolean pieceFound = false;
                    int index = 1;
                    
                    //right move
                    while(!pieceFound){
                        counter2++;
                        
                        if(board.getPiece(xMoves.get(i) + index, yMoves.get(i)) != null || xMoves.get(i) + index >= 7){
                            pieceFound = true;
                            index = 1;
                        }
                        else{
                            index ++;
                        }
                    }
                    
                    //left move
                    pieceFound = false;
                    while(!pieceFound){
                        counter2++;
                        
                        if(board.getPiece(xMoves.get(i) - index, yMoves.get(i)) != null || xMoves.get(i) - index <= 0){
                            pieceFound = true;
                            index = 1;
                        }
                        else{
                            index ++;
                        }
                    }
                    
                    //up move
                    pieceFound = false;
                    while(!pieceFound){
                        counter2++;
                        
                        if(board.getPiece(xMoves.get(i), yMoves.get(i) - index) != null || yMoves.get(i) - index <= 0){
                            pieceFound = true;
                            index = 1;
                        }
                        else{
                            index ++;
                        }
                    }
                    
                    //down move
                    pieceFound = false;
                    
                    while(!pieceFound){
                        counter2++;
                        
                        if(board.getPiece(xMoves.get(i), yMoves.get(i)+ index) != null || yMoves.get(i) + index >= 7){
                            pieceFound = true;
                            index = 1;
                        }
                        else{
                            index ++;
                        }
                    }
                    if(counter2 > counter){
                        counter = counter2;
                        finalX = xMoves.get(i);
                        finalY = yMoves.get(i);
                    }
                    
                }
                if(counter > originalAttack){
                    board.Active_Piece = board.getPiece(piece.getX(), piece.getY());
                    board.movePiece(piece, finalX, finalY, true);
                    return true;
                }
                
            }            
        }
        
        return false;
    }

    //returns the distance from center
    private double getDistance(int x, int y){
        double xDistance = abs(3.5 - x);
        double yDistance = abs(3.5 - y);
        
        return sqrt(xDistance*xDistance + yDistance*yDistance);    
    }
    
    //if can take a piece without being attacked at landing spot
    private boolean canTake(){
        int maxPoints = -1;
        int takePoints = -1;
        Piece piece = null;
        int destinationX=-1, destinationY=-1, originX = -1, originY = -1;
        
        //check if can take a piece
        for (Piece White_Piece : board.White_Pieces) {
            for(Piece Black_Piece : board.Black_Pieces){
                if(White_Piece.canMove(Black_Piece.getX(), Black_Piece.getY()) > 0 && Black_Piece.getCapturePoints() > takePoints){
                    takePoints = Black_Piece.getCapturePoints();
                    
                    for(Piece Black_Defender : board.Black_Pieces){
                        if(!Black_Defender.equals(Black_Piece)){
                            if(Black_Defender.canMove(Black_Piece.getX(), Black_Piece.getY()) >= 0){
                                takePoints -= White_Piece.getCapturePoints();
                            }
                        }
                    }
                    
                    if(takePoints > maxPoints){
                        maxPoints = takePoints;
                        piece = White_Piece;
                        destinationX = Black_Piece.getX();
                        destinationY = Black_Piece.getY();
                        originX = White_Piece.getX();
                        originY = White_Piece.getY();
                    } 
                }
            }
        }
        
        if(piece != null){
            board.Active_Piece = board.getPiece(originX, originY);
            board.movePiece(piece, destinationX, destinationY, true);
            
            return true;
        }
        return false;
    }
    
    //return if in middle of a mateScan
    public boolean getMateScan(){
        return this.mateScan;
    }
}
