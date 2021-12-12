/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessgui;

import chessgui.pieces.Piece;
import java.awt.geom.Point2D;
import static java.lang.Math.abs;
import static java.lang.Math.round;
import static java.lang.Math.sqrt;
import java.util.ArrayList;

/**
 *
 * @author tylar
 */
public class AutoPlayer {
    
    private final MainFrame mainFrame;
    private final Board board;
    private ArrayList<String[]> openings;
    private String[] selectedOpening = {"3133", "6052", "2123"};
    private int gamePhase;
    private int moveCounter;
    
    public AutoPlayer (MainFrame mainFrame, Board board){
        this.mainFrame = mainFrame;
        this.gamePhase = 0;
        this.moveCounter = 0;
        this.board = board;
    }
    
    public void getNextMove(){
        if(board.whiteKing.checkScan(board.whiteKing.getX(), board.whiteKing.getY())){    
            if(moveCounter < selectedOpening.length){
                board.Active_Piece = board.getPiece(Integer.parseInt(selectedOpening[moveCounter].charAt(0) + ""), Integer.parseInt(selectedOpening[moveCounter].charAt(1) + ""));
                board.movePiece(board.getPiece(Integer.parseInt(selectedOpening[moveCounter].charAt(0) + ""), Integer.parseInt(selectedOpening[moveCounter].charAt(1) + "")), 
                        Integer.parseInt(selectedOpening[moveCounter].charAt(2) + ""), Integer.parseInt(selectedOpening[moveCounter].charAt(3) + ""), true);
            }
            else if (!canTake()){
                if(board.whiteKing.getMoveCounter() == 0){
                    if(!canCastle()){
                        if(!canDevelop()){
                            System.out.println("can't develop");
                        }
                    }
                }
                else if(!canDevelop()){
                    System.out.println("can't develop or castle");
                }

            }
        }
        //get out of check
        else{
            clearCheck();
        }
        
        moveCounter++;
    }
    
    public boolean clearCheck(){
        if(!findBlock(board.whiteKing.getX(), board.whiteKing.getY())){
            for(int x= 1; x> -2; x--){
              for(int y= 1; y > -2; y--){
                  if(board.whiteKing.getX() + x < 8 && board.whiteKing.getX() + x >= 0 && board.whiteKing.getY() + y < 8 && board.whiteKing.getY() + y > 0){
                      if(board.whiteKing.canMove(board.whiteKing.getX() + x, board.whiteKing.getY() + y) > 0){
                          System.out.println("hello");
                          board.Active_Piece = board.whiteKing;
                          board.movePiece(board.whiteKing, board.whiteKing.getX() + x, board.whiteKing.getY() + y, true); 
                          return true;
                      }
                  }
              }
          }  
        }
            
        return false;
    }
    
    //find if king can be blocked - variables recieved pertain to king needing needing defending
    public Boolean findBlock(int kingX, int kingY){ 
        ArrayList<Piece> canMovers = new ArrayList();
        ArrayList<Point2D> attackSquares;

            for(int i=0; i < board.Black_Pieces.size(); i++){
                if(board.Black_Pieces.get(i).canMove(kingX, kingY) > 0){
                    canMovers.add(board.Black_Pieces.get(i));
                }
            }
            
            attackSquares = canMovers.get(0).findAttackSquares(kingX, kingY);                      
                for(int k=0; k<board.White_Pieces.size(); k++){
                    for(int j=0; j<attackSquares.size(); j++){
                        if(board.White_Pieces.get(k).canMove((int)attackSquares.get(j).getX(), (int)attackSquares.get(j).getY()) > 0){
                            board.Active_Piece = board.White_Pieces.get(k);
                            board.movePiece(board.White_Pieces.get(k), (int)attackSquares.get(j).getX(), (int)attackSquares.get(j).getY(), true);
                            return true;
                        }
                    }
                }

        return false;
    }
    
    public boolean canCastle(){
        
        if(board.whiteKing.getMoveCounter() == 0){
            if(board.whiteKing.canMove(1, 0) > 0){
                board.Active_Piece = board.whiteKing;
                board.movePiece(board.whiteKing, 1, 0, true);
                return true;
            }
            else if(board.whiteKing.canMove(6, 0) > 0){
                board.Active_Piece = board.whiteKing;
                board.movePiece(board.whiteKing, 6, 0, true);
                return true;
            }
            
        }
        
        return false;
    }
    
    public boolean canDevelop(){
        
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
                if(piece.canMove(piece.getX(), piece.getY()+2) > 0){
                    boolean attacked = false;
                    for(Piece attacker : board.Black_Pieces){
                        if (attacker.canMove(piece.getX(), piece.getLast_y()+2) >= 0){
                            attacked = true;
                        }
                    }
                    if(!attacked){
                        board.Active_Piece = board.getPiece(piece.getX(), piece.getY());
                        board.movePiece(piece, piece.getX(), piece.getY()+2, true);
                        return true;
                    }
                }
                else if(piece.canMove(piece.getX(), piece.getY()+1) > 0){
                    boolean attacked = false;
                    for(Piece attacker : board.Black_Pieces){
                        if (attacker.canMove(piece.getX(), piece.getLast_y()+1) >= 0){
                            attacked = true;
                        }
                    }
                    if(!attacked){
                        board.Active_Piece = board.getPiece(piece.getX(), piece.getY());
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

    public double getDistance(int x, int y){
        double xDistance = abs(3.5 - x);
        double yDistance = abs(3.5 - y);
        
        return sqrt(xDistance*xDistance + yDistance*yDistance);    
    }
    
    public boolean canTake(){
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
                            if(Black_Defender.canMove(Black_Piece.getX(), Black_Piece.getY()) > -1){
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
}
