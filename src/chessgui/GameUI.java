/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessgui;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.Timer;

/**
 *
 * @author tylar
 */
public class GameUI extends javax.swing.JPanel {

    private final String moves;
    private final MainFrame mainFrame;
    private final Board board;
    public ArrayList<JLabel> whiteGraphics;
    public ArrayList<JLabel> blackGraphics;
    private final FlowLayout boardLayout;
    
    private LocalDateTime blackStart, whiteStart;
    private Duration blackTimePassed, whiteTimePassed;
    private Timer blackTimer, whiteTimer;
    
    private final int increment;
    
    public GameUI(MainFrame mainFrame, int gameTime, int increment) throws IOException {
        initComponents();
        this.mainFrame = mainFrame;
        this.board = new Board(this);
        BoardPanel.add(board);
        boardLayout = new FlowLayout();
        BoardPanel.setLayout(boardLayout);
        BlackCapturePanel.setLayout(new GridLayout(4,4));
        WhiteCapturePanel.setLayout(new GridLayout(4,4));
        whiteGraphics = new ArrayList();
        blackGraphics = new ArrayList();
        this.moves = "";
        startClock(gameTime);
        this.increment = increment;
        WhiteTimeField.setText(gameTime + "m 00s");
        BlackTimeField.setText(gameTime + "m 00s");
    }
    
    public void startClock(int gameTime){
        
        Duration duration = Duration.ofMinutes(gameTime);
        
        whiteStart = LocalDateTime.now();
        whiteTimePassed = Duration.ofMinutes(0);
        
        whiteTimer = new Timer(500, new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                LocalDateTime now = LocalDateTime.now();
                Duration runningTime = Duration.between(whiteStart, now);
                Duration timeLeft = duration.minus(runningTime).minus(whiteTimePassed);
                if(timeLeft.isZero() || timeLeft.isNegative()){
                    timeLeft = Duration.ZERO;
                    try {
                        gameOver(false);
                    } catch (IOException ex) {
                        Logger.getLogger(GameUI.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(GameUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                WhiteTimeField.setText(format(timeLeft));
            }
        });
        whiteTimer.start();
        
        blackStart = LocalDateTime.now();
        blackTimePassed = Duration.ofMinutes(0);
        
        blackTimer = new Timer(500, new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                LocalDateTime now = LocalDateTime.now();
                Duration runningTime = Duration.between(blackStart, now);
                Duration timeLeft = duration.minus(runningTime).minus(blackTimePassed);
                if(timeLeft.isZero() || timeLeft.isNegative()){
                    timeLeft = Duration.ZERO;
                    try {
                        gameOver(true);
                    } catch (IOException ex) {
                        Logger.getLogger(GameUI.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(GameUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                BlackTimeField.setText(format(timeLeft));
            }
        });
        blackTimer.start();
        blackTimer.stop();
        
    }
    
    public void updateHistory(ArrayList<String> moves){
        String buffer = "";
        for(int x=0; x<moves.size(); x++){
            buffer += moves.get(x);
        }
        
        MovesHistory.setText(buffer);
    }
    
    public void switchTimers(){
        if(whiteTimer.isRunning()){
            whiteTimer.stop();           
            blackStart = LocalDateTime.now();
            whiteTimePassed = whiteTimePassed.plus(Duration.between(whiteStart, blackStart)).minus(Duration.ofSeconds(increment));
            
            blackTimer.start();
        }
        else if (blackTimer.isRunning()){
            blackTimer.stop();
            whiteStart = LocalDateTime.now();
            blackTimePassed = blackTimePassed.plus(Duration.between(blackStart, whiteStart)).minus(Duration.ofSeconds(increment));
            whiteTimer.start();
        }
    }
    
    protected String format(Duration duration) {
            long hours = duration.toHours();
            long mins = duration.minusHours(hours).toMinutes();
            long seconds = duration.minusMinutes(mins).toMillis() / 1000;
            return String.format("%02dm %02ds", mins, seconds);
    }

    public void pieceRemoved(String filePath, Boolean isWhite) throws IOException{
        if(isWhite){
            BufferedImage piece = ImageIO.read(new File("images" + File.separator + "white_pieces" + File.separator + filePath));
            JLabel label = new JLabel(new ImageIcon(piece));
            blackGraphics.add(label);
            BlackCapturePanel.add(label);
        }
        else{
            BufferedImage piece = ImageIO.read(new File("images" + File.separator + "black_pieces" + File.separator + filePath));
            JLabel label = new JLabel(new ImageIcon(piece));
            whiteGraphics.add(label);
            WhiteCapturePanel.add(label);
        }
        mainFrame.pack();
    }
    
    public void revertCapture(Boolean isWhite) throws IOException{
        if(isWhite){
            WhiteCapturePanel.remove(whiteGraphics.get(whiteGraphics.size()-1));
            whiteGraphics.remove(whiteGraphics.size()-1);
        }
        else{
            BlackCapturePanel.remove(blackGraphics.get(blackGraphics.size()-1));
            blackGraphics.remove(blackGraphics.size()-1);
        }
        mainFrame.pack();
    }
    
    public void gameOver(Boolean isWhite) throws IOException, FileNotFoundException, ClassNotFoundException{
        BoardPanel.remove(board);
        BoardPanel.repaint();
        BoardPanel.add(new GameOver(isWhite, mainFrame));
        boardLayout.setAlignment(FlowLayout.CENTER);
        mainFrame.pack();
        mainFrame.recordGame(board.getMoves());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        BoardPanel = new javax.swing.JPanel();
        SidePanel = new javax.swing.JPanel();
        ResignButton = new javax.swing.JButton();
        WhiteTimeField = new javax.swing.JTextField();
        BlackTimeField = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        MovesHistory = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        BlackCapturePanel = new javax.swing.JPanel();
        WhiteCapturePanel = new javax.swing.JPanel();

        BoardPanel.setMinimumSize(new java.awt.Dimension(800, 800));
        BoardPanel.setPreferredSize(new java.awt.Dimension(800, 800));

        javax.swing.GroupLayout BoardPanelLayout = new javax.swing.GroupLayout(BoardPanel);
        BoardPanel.setLayout(BoardPanelLayout);
        BoardPanelLayout.setHorizontalGroup(
            BoardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 800, Short.MAX_VALUE)
        );
        BoardPanelLayout.setVerticalGroup(
            BoardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 800, Short.MAX_VALUE)
        );

        ResignButton.setText("Resign");
        ResignButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ResignButtonActionPerformed(evt);
            }
        });

