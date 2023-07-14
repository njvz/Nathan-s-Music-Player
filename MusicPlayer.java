import javax.swing.*; // for JFrame and components
import javax.swing.event.ChangeListener; // for volume slider
import javax.swing.event.ChangeEvent; // for volume slider
import javax.swing.filechooser.*; // for FileFilter, FileNameExtensionFilter
import java.awt.*; // for Layout, Color 
import java.awt.event.*; // for ActionListener
import javax.sound.sampled.*; // for Clip, Audiostream
import java.io.*; // for Files
import java.util.*; // for ArrayList, Random, Scanner
import java.util.Timer; // for timer
import static javax.swing.ScrollPaneConstants.*; // for scroll bar


public class MusicPlayer extends JFrame implements ActionListener{

	private JLabel muteLabel;
	private JLabel soundLabel; 
	private JLabel snailLabel;
	private JLabel currentSongLabel;  
	private JLabel currentTimeLabel;
	private JLabel totalTimeLabel;

	private JPanel buttonPanel; 
	private JPanel currentSongPanel;

	private JButton chooseFileButton;
	private JButton shuffleButton; 
	private JButton previousButton;
	private JButton playButton;
	private JButton nextButton;
	private JButton loopButton;

	private ImageIcon shuffleIcon;
	private ImageIcon previousIcon; 
	private ImageIcon playIcon; 
	private ImageIcon pauseIcon;  
	private ImageIcon nextIcon;
	private ImageIcon loopIcon;   
	private ImageIcon unloopIcon;
 
	private JSlider volumeSlider;
	private FloatControl volumeControl;
	private JTextArea playlistArea;
   	private JProgressBar progressBar;
   
	private JFileChooser fileChooser; 
	private ArrayList<File> songFileList; 
	private Clip clip;   

	private boolean isPaused; 
	private boolean isLooping; 

	private int songFilePos; 
	private int numSongs;
	private float volume;

   
	public MusicPlayer() { 

		super("Nathan's Music Player"); 
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);

		// instantiate buttons
		chooseFileButton = new JButton("Add Song"); 
		shuffleButton = new JButton();
		previousButton = new JButton(); 
		playButton = new JButton(); 
		nextButton = new JButton(); 
		loopButton = new JButton(); 

		// add listeners to buttons
		chooseFileButton.addActionListener(this);
		shuffleButton.addActionListener(this);
		previousButton.addActionListener(this);
		playButton.addActionListener(this);
		nextButton.addActionListener(this);
		loopButton.addActionListener(this);

		// display choose file button
		chooseFileButton.setBounds(380, 10, 100, 30);
		add(chooseFileButton);

		// instantiate button icons
		shuffleIcon = new ImageIcon("shuffle.png");
		previousIcon = new ImageIcon("previous.png");
		playIcon = new ImageIcon("play.png");
		pauseIcon = new ImageIcon("pause.png");  
		nextIcon = new ImageIcon("next.png"); 
		loopIcon = new ImageIcon("loop.png");  
		unloopIcon = new ImageIcon("unloop.png");   

		// set button icons
		shuffleButton.setIcon(shuffleIcon);
		previousButton.setIcon(previousIcon); 
		playButton.setIcon(playIcon); 
		nextButton.setIcon(nextIcon);  
		loopButton.setIcon(loopIcon); 

		// add audio buttons to button panel
		buttonPanel = new JPanel();
		buttonPanel.setBounds(0,420, 500, 50);
		buttonPanel.setBackground(new Color(102,178,255));
	
		buttonPanel.add(shuffleButton);
		buttonPanel.add(previousButton); 
		buttonPanel.add(playButton); 
		buttonPanel.add(nextButton); 
		buttonPanel.add(loopButton); 

		// display button panel
		add(buttonPanel);


		// display mute, sound, and snail icons
		muteLabel = new JLabel(new ImageIcon("mute.png"));
		muteLabel.setBounds(22,17, 15, 15);
		add(muteLabel);
			
		soundLabel = new JLabel(new ImageIcon("sound.png"));
		soundLabel.setBounds(145,17, 15, 15);
		add(soundLabel);

		snailLabel = new JLabel(new ImageIcon("snail.gif"));
		snailLabel.setBounds(220,10, 60, 60);
		add(snailLabel);

