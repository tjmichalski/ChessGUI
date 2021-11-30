package chessgui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;


//creates window and sets meta data for window
public class MainFrame extends JFrame {
    
    private JPanel mainPanel;
    private ArrayList<ArrayList <String>> allGamesHistory;
    
    //variables for file input and output
    private FileOutputStream fos;
    private FileInputStream fis;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    
    public String boardName;
    public String piecesName;
    
    public MainFrame()
    {
        this.setTitle("Chess");
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        mainPanel = new StartMenu(this);
        this.add(mainPanel); 
        
        this.setLocation(200, 50);
        this.pack();
        this.setVisible(true);
        this.boardName = "board.png";
        this.piecesName = "";
    }
    
    public void setNewPanel(JPanel newPanel){
        this.remove(mainPanel);
        this.mainPanel = newPanel;
        this.add(mainPanel);
        this.pack();
    }
    
    //add an employee to file
    public void recordGame(ArrayList<String> moves)throws FileNotFoundException, IOException, ClassNotFoundException {
        getAllHistory();
        allGamesHistory.add(moves);
        fos = new FileOutputStream("GamesHistory.txt");
        oos = new ObjectOutputStream(fos);
        oos.writeObject(allGamesHistory);
    }
    
    //updates mainFrame employees list from file AND returns list
    public ArrayList<ArrayList <String>> getAllHistory() throws FileNotFoundException, IOException, ClassNotFoundException{
        
        String file = "GamesHistory.txt";
        try{
            fis = new FileInputStream(file);
        }
        catch(FileNotFoundException e){
            File newFile = new File(file);
            newFile.createNewFile();
            fis = new FileInputStream(file);
        }
        try{
            ois = new ObjectInputStream(fis);
            allGamesHistory = (ArrayList<ArrayList <String>>) ois.readObject();
            ois.close();
        }
        catch(EOFException e){
            allGamesHistory = new ArrayList();
            allGamesHistory.add(new ArrayList());
        }
        
        return allGamesHistory;
    }
    
    
}
