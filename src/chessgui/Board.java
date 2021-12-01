package chessgui;

import chessgui.pieces.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
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

public final class Board extends JPanel {
        
    public int turnCounter = 0, turnCounter2=-1;
    public int fiftyMovesCounter = 0;
    private ArrayList<String> moves;
    private MainFrame mainFrame;
    private ArrayList<String> gameStates;
    private static final Image NULL_IMAGE = new BufferedImage(11, 11, BufferedImage.TYPE_INT_ARGB);

    private final int Square_Width = 110;
    public ArrayList<Piece> White_Pieces;
    public ArrayList<Piece> Black_Pieces;
    
    public Boolean castleMove = false;
    public Boolean enPassantMove = false;
    
    public ArrayList<DrawingShape> Static_Shapes;
    public ArrayList<DrawingShape> Piece_Graphics;

    public Piece Active_Piece;
    public Piece lastMoved;
    public Piece lastRemoved;
    public Piece blackKing;
    public Piece whiteKing;
    public GameUI gameUI;

    private final int rows = 8;
    private final int cols = 8;
    private Integer[][] BoardGrid;
    public String boardName;
    public String piecesName;
    private String board_file_path = "images" + File.separator;
    private String active_square_file_path = "images" + File.separator + "active_square.png";
    
    private final String[] files = {"H", "G", "F", "E", "D", "C", "B", "A"};

    public void initGrid(){
        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                BoardGrid[i][j] = 0;
            }
        }

        //Image white_piece = loadImage("images/white_pieces/" + piece_name + ".png");
        //Image black_piece = loadImage("images/black_pieces/" + piece_name + ".png");  

        White_Pieces.add(new King(3,0,true,"King.png",this, 31));
        whiteKing = getPiece(3,0);
        White_Pieces.add(new Queen(4,0,true,"Queen.png",this, 8));
//        White_Pieces.add(new Bishop(2,0,true,"Bishop.png",this, 3));
//        White_Pieces.add(new Bishop(5,0,true,"Bishop.png",this, 3));
//        White_Pieces.add(new Knight(1,0,true,"Knight.png",this, 3));
//        White_Pieces.add(new Knight(6,0,true,"Knight.png",this, 3));
//        White_Pieces.add(new Rook(0,0,true,"Rook.png",this, 5));
//        White_Pieces.add(new Rook(7,0,true,"Rook.png",this, 5));
//        White_Pieces.add(new Pawn(0,1,true,"Pawn.png",this, 1));
//        White_Pieces.add(new Pawn(1,1,true,"Pawn.png",this, 1));
//        White_Pieces.add(new Pawn(2,1,true,"Pawn.png",this, 1));
//        White_Pieces.add(new Pawn(3,1,true,"Pawn.png",this, 1));
//        White_Pieces.add(new Pawn(4,1,true,"Pawn.png",this, 1));
//        White_Pieces.add(new Pawn(5,1,true,"Pawn.png",this, 1));
//        White_Pieces.add(new Pawn(6,1,true,"Pawn.png",this, 1));
//        White_Pieces.add(new Pawn(7,1,true,"Pawn.png",this, 1));

        Black_Pieces.add(new King(3,7,false,"King.png",this, 31));
        blackKing = getPiece(3, 7);
        Black_Pieces.add(new Queen(4,7,false,"Queen.png",this, 8));
