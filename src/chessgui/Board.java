package chessgui;

import chessgui.pieces.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.*;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;

//what is this?
@SuppressWarnings("serial")

//main powerhouse object for gameplay operations
public final class Board extends JPanel {

    //used for many graphic maniuplations
    private final MainFrame mainFrame;
 
    //various graphics related variables
    private final int Square_Width = 110;
    private final ArrayList<DrawingShape> Static_Shapes;
    private final ArrayList<DrawingShape> Piece_Graphics;
    private final String[] files = {"H", "G", "F", "E", "D", "C", "B", "A"};
    
    public String boardName;
    public String piecesName;

    
    //lists of pieces for each player
    public ArrayList<Piece> White_Pieces;
    public ArrayList<Piece> Black_Pieces;

    
    //various functional variables for game rules and activity
    private AutoPlayer autoPlayer;
    private boolean computerPlayer;
    public Piece Active_Piece;
    public Piece lastRemoved;
    private Piece blackKing;
    private Piece whiteKing;
    private GameUIPanel gameUI;
    
    public Piece lastMoved;
    public Boolean castleMove = false;
    public Boolean enPassantMove = false;
    public int turnCounter = 0;
    private int fiftyMovesCounter = 0;
    
    //each gamestate held to check for repetition rule
    private final ArrayList<String> gameStates;
    
    //each move saved for game history file writing
    private final ArrayList<String> moves;

    
    public Board(GameUIPanel gameUI, String boardName, String piecesName, MainFrame mainFrame, boolean computerPlayer) {
        //init bunch of variables
        this.gameUI = gameUI;
        this.boardName = boardName;
        this.piecesName = piecesName;
        this.mainFrame = mainFrame;
        this.computerPlayer = computerPlayer;
        this.Static_Shapes = new ArrayList();
        this.Piece_Graphics = new ArrayList();
        this.White_Pieces = new ArrayList();
        this.Black_Pieces = new ArrayList();
        this.moves = new ArrayList();
        this.gameStates = new ArrayList();

        //various graphic settings initiated
        this.setPreferredSize(new Dimension(880, 880));
        this.setMinimumSize(new Dimension(880, 880));
        this.setMaximumSize(new Dimension(880, 880));
        this.setBorder(BorderFactory.createLineBorder(Color.black, 2));
        this.setVisible(true);
        this.requestFocus();
        
        //mouse listener for user inputs
        this.addMouseListener(mouseAdapter);
        
        initPieces();       
        drawBoard();
        if(computerPlayer){
            this.autoPlayer = new AutoPlayer(mainFrame, this);
            autoPlayer.getNextMove();
        }
    }
    
    //create and add fresh game of pieces to pieces lists
    private void initPieces(){

        White_Pieces.add(new King(3,0,true,"King.png",this, 31));
        whiteKing = getPiece(3,0);
        White_Pieces.add(new Queen(4,0,true,"Queen.png",this, 8));
        White_Pieces.add(new Bishop(2,0,true,"Bishop.png",this, 3));
        White_Pieces.add(new Bishop(5,0,true,"Bishop.png",this, 3));
        White_Pieces.add(new Knight(1,0,true,"Knight.png",this, 3));
        White_Pieces.add(new Knight(6,0,true,"Knight.png",this, 3));
        White_Pieces.add(new Rook(0,0,true,"Rook.png",this, 5));
        White_Pieces.add(new Rook(7,0,true,"Rook.png",this, 5));
        White_Pieces.add(new Pawn(0,1,true,"Pawn.png",this, 1));
        White_Pieces.add(new Pawn(1,1,true,"Pawn.png",this, 1));
        White_Pieces.add(new Pawn(2,1,true,"Pawn.png",this, 1));
        White_Pieces.add(new Pawn(3,1,true,"Pawn.png",this, 1));
        White_Pieces.add(new Pawn(4,1,true,"Pawn.png",this, 1));
        White_Pieces.add(new Pawn(5,1,true,"Pawn.png",this, 1));
        White_Pieces.add(new Pawn(6,1,true,"Pawn.png",this, 1));
        White_Pieces.add(new Pawn(7,1,true,"Pawn.png",this, 1));

        Black_Pieces.add(new King(3,7,false,"King.png",this, 31));
        blackKing = getPiece(3, 7);
        Black_Pieces.add(new Queen(4,7,false,"Queen.png",this, 8));
        Black_Pieces.add(new Bishop(2,7,false,"Bishop.png",this, 3));
        Black_Pieces.add(new Bishop(5,7,false,"Bishop.png",this, 3));
        Black_Pieces.add(new Knight(1,7,false,"Knight.png",this, 3));
        Black_Pieces.add(new Knight(6,7,false,"Knight.png",this, 3));
        Black_Pieces.add(new Rook(0,7,false,"Rook.png",this, 5));
        Black_Pieces.add(new Rook(7,7,false,"Rook.png",this, 5));
        Black_Pieces.add(new Pawn(0,6,false,"Pawn.png",this, 1));
        Black_Pieces.add(new Pawn(1,6,false,"Pawn.png",this, 1));
        Black_Pieces.add(new Pawn(2,6,false,"Pawn.png",this, 1));
        Black_Pieces.add(new Pawn(3,6,false,"Pawn.png",this, 1));
        Black_Pieces.add(new Pawn(4,6,false,"Pawn.png",this, 1));
        Black_Pieces.add(new Pawn(5,6,false,"Pawn.png",this, 1));
        Black_Pieces.add(new Pawn(6,6,false,"Pawn.png",this, 1));
        Black_Pieces.add(new Pawn(7,6,false,"Pawn.png",this, 1));
    }