        WhiteTimeField.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        WhiteTimeField.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        BlackTimeField.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        BlackTimeField.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jButton1.setText("Mute");

        MovesHistory.setColumns(20);
        MovesHistory.setRows(5);
        jScrollPane1.setViewportView(MovesHistory);

        jLabel3.setText("Move History:");

        BlackCapturePanel.setPreferredSize(new java.awt.Dimension(236, 200));

        javax.swing.GroupLayout BlackCapturePanelLayout = new javax.swing.GroupLayout(BlackCapturePanel);
        BlackCapturePanel.setLayout(BlackCapturePanelLayout);
        BlackCapturePanelLayout.setHorizontalGroup(
            BlackCapturePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        BlackCapturePanelLayout.setVerticalGroup(
            BlackCapturePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );

        WhiteCapturePanel.setPreferredSize(new java.awt.Dimension(800, 55));

        javax.swing.GroupLayout WhiteCapturePanelLayout = new javax.swing.GroupLayout(WhiteCapturePanel);
        WhiteCapturePanel.setLayout(WhiteCapturePanelLayout);
        WhiteCapturePanelLayout.setHorizontalGroup(
            WhiteCapturePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        WhiteCapturePanelLayout.setVerticalGroup(
            WhiteCapturePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout SidePanelLayout = new javax.swing.GroupLayout(SidePanel);
        SidePanel.setLayout(SidePanelLayout);
        SidePanelLayout.setHorizontalGroup(
            SidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SidePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(SidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ResignButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE)
                    .addComponent(WhiteTimeField, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(BlackTimeField)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(BlackCapturePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(WhiteCapturePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE))
                .addContainerGap())
        );
        SidePanelLayout.setVerticalGroup(
            SidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, SidePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(WhiteTimeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(WhiteCapturePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(BlackCapturePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BlackTimeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ResignButton, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(BoardPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(SidePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(BoardPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 13, Short.MAX_VALUE))
                    .addComponent(SidePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void ResignButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ResignButtonActionPerformed
        if(board.turnCounter % 2 == 1){
            try {
                gameOver(true);
            } catch (IOException ex) {
                Logger.getLogger(GameUI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(GameUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else{
            try {
                gameOver(false);
            } catch (IOException ex) {
                Logger.getLogger(GameUI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(GameUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_ResignButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel BlackCapturePanel;
    private javax.swing.JTextField BlackTimeField;
    private javax.swing.JPanel BoardPanel;
    private javax.swing.JTextArea MovesHistory;
    private javax.swing.JButton ResignButton;
    private javax.swing.JPanel SidePanel;
    private javax.swing.JPanel WhiteCapturePanel;
    private javax.swing.JTextField WhiteTimeField;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