		// display volume slider
		volumeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0); 
		volumeSlider.setBounds(30, 10, 120, 30);
		volumeSlider.setValue(70);
		add(volumeSlider);
		addVolumeControl(); 

		// display playlist area
		playlistArea = new JTextArea(10, 40);
		playlistArea.setEditable(false);
		playlistArea.setFont(new Font("", Font.PLAIN, 15));
		playlistArea.append("Playlist:\n");
		JScrollPane scrollPane = new JScrollPane(playlistArea);
		scrollPane.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(50, 70, 400, 304);
		add(scrollPane);

		// display current song panel
		currentSongPanel = new JPanel(); 
		currentSongPanel.setBounds(50, 380, 400, 25);
		currentSongPanel.setBackground(new Color(102,178,255));
		currentSongPanel.setLayout(new FlowLayout());
		currentSongLabel = new JLabel("");
		currentSongPanel.add(currentSongLabel);
		add(currentSongPanel);
		
	    // display progress bar, current time, and total time labels
		progressBar = new JProgressBar(); 
		progressBar.setBounds(60, 385, 380, 50);
		add(progressBar);
	
		currentTimeLabel = new JLabel();	
		currentTimeLabel.setBounds(20, 400, 50, 20);
		currentTimeLabel.setText("0:00");
		add(currentTimeLabel);

		totalTimeLabel = new JLabel();	
		totalTimeLabel.setBounds(452, 400, 50, 20);
		totalTimeLabel.setText("0:00");
		add(totalTimeLabel);


		// instantiate song list
		songFileList = new ArrayList<File>();
		// set up file chooser
		fileChooser = new JFileChooser(".");
		fileChooser.setFileFilter(new FileNameExtensionFilter("WAV Files", "wav"));
		fileChooser.setApproveButtonText("Choose");

	 
		// for customization
		getContentPane().setBackground(new Color(102,178,255));
		setSize(500, 500);
		setLocationRelativeTo(null);
		setResizable(false);
		setVisible(true);
	}
	

	// button actions
	public void actionPerformed (ActionEvent event) { 
		  
		if (event.getSource() == chooseFileButton) { 
			chooseFile();
		}
		
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

	// button functionality 
	
	private void chooseFile() { 
		
		int result = fileChooser.showOpenDialog(this);
		// choose files or exit out of fileChooser
		if (result == JFileChooser.APPROVE_OPTION) 
		{        
			songFileList.add(fileChooser.getSelectedFile());  
			numSongs++;    
			displaySongInPlaylist();  

		}
			
	}

		
	// play/pause current song
	private void togglePlay() { 
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


	private void playNewSong() { 
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
			// set volume to value on silder
			setVolume();

			// makes sure to reset pause, loop, checkers after song is played
			isPaused = false;
			playButton.setIcon(pauseIcon); 

			isLooping = false;  
			loopButton.setIcon(loopIcon);

			// display current song name, progress bar
			displayCurrentSong();
			displayProgress();
	}


	// play previous song
	private void previous() { 
		clip.stop(); 
		
		if (songFilePos != 0) {
			songFilePos--; 
		}       
		playNewSong();          
	}   


	// play next song
	private void next() { 
		clip.stop();
		
		songFilePos++;   
		if (songFilePos == numSongs) {
			songFilePos = 0;
		}
		playNewSong();
		
	} 


	// loop current song, change button text when loop is toggled
	private void toggleLoop() {  
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
	private void shuffle() { 
		clip.stop();
		Random rand = new Random();
		songFilePos = rand.nextInt(numSongs);
		playNewSong();
	}
	

	private File getCurrentFile() { 
		return songFileList.get(songFilePos);
	}


	// makes sure next song is played when the current song ends
	private void checkIfSongEnded() {
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
	private void startTimer() { 
		Timer timer = new Timer();
		TimerTask task = new TimerTask() { 
			public void run() { 
				checkIfSongEnded();
			}
		};
		timer.scheduleAtFixedRate(task, 0, 1000);
	} 


	// display songs in playlist 
	private void displaySongInPlaylist() {
		playlistArea.append(numSongs + ". " + songFileList.get(numSongs-1).getName().substring(0,songFileList.get(numSongs-1).getName().length()-4 ) + "\n");
		
	}


	// display current song 
	private void displayCurrentSong() { 
		// create JLabel with current song name
		currentSongLabel.setText(getCurrentFile().getName().substring(0,getCurrentFile().getName().length()-4 )); // remove .wav from name	

	}


	// change progress bar and current time values
	private void displayProgress() { 

		progressBar.setMaximum((int) clip.getMicrosecondLength());
		progressBar.setValue((int) clip.getMicrosecondPosition());				
		
		// display total time of song in 0:00 format
		totalTimeLabel.setText(String.format("%d:%02d", clip.getMicrosecondLength()/1000000/60, clip.getMicrosecondLength()/1000000%60));
		
		

		// fill the bar as the song plays (every second)
		Timer timer = new Timer(); 
		TimerTask task = new TimerTask() { 
			public void run() { 
				progressBar.setValue((int) clip.getMicrosecondPosition());
				
				// display current time of song in 0:00 format
				currentTimeLabel.setText(String.format("%d:%02d", clip.getMicrosecondPosition()/1000000/60, clip.getMicrosecondPosition()/1000000%60));
			}
		}; 
		timer.scheduleAtFixedRate(task, 0, 1000); 
	}   


	// set volume to value on slider
	public void setVolume(){ 
		volume = (float) volumeSlider.getValue() / 100; // gets value from slider
		volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN); // gets volume control from clip
		volumeControl.setValue(30f * (float) Math.log10(volume)); // sets volume to value on slider
	}


	// add volume functionality to slider
	public void addVolumeControl(){
		
		volumeSlider.addChangeListener(new ChangeListener() { 
		public void stateChanged(ChangeEvent event) { 
			volumeSlider = (JSlider) event.getSource();

			if (!volumeSlider.getValueIsAdjusting()) { 
				volume = (float) volumeSlider.getValue() / 100; 
				volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN); 
				volumeControl.setValue(30f * (float) Math.log10(volume));
			}
		}

		});
	}

    
	public static void main(String[] args) {
        new MusicPlayer();
    }
  	
}