    //add board and each piece graphics to board and graphics lists
    private void drawBoard()
    {
        Piece_Graphics.clear();
        Static_Shapes.clear();
        
        Image board = loadImage("images/"+boardName);
        Static_Shapes.add(new DrawingImage(board, new Rectangle2D.Double(0, 0, 880, 880)));
        
        //recolor square currently clicked
        if (Active_Piece != null)
        {
            Image active_square = loadImage("images" + File.separator + "active_square.png");
            Static_Shapes.add(new DrawingImage(active_square, new Rectangle2D.Double(Square_Width*Active_Piece.getX(),Square_Width*Active_Piece.getY(), 110, 110)));
        }
        
        //drawings all of each sides pieces on each iteration
        for (int i = 0; i < White_Pieces.size(); i++)
        {
            int COL = White_Pieces.get(i).getX();
            int ROW = White_Pieces.get(i).getY();
            Image piece = loadImage("images" + File.separator + "white_pieces" + piecesName + File.separator + White_Pieces.get(i).getFilePath()); 
            piece = piece.getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            Piece_Graphics.add(new DrawingImage(piece, new Rectangle2D.Double(Square_Width*COL,Square_Width*ROW, piece.getWidth(null), piece.getHeight(null))));
        }
        for (int i = 0; i < Black_Pieces.size(); i++)
        {
            int COL = Black_Pieces.get(i).getX();
            int ROW = Black_Pieces.get(i).getY();
            Image piece = loadImage("images" + File.separator + "black_pieces" + piecesName + File.separator + Black_Pieces.get(i).getFilePath()); 
            piece = piece.getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            Piece_Graphics.add(new DrawingImage(piece, new Rectangle2D.Double(Square_Width*COL,Square_Width*ROW, piece.getWidth(null), piece.getHeight(null))));
        }
        
        this.repaint();
    }

    
    //checks if any piece, black or white, currently sits on the coordinates passed to it - x & y
    public Piece getPiece(int x, int y) {
        for (Piece p : White_Pieces)
        {
            if (p.getX() == x && p.getY() == y)
            {
                return p;
            }
        }
        for (Piece p : Black_Pieces)
        {
            if (p.getX() == x && p.getY() == y)
            {
                return p;
            }
        }
        return null;
    }
    
