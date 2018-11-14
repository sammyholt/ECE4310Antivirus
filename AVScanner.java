import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Base64;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JLabel;

public class AVScanner {

	// declare global variables
	private JFrame frmVirusScanner;
	JFileChooser chooser;
	String choosertitle = "Choose Directory to Scan";
	JFileChooser definitionsChooser;
	String definitionsChooserTitle = "Choose a Definitions File";
	String definitionsPath;
	String currentDirectory;
	String selectedDirectory;
	private JTextField textField;
	ArrayList<String> paths;
	ArrayList<String> knownVirusHashCodes;
	private JTextField textFieldDefinitions;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AVScanner window = new AVScanner();
					window.frmVirusScanner.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public AVScanner() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmVirusScanner = new JFrame();
		frmVirusScanner.setTitle("Virus Scanner");
		frmVirusScanner.setResizable(false);
		frmVirusScanner.setBounds(100, 100, 757, 584);
		frmVirusScanner.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmVirusScanner.getContentPane().setLayout(null);
		
		
		// Add choose folder button to frame
		JButton btnChooseFolder = new JButton("Choose Folder");
		btnChooseFolder.setBounds(563, 11, 167, 32);
		frmVirusScanner.getContentPane().add(btnChooseFolder);
		
		// Add text field for directory
		textField = new JTextField();
		textField.setEditable(false);
		textField.setBounds(117, 11, 436, 32);
		frmVirusScanner.getContentPane().add(textField);
		textField.setColumns(10);
		
		
		// Add textarea to show scan results
		JTextArea textArea = new JTextArea();
		textArea.setEditable(false);
		
		// Add textarea to scrollpane to make it scrollable
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setBounds(10, 135, 720, 353);
		frmVirusScanner.getContentPane().add(scrollPane);
		
		// add scan button, disabled by default
		JButton btnScan = new JButton("Scan");
		btnScan.setEnabled(false);
		btnScan.setBounds(10, 97, 720, 32);
		frmVirusScanner.getContentPane().add(btnScan);
		
		// add delete virus button, disabled by default
		JButton btnDeleteViruses = new JButton("No Viruses Detected");
		btnDeleteViruses.setEnabled(false);
		btnDeleteViruses.setBounds(10, 499, 720, 45);
		frmVirusScanner.getContentPane().add(btnDeleteViruses);
		
		// add text field for definitions file
		textFieldDefinitions = new JTextField();
		textFieldDefinitions.setEditable(false);
		textFieldDefinitions.setColumns(10);
		textFieldDefinitions.setBounds(117, 54, 436, 32);
		frmVirusScanner.getContentPane().add(textFieldDefinitions);
		
		// add label for scan directory
		JLabel lblDirectoryToScan = new JLabel("Directory to Scan:");
		lblDirectoryToScan.setBounds(10, 20, 109, 14);
		frmVirusScanner.getContentPane().add(lblDirectoryToScan);
		
		// add label for definitions path
		JLabel lblVirusDefinitions = new JLabel("Virus Definitions:");
		lblVirusDefinitions.setBounds(10, 63, 97, 14);
		frmVirusScanner.getContentPane().add(lblVirusDefinitions);
		
		// add choose file button for definitions file
		JButton btnChooseFile = new JButton("Choose File");
		btnChooseFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// executed when choose file button is clicked
				definitionsChooser = new JFileChooser();
				definitionsChooser.setCurrentDirectory(new java.io.File("."));
			    definitionsChooser.setDialogTitle(choosertitle);
			    
