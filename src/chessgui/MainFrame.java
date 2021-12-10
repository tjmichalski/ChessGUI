package chessgui;

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

//main visual element
public class MainFrame extends JFrame {
    
    //panel currently displayed
    private JPanel mainPanel;
    //nested list of all games history
    private ArrayList<ArrayList <String>> allGamesHistory;
    
    //variables for file input and output
    private FileOutputStream fos;
    private FileInputStream fis;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    
    //public variables for various game settings
    public String boardName;
    public String piecesName;
    public Boolean isMute;
    
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
        this.isMute = false;
    }
    
    public void setNewPanel(JPanel newPanel){
        this.remove(mainPanel);
        this.mainPanel = newPanel;
        this.add(mainPanel);
        this.pack();
    }
    
    //add a finished game to gamesHistory and save file
    public void recordGame(ArrayList<String> moves)throws FileNotFoundException, IOException, ClassNotFoundException {
        getAllHistory();
        allGamesHistory.add(moves);
        fos = new FileOutputStream("GamesHistory.txt");
        oos = new ObjectOutputStream(fos);
        oos.writeObject(allGamesHistory);
    }

    //read gameHistory fiile
    public ArrayList<ArrayList <String>> getAllHistory() throws FileNotFoundException, IOException, ClassNotFoundException{
        
        String file = "GamesHistory.txt";
        try{
            fis = new FileInputStream(file);
        }
        //create file is doesn't exist
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
        //if file empty, start new list
        catch(EOFException e){
            allGamesHistory = new ArrayList();
            allGamesHistory.add(new ArrayList());
        }
        
        return allGamesHistory;
    }   
}
