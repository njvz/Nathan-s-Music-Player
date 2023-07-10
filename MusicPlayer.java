import javax.swing.*; // for JFrame and components
import javax.swing.filechooser.*; // for FileFilter, FileNameExtensionFilter
import java.awt.*; // for Layout, Color 
import java.awt.event.*; // for ActionListener
import javax.sound.sampled.*; // for Clip, Audiostream
import java.io.*; // for Files
import java.util.*; // for ArrayList, Random, Scanner
import java.util.Timer; // for timer


public class MusicPlayer extends JFrame implements ActionListener{

    private JLabel prompt;
    private JTextField playlistSizeInput;
    private JLabel image1;
    private JLabel image2;  
    private JPanel buttonPanel; 

    private JButton chooseFileButton;
    private JButton previousButton;
    private JButton playButton;
    private JButton nextButton;
    private JButton loopButton;
    private JButton shuffleButton; 

    private ImageIcon playIcon; 
    private ImageIcon pauseIcon; 
    private ImageIcon shuffleIcon; 
    private ImageIcon loopIcon; 
    private ImageIcon previousIcon; 
    private ImageIcon nextIcon;  
    private ImageIcon unloopIcon;
 
    private JTextArea playlistArea;
   
    private JFileChooser fileChooser; 
    private ArrayList<File> songFileList; 
    private Clip clip;   
   
    private boolean filesChosen;
    private boolean isPaused; 
    private boolean isLooping; 

    private int songFilePos; 
    private int numSongs;

   
    public MusicPlayer() { 

        super("Nathan's Music Player"); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        // prompt and playlist display
        prompt = new JLabel("How many songs do you want in your playlist? ");
        playlistSizeInput = new JTextField(2);
        add(prompt);
        add(playlistSizeInput);

        // instantiate buttons
        chooseFileButton = new JButton("Create Playlist"); 
        previousButton = new JButton(); 
        playButton = new JButton(); 
        nextButton = new JButton(); 
        loopButton = new JButton(); 
        shuffleButton = new JButton();

        // add listeners to buttons
        chooseFileButton.addActionListener(this);
        previousButton.addActionListener(this);
        playButton.addActionListener(this);
        nextButton.addActionListener(this);
        loopButton.addActionListener(this);
        shuffleButton.addActionListener(this);

        add(chooseFileButton);

        // display image1
        image1 = new JLabel(new ImageIcon("headset.png"));
        add(image1);

        // add audio buttons to button panel
        buttonPanel = new JPanel();
        buttonPanel.add(shuffleButton);
        buttonPanel.add(previousButton); 
        buttonPanel.add(playButton); 
        buttonPanel.add(nextButton); 
        buttonPanel.add(loopButton); 
        // add button panel to JFrame
        add(buttonPanel);
        buttonPanel.setBackground(new Color(102,178,255));

        // display image2
         image2 = new JLabel(new ImageIcon("earbuds.png"));
         add(image2);

        // instantiate icons
        playIcon = new ImageIcon("play.png");
        pauseIcon = new ImageIcon("pause.png"); 
        shuffleIcon = new ImageIcon("shuffle.png"); 
        loopIcon = new ImageIcon("loop.png"); 
        previousIcon = new ImageIcon("previous.png"); 
        nextIcon = new ImageIcon("next.png"); 
        unloopIcon = new ImageIcon("unloop.png");   

        // set icons for buttons
        playButton.setIcon(playIcon); 
        shuffleButton.setIcon(shuffleIcon); 
        loopButton.setIcon(loopIcon); 
        previousButton.setIcon(previousIcon); 
        nextButton.setIcon(nextIcon); 

        // set dispplay for playlist
        playlistArea = new JTextArea(10, 40);
        playlistArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(playlistArea);
        add(scrollPane);
       

        // instantiate song list
        songFileList = new ArrayList<File>();
        // set up file chooser
        fileChooser = new JFileChooser(".");
        fileChooser.setFileFilter(new FileNameExtensionFilter("WAV Files", "wav"));
        fileChooser.setApproveButtonText("Choose");

     

        // for customization
        getContentPane().setBackground(new Color(102,178,255));

        setSize(525, 330);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }
    
    // button actions
    public void actionPerformed (ActionEvent event) { 
          
        if (event.getSource() == chooseFileButton) { 
            chooseFile();
        }
        if (filesChosen) {
            if (event.getSource() == playButton) { 
                togglePlay();
            }
            if (event.getSource() == previousButton) { 
                previous();
            }
            if (event.getSource() == nextButton) { 
                next();
            }
            if (event.getSource() == loopButton) { 
                toggleLoop();
            }  
            if (event.getSource() == shuffleButton) { 
                shuffle();
            }
        }
   
    }

    // button functionality 

    public void chooseFile() { 
        if (!filesChosen) {

            numSongs = Integer.parseInt(playlistSizeInput.getText());
    
            for (int x = 0; x < numSongs; x++){
            
                int result = fileChooser.showOpenDialog(this);
                // choose files or exit out of fileChooser
                if (result == JFileChooser.APPROVE_OPTION) 
                {        
                    songFileList.add(fileChooser.getSelectedFile());          
                }
                else { 
                    break;
                }
            }
    
            filesChosen = true;
            chooseFileButton.setText("Playlist Created");
            displayPlaylist();
        }
    }
        
