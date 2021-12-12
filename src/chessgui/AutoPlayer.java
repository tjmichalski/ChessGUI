/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessgui;

import chessgui.pieces.Piece;
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
        if(moveCounter < selectedOpening.length){
            board.Active_Piece = board.getPiece(Integer.parseInt(selectedOpening[moveCounter].charAt(0) + ""), Integer.parseInt(selectedOpening[moveCounter].charAt(1) + ""));
            board.movePiece(board.getPiece(Integer.parseInt(selectedOpening[moveCounter].charAt(0) + ""), Integer.parseInt(selectedOpening[moveCounter].charAt(1) + "")), 
                    Integer.parseInt(selectedOpening[moveCounter].charAt(2) + ""), Integer.parseInt(selectedOpening[moveCounter].charAt(3) + ""), true);
        }
        else if (!canTake()){
            if(!canDevelop()){
                System.out.println("can't develop");
            }
        }
        
        moveCounter++;
    }
    
    public boolean canDevelop(){
        
        for(Piece piece : board.White_Pieces){
            if(piece.getNotationName().equals("N") || piece.getNotationName().equals("B") || piece.getNotationName().equals("P")){
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
            System.out.println(piece);
            board.movePiece(piece, destinationX, destinationY, true);
            
            return true;
        }
        return false;
    }
}