    //find if king can be blocked - variables recieved pertain to king needing needing defending
    public Boolean canBeBlocked(int kingX, int kingY, boolean isWhite){ 
        ArrayList<Piece> canMovers = new ArrayList();
        ArrayList<Point2D> attackSquares;
            
            
        //find every opposing piece that can currently attack king
        if(!isWhite){
            for(int i=0; i < White_Pieces.size(); i++){
                if(White_Pieces.get(i).canMove(kingX, kingY) > 0){
                    canMovers.add(White_Pieces.get(i));
                }
            }
        }
        else{
            for(int i=0; i < Black_Pieces.size(); i++){
                if(Black_Pieces.get(i).canMove(kingX, kingY) > 0){
                    canMovers.add(Black_Pieces.get(i));
                }
            }
        }
            
            
        //more than one current attacker means blocking isn't viable 
        if(canMovers.size() == 1){
            attackSquares = canMovers.get(0).findAttackSquares(kingX, kingY);                      
            if(isWhite){
                for(int k=0; k<White_Pieces.size(); k++){
                    for(int j=0; j<attackSquares.size(); j++){
                        if(White_Pieces.get(k).canMove((int)attackSquares.get(j).getX(), (int)attackSquares.get(j).getY()) > 0){
                            return true;
                        }
                    }
                }
            }
            else{
                for(int k=0; k<Black_Pieces.size(); k++){
                    for(int j=0; j<attackSquares.size(); j++){
                        if(Black_Pieces.get(k).canMove((int)attackSquares.get(j).getX(), (int)attackSquares.get(j).getY()) > 0){
                           return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    //record color, type, and position of each piece in play
    private void recordGamestate(){
        String gameState = "";
            
            for(int x=0; x<Black_Pieces.size(); x++){
                gameState += Black_Pieces.get(x).isWhite();
                gameState += Black_Pieces.get(x).getFilePath();
                gameState += Black_Pieces.get(x).getX();
                gameState += Black_Pieces.get(x).getY();
            }
            for(int x=0; x<White_Pieces.size(); x++){
                gameState += White_Pieces.get(x).isWhite();
                gameState += White_Pieces.get(x).getFilePath();
                gameState += White_Pieces.get(x).getX();
                gameState += White_Pieces.get(x).getY();
            }
            gameStates.add(gameState);         
    }
    
    //record original position, move type (capture or just move), and new position of each move - then display on gamehistory panel
    private void recordMove(Piece lastRemoved, Piece lastMoved){
        if(lastRemoved == null){
            this.moves.add(files[lastMoved.getLast_x()] + (lastMoved.getLast_y()+1) + lastMoved.getNotationName() + "->" + files[lastMoved.getX()] + (lastMoved.getY()+1) + "  \n");
        }
        else{
            this.moves.add(files[lastMoved.getLast_x()] + (lastMoved.getLast_y()+1) + lastMoved.getNotationName() + "X" + files[lastMoved.getX()] + (lastMoved.getY()+1) + "  \n");
        }
        gameUI.updateHistory(this.moves);
        
    }
    
    //reverts last move and sets various variables to previous states to prevent errors
    public void revertMove() throws IOException{
        //needed incase reverting castle or passant move
        castleMove = false;
        enPassantMove = false;
        
        //move piece back to position
        lastMoved.setX(lastMoved.getLast_x());
        lastMoved.setY(lastMoved.getLast_y());
            
        //if a piece was removed, readd it
        if(lastRemoved != null){
            if(lastRemoved.isWhite()){
                White_Pieces.add(White_Pieces.size()-1, lastRemoved);
            }
            else if(!lastRemoved.isWhite()){
                Black_Pieces.add(White_Pieces.size()-1, lastRemoved);
            }
            
            gameUI.revertCapture(!lastRemoved.isWhite());
        }
            
        //back up reverted piece's move counter
        lastMoved.setMoveCounter(lastMoved.getMoveCounter() - 1);
        
        //revert clock change
        gameUI.switchTimers();
        
        //reset lastmoved and decrement game move counter
        lastMoved = null;
        turnCounter--;
        
        //play sound
        try {
            playSound("sounds/undo.wav");
        } catch (LineUnavailableException | UnsupportedAudioFileException | IOException ex) {
            Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //if game over, note win method in game history
    public void addEndgame(String method){
        moves.add(method);
    }
    
    //check each possible endgame on moves
    private void checkEndgames() throws IOException, FileNotFoundException, ClassNotFoundException, LineUnavailableException, UnsupportedAudioFileException{
        lackOfMaterial();
        fiftyMovesRule();
        staleMate();
        repetition();
    }

    //same game state achieved 3 times in one game == draw
    private void repetition()throws IOException, FileNotFoundException, ClassNotFoundException, LineUnavailableException, UnsupportedAudioFileException{
        gameStates.sort(String::compareToIgnoreCase);
        for(int x=1; x<gameStates.size()-1; x++){
            if(gameStates.get(x).equals(gameStates.get(x-1))){
               if(gameStates.get(x).equals(gameStates.get(x+1))){
                   addEndgame("Repetition");
                   gameUI.gameOver(0, "Repetition");
               } 
            }
        }
    }

    //no moveable pieces without putting self in check == draw
    private void staleMate() throws IOException, FileNotFoundException, ClassNotFoundException, LineUnavailableException, UnsupportedAudioFileException{
        if(!blackKing.checkMateScan() && turnCounter%2 == 1){
            for(int n=1; n<Black_Pieces.size(); n++){
                for(int x=0; x<8; x++){
                    for(int y=0; y<8; y++){
                        if(Black_Pieces.get(n).canMove(x, y) > 0){
                            return;
                        }
                    }
                }
            }

            try {
                playSound("gameover/take.wav");
            } catch (LineUnavailableException | UnsupportedAudioFileException | IOException ex) {
                Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
            }
            addEndgame("Stalemate");
            gameUI.gameOver(0, "Stalemate");
        }
        else if(!whiteKing.checkMateScan() && turnCounter%2==0){
            for(int n=1; n<White_Pieces.size(); n++){
                for(int x=0; x<8; x++){
                    for(int y=0; y<8; y++){
                        if(White_Pieces.get(n).canMove(x, y) > 0){
                            return;
                        }
                    }
                }
            }
            try {
                playSound("gameover/take.wav");
            } catch (LineUnavailableException | UnsupportedAudioFileException | IOException ex) {
                Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
            }
            addEndgame("Stalemate");
            gameUI.gameOver(0, "Stalemate");
        }
    }

    //each player makes 50 continuous moves with no capture OR pawn moves == draw
    private void fiftyMovesRule() throws IOException, FileNotFoundException, ClassNotFoundException, LineUnavailableException, UnsupportedAudioFileException{
        if(fiftyMovesCounter >= 100){
            addEndgame("50 Moves Rule");
            gameUI.gameOver(0, "50 Moves Rule");
        }
    }

    //pieces remaining constitute a non-checkmateable gamestate == draw
    private void lackOfMaterial() throws IOException, FileNotFoundException, ClassNotFoundException, LineUnavailableException, UnsupportedAudioFileException{
        String[] noMaterialTypes = {"K", "KB", "KN"};

        String pieces = "";
        for(int x=0; x<White_Pieces.size(); x++){
            pieces += White_Pieces.get(x).getNotationName();
        }

        if(noMaterialTypes[0].equals(pieces) || noMaterialTypes[1].equals(pieces) || noMaterialTypes[2].equals(pieces)){
            pieces = "";
            for(int x=0; x<Black_Pieces.size(); x++){
                pieces += Black_Pieces.get(x).getNotationName();
            }
            if(noMaterialTypes[0].equals(pieces) || noMaterialTypes[1].equals(pieces) || noMaterialTypes[2].equals(pieces)){
                addEndgame("Lack Of Material");
                gameUI.gameOver(0, "Lack Of Material");
            }
        }
    }
    
    //move indicated piece to indicated coordinates
    public void movePiece(Piece piece, int x, int y, boolean is_whites_turn){
        
        if(Active_Piece != null && Active_Piece.canMove(x, y)  > 0
            && ((is_whites_turn && Active_Piece.isWhite()) || (!is_whites_turn && !Active_Piece.isWhite()))){
            Piece clickedPiece = getPiece(x, y);
            
            if(clickedPiece != null){
                lastRemoved = clickedPiece;
                checkRemoval(clickedPiece, is_whites_turn);
            }
            else{
                lastRemoved = null;
            }

            piece.setX(x);
            piece.setY(y);
            lastMoved = piece;
            gameUI.switchTimers();

            // if piece is a pawn set has_moved to true
            if (Active_Piece.getClass().equals(Pawn.class))
            {
                Pawn castedPawn = (Pawn)(Active_Piece);
                castedPawn.setHasMoved(true);
                fiftyMovesCounter = 0;
            }
            else if(lastRemoved != null){
                fiftyMovesCounter = 0;
            }
            else{
                fiftyMovesCounter++;
            }

            Active_Piece = null;
            lastMoved.setMoveCounter(lastMoved.getMoveCounter() + 1);
            turnCounter++;

            //move rook if just castled
            //castleMove will be false if last move reverted
            if(castleMove){
                castleMove = false;
                //right castle
                if(lastMoved.getX() - lastMoved.getLast_x() == 2){
                    Piece castlePiece = getPiece(7, lastMoved.getY());
                    castlePiece.setX(lastMoved.getX()-1);
                    castlePiece.setY(lastMoved.getY());
                }
                //castle left
                else{
                    Piece castlePiece = getPiece(0, lastMoved.getY());
                    castlePiece.setX(lastMoved.getX()+1);
                    castlePiece.setY(lastMoved.getY());
                }
            }

            //enPassantMove variable set to true in pawn attack function
            if(enPassantMove){
                enPassantMove = false;

                //remove piece behind whatever color is taking
                if(lastMoved.isWhite()){
                    lastRemoved = getPiece(lastMoved.getX(), lastMoved.getY()-1);
                    Black_Pieces.remove(lastRemoved);
                    try {
                        gameUI.pieceRemoved(lastRemoved.getFilePath(), false);
                    } catch (IOException ex) {
                        Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                else{
                    lastRemoved = getPiece(lastMoved.getX(), lastMoved.getY()+1);
                    White_Pieces.remove(lastRemoved);
                    try {
                        gameUI.pieceRemoved(lastRemoved.getFilePath(), true);
                    } catch (IOException ex) {
                        Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            //if whiteking in check
            if(!whiteKing.checkScan(whiteKing.getX(), whiteKing.getY())){
                //if put self in check, revert move, illegal
                if(lastMoved.isWhite()){
                    try {
                        revertMove();
                    } catch (IOException ex) {
                        Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                else {

                    recordGamestate();
                    recordMove(lastRemoved, lastMoved);

                    //if white king in checkmate, gameover
                    if(!whiteKing.checkMateScan()){
                        try {
                            addEndgame("Black Checkmate");
                            gameUI.gameOver(-1, "Checkmate");
                        } catch (IOException | ClassNotFoundException | LineUnavailableException | UnsupportedAudioFileException ex) {
                            Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    else{
                        try {
                            playSound("sounds/check.wav");
                        } catch (LineUnavailableException | UnsupportedAudioFileException | IOException ex) {
                            Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                }
            }
            //if black king in check
            else if (!blackKing.checkScan(blackKing.getX(), blackKing.getY())){
                //if put self in check, revert move, illegal
                if(!lastMoved.isWhite()){
                    try {
                        revertMove();
                    } catch (IOException ex) {
                        Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                else{

                    recordGamestate();
                    recordMove(lastRemoved, lastMoved);

                    //if black king in checkmate, gameover
                    if(!blackKing.checkMateScan()){
                        try {
                            addEndgame("White Checkmate");
                            gameUI.gameOver(1, "Checkmate");
                        } catch (IOException | ClassNotFoundException | LineUnavailableException | UnsupportedAudioFileException ex) {
                            Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    else{
                        try {
                            playSound("sounds/check.wav");
                        } catch (LineUnavailableException | UnsupportedAudioFileException | IOException ex) {
                            Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
            else{
                try {
                    playSound("sounds/move.wav");
                } catch (LineUnavailableException | UnsupportedAudioFileException | IOException ex) {
                    Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
                }
                recordGamestate();
                recordMove(lastRemoved, lastMoved);
            }
        }
        drawBoard();
    }
    
    //return game history
    public ArrayList<String> getMoves(){
        return this.moves;
    }
    
    
    public void checkRemoval(Piece clicked_piece, boolean is_whites_turn){
            // if piece is there, remove it so we can move there
                if (clicked_piece.isWhite())
                {
                    White_Pieces.remove(clicked_piece);
                    try {
                        gameUI.pieceRemoved(clicked_piece.getFilePath(), true);
                    } catch (IOException ex) {
                        Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                else
                {
                    Black_Pieces.remove(clicked_piece);
                    try {
                        gameUI.pieceRemoved(clicked_piece.getFilePath(), false);
                    } catch (IOException ex) {
                        Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                try {
                    playSound("sounds/take.wav");
                } catch (LineUnavailableException | UnsupportedAudioFileException | IOException ex) {
                    Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
                }
    }
    
    //
    private final MouseAdapter mouseAdapter = new MouseAdapter() {

        @Override
        public void mousePressed(MouseEvent e) {            
            //calculates which square you clicked on by aimple arithematic (Spellcheck?)
            int d_X = e.getX();
            int d_Y = e.getY();  
            int Clicked_Row = d_Y / Square_Width;
            int Clicked_Column = d_X / Square_Width;

            //boolean for who's turn it is - seems like could be better method
            boolean is_whites_turn = true;
            if (turnCounter%2 == 1)
            {
                is_whites_turn = false;
            }

            //passed clicked square to getpiece function to see if any pieces present
            //assigns to click_piece object even if null
            Piece clicked_piece = getPiece(Clicked_Column, Clicked_Row);

            //sets active piece to currently clicked piece if selection is valid 
            // based on who's turn it is
            if (Active_Piece == null && clicked_piece != null && 
                    ((is_whites_turn && clicked_piece.isWhite()) || (!is_whites_turn && !clicked_piece.isWhite())))
            {
                Active_Piece = clicked_piece;
                drawBoard();
            }

            //deselects current square 
            else if (Active_Piece != null && Active_Piece.getX() == Clicked_Column && Active_Piece.getY() == Clicked_Row)
            {
                Active_Piece = null;
                drawBoard();
            }
            else{
                movePiece(Active_Piece, Clicked_Column, Clicked_Row, is_whites_turn);
            }
            
            
            
            //check for non checkmate endgames and redraw board
            try {
                checkEndgames();
            } catch (IOException | ClassNotFoundException | LineUnavailableException | UnsupportedAudioFileException ex) {
                Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
            }
                    
            if(computerPlayer && turnCounter%2==0){
                autoPlayer.getNextMove();
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {		
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) 
        {
        }
        @Override
        public void mouseClicked(MouseEvent e) {         
        }
      
    };
    
    private void playSound(String fileName) throws LineUnavailableException, UnsupportedAudioFileException, IOException{
            if(!mainFrame.isMute){
                File file = new File(fileName);
                Clip clip = AudioSystem.getClip();

                AudioInputStream ais = AudioSystem.getAudioInputStream(file);
                clip.open(ais);
                clip.start(); 
            }
            
    }
        
      
    private Image loadImage(String imageFile) {
        try {
                return ImageIO.read(new File(imageFile));
        }
        catch (IOException e) {
                return null;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D)g;
        drawBackground(g2);
        drawShapes(g2);
    }

    private void drawBackground(Graphics2D g2) {
        g2.setColor(getBackground());
        g2.fillRect(0,  0, getWidth(), getHeight());
    }
       

    private void drawShapes(Graphics2D g2) {
        for (DrawingShape shape : Static_Shapes) {
            shape.draw(g2);
        }	
        for (DrawingShape shape : Piece_Graphics) {
            shape.draw(g2);
        }
    }
}



interface DrawingShape {
    boolean contains(Graphics2D g2, double x, double y);
    void adjustPosition(double dx, double dy);
    void draw(Graphics2D g2);
}


class DrawingImage implements DrawingShape {

    public Image image;
    public Rectangle2D rect;

    public DrawingImage(Image image, Rectangle2D rect) {
            this.image = image;
            this.rect = rect;
    }

    @Override
    public boolean contains(Graphics2D g2, double x, double y) {
            return rect.contains(x, y);
    }

    @Override
    public void adjustPosition(double dx, double dy) {
            rect.setRect(rect.getX() + dx, rect.getY() + dy, rect.getWidth(), rect.getHeight());	
    }

    @Override
    public void draw(Graphics2D g2) {
            Rectangle2D bounds = rect.getBounds2D();
            g2.drawImage(image, (int)bounds.getMinX(), (int)bounds.getMinY(), (int)bounds.getMaxX(), (int)bounds.getMaxY(),
                                            0, 0, image.getWidth(null), image.getHeight(null), null);
    }	
}