    // play/pause current song
    public void togglePlay() { 
        // if first song has been played and song is not paused, pause 
        if (clip!= null && isPaused == false) {
            clip.stop();               
            isPaused = true; 
            playButton.setIcon(playIcon);;
        }
        else {
            // if first song, play new song, start timer
                if (clip == null) {  
                playNewSong();     
                startTimer();                   
            }
                // if song is paused, resume 
            else if (isPaused == true) {
                clip.start();
                isPaused = false; 
                playButton.setIcon(pauseIcon); 
            } 
            
        }
            
    }

    public void playNewSong() { 
        try {
            /* 
            * An audio input stream is an input stream with a specified audio format and length.  
            * The length is expressed in sample frames, not bytes. 
            *  
            * The Clip interface represents a special kind of data line whose audio data can be  
            * loaded prior to playback, instead of being streamed in real time.
            */
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(getCurrentFile()); // creates AudioInputStream object. Brings music from file to program
            clip = AudioSystem.getClip(); // creates Clip object that acts like a cd player 
            clip.open(audioInput);
            clip.start();     
        }
            catch(Exception e) {
                System.out.println(e);
            }

            // makes sure to reset pause, loop, checkers after song is played
            isPaused = false;
            playButton.setIcon(pauseIcon); 

            isLooping = false;  
            loopButton.setIcon(loopIcon);

            // display current song name, progress bar
            displayCurrentSong();
            displayProgressBar();

    }

    // play previous song
    public void previous() { 
        clip.stop(); 
        
        if (songFilePos != 0) {
            songFilePos--; 
        }       
        playNewSong();          
    }   

    // play next song
    public void next() { 
        clip.stop();
        
        songFilePos++;   
        if (songFilePos == numSongs) {
            songFilePos = 0;
        }
        playNewSong();
        
    } 

    // loop current song, change button text when loop is toggled
    public void toggleLoop() {  
        if (isLooping == false) {
            isLooping = true; 
            loopButton.setIcon(unloopIcon);
        }
        else {
            isLooping = false; 
            loopButton.setIcon(loopIcon);
        }

    }

    // play random song in filelist 
    public void shuffle() { 
        clip.stop();
        Random rand = new Random();
        songFilePos = rand.nextInt(numSongs);
        playNewSong();
    }
    
    public File getCurrentFile() { 
        return songFileList.get(songFilePos);
    }

    // makes sure next song is played when the current song ends
    public void checkIfSongEnded() {
        if (clip.getMicrosecondPosition() == clip.getMicrosecondLength()) {
            if (isLooping == false) {
                next();
            }
            else {
                clip.setMicrosecondPosition(0);
                clip.start();
            }
        }
    }
            
    // uses timer to check if song has ended every second
    public void startTimer() { 
        Timer timer = new Timer();
        TimerTask task = new TimerTask() { 
            public void run() { 
                checkIfSongEnded();
            }
        };
        timer.scheduleAtFixedRate(task, 0, 1000);
    } 

    // displays playlist in JFrame 
    public void displayPlaylist() {
        playlistArea.append("Selected Songs:\n");
        for (int i = 0; i < numSongs && i < songFileList.size(); i++) {
            playlistArea.append((i + 1) + ". " + songFileList.get(i).getName() + "\n");
        }
        revalidate();
        repaint();
        
    }
    // display current song in JFrame
    public void displayCurrentSong() { 
        // remove old display of current song if it exists
        Component[] components = getContentPane().getComponents();
        for (Component component : components) {
            if (component instanceof JLabel && ((JLabel) component).getIcon() == null && ((JLabel) component).getText().startsWith("Now Playing: ")) {
                remove(component);
                break;
            
                }
        }
        // create JLabel with current song name
        JLabel currentSong = new JLabel("Now Playing: " + getCurrentFile().getName() + " ");
        add(currentSong); 
        revalidate(); 
        repaint();

    }

    // display progress bar 
    public void displayProgressBar() { 
        // remove old progress bar if it exists
        Component[] components = getContentPane().getComponents();
        for (Component component : components) {
            if (component instanceof JProgressBar) {
                remove(component);
                break;
            }
        }
        // create progress bar
        JProgressBar progressBar = new JProgressBar(0, (int) clip.getMicrosecondLength());
        progressBar.setValue((int) clip.getMicrosecondPosition());
        progressBar.setBounds(0, 0, 500, 100);
        progressBar.setStringPainted(true);

        add(progressBar);
        revalidate();
        repaint();
        // fill the bar as the song plays (every second)
        Timer timer = new Timer(); 
        TimerTask task = new TimerTask() { 
            public void run() { 
                progressBar.setValue((int) clip.getMicrosecondPosition());
            }
        }; 
        timer.scheduleAtFixedRate(task, 0, 1000); 
    }   

    public static void main(String[] args) {
       new MusicPlayer();
            
    }
}