//        Black_Pieces.add(new Bishop(2,7,false,"Bishop.png",this, 3));
//        Black_Pieces.add(new Bishop(5,7,false,"Bishop.png",this, 3));
//        Black_Pieces.add(new Knight(1,7,false,"Knight.png",this, 3));
//        Black_Pieces.add(new Knight(6,7,false,"Knight.png",this, 3));
//        Black_Pieces.add(new Rook(0,7,false,"Rook.png",this, 5));
//        Black_Pieces.add(new Rook(7,7,false,"Rook.png",this, 5));
//        Black_Pieces.add(new Pawn(0,6,false,"Pawn.png",this, 1));
//        Black_Pieces.add(new Pawn(1,6,false,"Pawn.png",this, 1));
//        Black_Pieces.add(new Pawn(2,6,false,"Pawn.png",this, 1));
//        Black_Pieces.add(new Pawn(3,6,false,"Pawn.png",this, 1));
//        Black_Pieces.add(new Pawn(4,6,false,"Pawn.png",this, 1));
//        Black_Pieces.add(new Pawn(5,6,false,"Pawn.png",this, 1));
//        Black_Pieces.add(new Pawn(6,6,false,"Pawn.png",this, 1));
//        Black_Pieces.add(new Pawn(7,6,false,"Pawn.png",this, 1));

    }

    public Board(GameUI gameUI, String boardName, String piecesName, MainFrame mainFrame) {
        this.gameUI = gameUI;
        this.boardName = boardName;
        this.piecesName = piecesName;
        this.mainFrame = mainFrame;
        BoardGrid = new Integer[rows][cols];
        Static_Shapes = new ArrayList();
        Piece_Graphics = new ArrayList();
        White_Pieces = new ArrayList();
        Black_Pieces = new ArrayList();
        this.moves = new ArrayList();
        this.gameStates = new ArrayList();

        initGrid();

        this.setBackground(new Color(120,13,84));
        this.setPreferredSize(new Dimension(880, 880));
        this.setMinimumSize(new Dimension(880, 880));
        this.setMaximumSize(new Dimension(880, 880));

        this.addMouseListener(mouseAdapter);
        this.addComponentListener(componentAdapter);
        this.addKeyListener(keyAdapter);
        this.setBorder(BorderFactory.createLineBorder(Color.black, 2));

        
        this.setVisible(true);
        this.requestFocus();
        drawBoard();
    }


    private void drawBoard()
    {
        Piece_Graphics.clear();
        Static_Shapes.clear();
        
        Image board = loadImage(this.board_file_path+boardName);
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
    
    //find if king can be blocked
    public Boolean canBeBlocked(int kingX, int kingY, boolean isWhite){
        Boolean canBeBlocked = false;
 
        ArrayList<Piece> canMovers = new ArrayList();
        ArrayList<Point2D> attackSquares = new ArrayList();
            
            
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
                            canBeBlocked = true;
                        }
                    }
                }
            }
            else{
                for(int k=0; k<Black_Pieces.size(); k++){
                    for(int j=0; j<attackSquares.size(); j++){
                        if(Black_Pieces.get(k).canMove((int)attackSquares.get(j).getX(), (int)attackSquares.get(j).getY()) > 0){
                           canBeBlocked = true;
                        }
                    }
                }
            }
        }
          
        return canBeBlocked;
    }
    
    private void recordGamestate(){
        String gameState = "";
        if(turnCounter2 != turnCounter){
            turnCounter2 = turnCounter;
            
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
         
    }
    
    private void recordMove(Piece lastRemoved, Piece lastMoved){
        if(lastRemoved == null){
            this.moves.add(files[lastMoved.getLast_x()] + (lastMoved.getLast_y()+1) + lastMoved.getNotationName() + "->" + files[lastMoved.getX()] + (lastMoved.getY()+1) + "  \n");
        }
        else{
            this.moves.add(files[lastMoved.getLast_x()] + (lastMoved.getLast_y()+1) + lastMoved.getNotationName() + "X" + files[lastMoved.getX()] + (lastMoved.getY()+1) + "  \n");
        }
        gameUI.updateHistory(this.moves);
        
    }
        
    public ArrayList<String> getMoves(){
        return this.moves;
    }
    
    //reverts last move and sets various variables to previous states
    public void revertMove() throws IOException{
        castleMove = false;
        enPassantMove = false;
        lastMoved.setX(lastMoved.getLast_x());
        lastMoved.setY(lastMoved.getLast_y());
            
        if(lastRemoved != null){
            if(lastRemoved.isWhite()){
                White_Pieces.add(White_Pieces.size()-1, lastRemoved);
            }
            else if(!lastRemoved.isWhite()){
                Black_Pieces.add(White_Pieces.size()-1, lastRemoved);
            }
            
            gameUI.revertCapture(!lastRemoved.isWhite());
        }
            
        lastMoved.setMoveCounter(lastMoved.getMoveCounter() - 1);
        gameUI.switchTimers();
        lastMoved = null;
        turnCounter--;
        try {
            playSound("sounds/undo.wav");
        } catch (LineUnavailableException | UnsupportedAudioFileException | IOException ex) {
            Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void addEndgame(String method){
        moves.add(method);
    }
    
    private void checkEndgames() throws IOException, FileNotFoundException, ClassNotFoundException, LineUnavailableException, UnsupportedAudioFileException{
        lackOfMaterial();
        fiftyMovesRule();
        staleMate();
        repetition();
    }

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

    private void fiftyMovesRule() throws IOException, FileNotFoundException, ClassNotFoundException, LineUnavailableException, UnsupportedAudioFileException{
        if(fiftyMovesCounter >= 100){
            addEndgame("50 Moves Rule");
            gameUI.gameOver(0, "50 Moves Rule");
        }
    }

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
    
    private MouseAdapter mouseAdapter = new MouseAdapter() {
        
        @Override
        public void mouseClicked(MouseEvent e) {

                
        }

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
            }
            
            //deselects current square 
            else if (Active_Piece != null && Active_Piece.getX() == Clicked_Column && Active_Piece.getY() == Clicked_Row)
            {
                Active_Piece = null;
            }
            
            //moves if valid move based on canmove() function
            else if (Active_Piece != null && Active_Piece.canMove(Clicked_Column, Clicked_Row)  > 0
                    && ((is_whites_turn && Active_Piece.isWhite()) || (!is_whites_turn && !Active_Piece.isWhite())))
            {
                // if piece is there, remove it so we can be there
                if (clicked_piece != null)
                {
                    lastRemoved = clicked_piece;
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
                else{
                    lastRemoved = null;
                }
                // do move
                Active_Piece.setX(Clicked_Column);
                Active_Piece.setY(Clicked_Row);
                lastMoved = Active_Piece;
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
                
                if(!whiteKing.checkScan(whiteKing.getX(), whiteKing.getY())){
                    System.out.println("white in check");
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
                else if (!blackKing.checkScan(blackKing.getX(), blackKing.getY())){
                    System.out.println("black in check");
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
            try {
                        checkEndgames();
                    } catch (IOException | ClassNotFoundException | LineUnavailableException | UnsupportedAudioFileException ex) {
                        Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
                    }
            drawBoard();
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
                return NULL_IMAGE;
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

    private ComponentAdapter componentAdapter = new ComponentAdapter() {

        @Override
        public void componentHidden(ComponentEvent e) {

        }

        @Override
        public void componentMoved(ComponentEvent e) {

        }

        @Override
        public void componentResized(ComponentEvent e) {

        }

        @Override
        public void componentShown(ComponentEvent e) {

        }	
    };

    private KeyAdapter keyAdapter = new KeyAdapter() {

        @Override
        public void keyPressed(KeyEvent e) {

        }

        @Override
        public void keyReleased(KeyEvent e) {

        }

        @Override
        public void keyTyped(KeyEvent e) {

        }	
    };

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
