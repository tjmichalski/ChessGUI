package chessgui;

/**
 *
 * @author Tylar Michalski
 * GUI Object is master object, holds everything including the board frame and the board itself
 */
public class GUI {
    
    public MainFrame boardframe;
    public static void main(String[] args) {
        GUI gui = new GUI();
        gui.boardframe = new MainFrame();
        gui.boardframe.setVisible(true);
        
    }
}