			    if (definitionsChooser.showOpenDialog(btnChooseFolder) == JFileChooser.APPROVE_OPTION) { 
			    	// get chosen path of definitions file
			    	definitionsPath = definitionsChooser.getSelectedFile().getAbsolutePath();
			    	// enable scan button
			    	btnScan.setEnabled(true);
			    }
			    else {
			    	// definitions file path is empty and disable scan button
			       definitionsPath = "";
			       btnScan.setEnabled(false);
			    }
			    // set content of text field to the definitions file path
			    textFieldDefinitions.setText(definitionsPath);
			}
		});
		btnChooseFile.setBounds(563, 54, 167, 32);
		frmVirusScanner.getContentPane().add(btnChooseFile);
		
		btnChooseFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// executed when choose folder button is clicked
				chooser = new JFileChooser();
				chooser.setCurrentDirectory(new java.io.File("."));
			    chooser.setDialogTitle(choosertitle);
			    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // only allow folders
			    chooser.setAcceptAllFileFilterUsed(false); // do not allow user to override and select file anyways
			    
			    if (chooser.showOpenDialog(btnChooseFolder) == JFileChooser.APPROVE_OPTION) { 
			    	// set directories to the chosen directory 
			    	currentDirectory = chooser.getCurrentDirectory().getAbsolutePath();
			    	selectedDirectory = chooser.getSelectedFile().getAbsolutePath();
			    }
			    else {
			    	// if no directory is selected, set empty and disable scan button
			        currentDirectory = "";
			        selectedDirectory = "";
			        btnScan.setEnabled(false);
			    }
			    
			    // set content of text field to scan directory
			    textField.setText(selectedDirectory);  
			}
		});
		
		btnScan.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// executed when scan button is clicked
				try {
					// read virus hash codes into list from definitions file
					knownVirusHashCodes = readVirusDefinitions(definitionsPath);
					
					paths = new ArrayList<String>();
					paths = listOfDirectoriesAndFiles(selectedDirectory); // get a list of file paths where virus is found
					
					if(paths.size() > 0) {
						textArea.setText("");
					    paths.forEach((path) -> textArea.append(path + "\n")); // add each path in paths list to textarea on a new line
					    btnDeleteViruses.setEnabled(true); // enable delete viruses button
					    btnDeleteViruses.setText("Delete " + paths.size() + " Viruses"); // set text of delete viruses button to match number of found viruses
					}
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		btnDeleteViruses.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// executed when delete virus button is clicked
				if(deleteFiles(paths)) {
					// all files successfully removed
					textArea.setText("All files deleted successfully!");
				}else {
					// all files could not be removed
					textArea.setText("Not all files could be removed, check your file permissions.");
				}		
			}
		});
		
		
	}
	
	/**
	 * 
	 * @param path - A path to the desired scan directory.
	 * @return A list of strings representing the absolute paths of files matching a virus definition in the specified directory.
	 * 
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	public ArrayList<String> listOfDirectoriesAndFiles(String path) throws NoSuchAlgorithmException, IOException {
		File folder = new File(path);
		 
		
		File[] files = folder.listFiles();
		
		for(File file : files) {
			
			if(file.isFile()) {
				if(knownVirusHashCodes.contains(getFileHash(file))) {
					System.out.println("Virus detected from hash: " + getFileHash(file) + " ; Adding path: "  + file.getAbsolutePath());
					paths.add(file.getAbsolutePath()); // add file to list
				}
			}
			else {
				listOfDirectoriesAndFiles(file.getPath()); // recursively traverse each directory
			}
		}
		
		return paths;
	}
	
	/**
	 * Generates a file hash code represented as a string.  Hash code is generated by reading data inside the file 
	 * using a BufferedInputStream.  
	 * 
	 * @param file - A file to generate a hash code from.
	 * @return The file hash code in Base64 string format using SHA-256 algorithm.
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 */
	public String getFileHash(File file) throws IOException, NoSuchAlgorithmException {
		byte[] buffer= new byte[8192]; // use a buffer size of 8kB blocks to help performance
	    int count;
	    MessageDigest digest = MessageDigest.getInstance("SHA-256"); // new digest using SHA-256 algorithm
	    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
	    while ((count = bis.read(buffer)) > 0) {
	    	// update digest in 8kb blocks while there is data
	        digest.update(buffer, 0, count); 
	    }
	    bis.close();

	    byte[] hash = digest.digest();
	    return Base64.getEncoder().encodeToString(hash); // encode hash to a string representation
	}
	
	/**
	 * Removes all files in the specified file path list.
	 * 
	 * @param paths - A list of absolute file paths to remove.
	 * @return True if all files were deleted, false if not.
	 */
	public boolean deleteFiles(ArrayList<String> paths) {
		boolean succeeded = true;
		for(String path : paths) {
			File file = new File(path);
			
			if(file.delete()) {
				// successfully deleted
			}else {
				succeeded = false;
			}
		}
		
		return succeeded;
	}
	
	/**
	 * Returns a list of strings read from a provided file.  Adds entire line of file as a list item
	 * then moves on to the next line.
	 * 
	 * @param path - Path to virus definitions file.
	 * @return ArrayList of strings representing known virus hash codes.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public ArrayList<String> readVirusDefinitions(String path) throws FileNotFoundException, IOException{
		ArrayList<String> knownVirusHashCodes = new ArrayList<String>();
		try (BufferedReader br = new BufferedReader(new FileReader(new File(path)))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		       // process the line.
		    	knownVirusHashCodes.add(line.toString());
		    }
		}
		
		return knownVirusHashCodes;
	}
}
