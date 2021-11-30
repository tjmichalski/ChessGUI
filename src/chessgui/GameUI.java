/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessgui;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
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
    
    public GameUI(MainFrame mainFrame, int gameTime, int increment, String boardName, String piecesName) throws IOException {
        initComponents();
        this.mainFrame = mainFrame;
        this.board = new Board(this, boardName, piecesName);
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
        MovesHistory.setEditable(false);
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
                        gameOver(-1, "Timeout");
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
                        gameOver(1, "Timeout");
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
//            BufferedImage piece = ImageIO.read(new File("images" + File.separator + "white_pieces" + File.separator + filePath));
            Image piece = ImageIO.read(new File("images" + File.separator + "white_pieces" + File.separator + filePath));
            piece = piece.getScaledInstance(59, 59, Image.SCALE_SMOOTH);
            JLabel label = new JLabel(new ImageIcon(piece));
            blackGraphics.add(label);
            BlackCapturePanel.add(label);
        }
        else{
//            BufferedImage piece = ImageIO.read(new File("images" + File.separator + "black_pieces" + File.separator + filePath));
            Image piece = ImageIO.read(new File("images" + File.separator + "black_pieces" + File.separator + filePath));
            piece = piece.getScaledInstance(59, 59, Image.SCALE_SMOOTH);
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
    
    public void gameOver(int isWhite, String method) throws IOException, FileNotFoundException, ClassNotFoundException{
        blackTimer.stop();
        whiteTimer.stop();
        BoardPanel.remove(board);
        BoardPanel.repaint();
        BoardPanel.add(new GameOver(method, isWhite, mainFrame));
        boardLayout.setAlignment(FlowLayout.CENTER);
        mainFrame.pack();
        mainFrame.recordGame(board.getMoves());
        ResignButton.setEnabled(false);
        MuteButton.setEnabled(false);
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
        MuteButton = new javax.swing.JButton();
        BlackCapturePanel = new javax.swing.JPanel();
        WhiteCapturePanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        MovesHistory = new javax.swing.JTextArea();

        setBackground(new java.awt.Color(169, 169, 169));

        BoardPanel.setBackground(new java.awt.Color(169, 169, 169));
        BoardPanel.setMaximumSize(new java.awt.Dimension(880, 885));
        BoardPanel.setMinimumSize(new java.awt.Dimension(880, 885));
        BoardPanel.setPreferredSize(new java.awt.Dimension(880, 885));

        javax.swing.GroupLayout BoardPanelLayout = new javax.swing.GroupLayout(BoardPanel);
        BoardPanel.setLayout(BoardPanelLayout);
        BoardPanelLayout.setHorizontalGroup(
            BoardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 880, Short.MAX_VALUE)
        );
        BoardPanelLayout.setVerticalGroup(
            BoardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 885, Short.MAX_VALUE)
        );

        SidePanel.setBackground(new java.awt.Color(88, 88, 88));
        SidePanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        SidePanel.setPreferredSize(new java.awt.Dimension(256, 885));

        ResignButton.setText("Resign");
        ResignButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ResignButtonActionPerformed(evt);
            }
        });

        WhiteTimeField.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        WhiteTimeField.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        BlackTimeField.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        BlackTimeField.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        MuteButton.setText("Mute");
        MuteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MuteButtonActionPerformed(evt);
            }
        });

        BlackCapturePanel.setBackground(new java.awt.Color(238, 238, 238));
        BlackCapturePanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        BlackCapturePanel.setPreferredSize(new java.awt.Dimension(236, 200));

        javax.swing.GroupLayout BlackCapturePanelLayout = new javax.swing.GroupLayout(BlackCapturePanel);
        BlackCapturePanel.setLayout(BlackCapturePanelLayout);
        BlackCapturePanelLayout.setHorizontalGroup(
            BlackCapturePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        BlackCapturePanelLayout.setVerticalGroup(
            BlackCapturePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 234, Short.MAX_VALUE)
        );

        WhiteCapturePanel.setBackground(new java.awt.Color(238, 238, 238));
        WhiteCapturePanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        WhiteCapturePanel.setPreferredSize(new java.awt.Dimension(800, 55));

        javax.swing.GroupLayout WhiteCapturePanelLayout = new javax.swing.GroupLayout(WhiteCapturePanel);
        WhiteCapturePanel.setLayout(WhiteCapturePanelLayout);
        WhiteCapturePanelLayout.setHorizontalGroup(
            WhiteCapturePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        WhiteCapturePanelLayout.setVerticalGroup(
            WhiteCapturePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 234, Short.MAX_VALUE)
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel3.setFont(new java.awt.Font("SansSerif", 0, 18)); // NOI18N
        jLabel3.setText("Move History:");

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        MovesHistory.setColumns(20);
        MovesHistory.setRows(5);
        jScrollPane1.setViewportView(MovesHistory);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout SidePanelLayout = new javax.swing.GroupLayout(SidePanel);
        SidePanel.setLayout(SidePanelLayout);
        SidePanelLayout.setHorizontalGroup(
            SidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SidePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(SidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(MuteButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ResignButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)
                    .addComponent(WhiteTimeField, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(BlackTimeField)
                    .addComponent(BlackCapturePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)
                    .addComponent(WhiteCapturePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        SidePanelLayout.setVerticalGroup(
            SidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, SidePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(WhiteTimeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(WhiteCapturePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(BlackCapturePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BlackTimeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(MuteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(ResignButton, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
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
                    .addComponent(BoardPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(SidePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void ResignButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ResignButtonActionPerformed
        if(board.turnCounter % 2 == 1){
            try {
                gameOver(1, "Resignation");
            } catch (IOException ex) {
                Logger.getLogger(GameUI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(GameUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else{
            try {
                gameOver(-1, "Resignation");
            } catch (IOException ex) {
                Logger.getLogger(GameUI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(GameUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_ResignButtonActionPerformed

    private void MuteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MuteButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_MuteButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel BlackCapturePanel;
    private javax.swing.JTextField BlackTimeField;
    private javax.swing.JPanel BoardPanel;
    private javax.swing.JTextArea MovesHistory;
    private javax.swing.JButton MuteButton;
    private javax.swing.JButton ResignButton;
    private javax.swing.JPanel SidePanel;
    private javax.swing.JPanel WhiteCapturePanel;
    private javax.swing.JTextField WhiteTimeField;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
