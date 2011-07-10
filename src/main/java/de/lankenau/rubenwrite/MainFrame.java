package de.lankenau.rubenwrite;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import de.lankenau.rubenwrite.data.BookDAO;
import de.lankenau.rubenwrite.data.TextBlock;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.Thread;
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;

public class MainFrame extends JFrame {

    static final String BACKUP_FILE = "rubenwriter.backup";

    String text = "No TEXT";
    TextBlock block = null;
    JEditorPane editor = null;
    JButton btnCheck;
    JButton btnStart;
    JButton btnEnd;
    JTextField txtDuration = null;
    int duration = 0;
    final int MAX_DURATION = 60 * 120;
    final static String LOG_FILE = "/var/log/rubenwriter.log";

    public static void log(String msg) {
	try {
	    FileOutputStream fos = new FileOutputStream(LOG_FILE, true);
	    OutputStreamWriter writer = new OutputStreamWriter(fos);
	    writer.write(msg);
	    writer.write("\n");
	    writer.close();
	    fos.close();
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }

    public static int getLastPos() {
	int lastPos = 0;
	try {
	    FileInputStream fis = new FileInputStream(LOG_FILE);
	    InputStreamReader isr = new InputStreamReader(fis);
	    BufferedReader br = new BufferedReader(isr);
	    String line = null;
	    while ((line = br.readLine()) != null) {
		String[] parts = line.split(",");
		if (parts.length >= 3) {
		    try {
			lastPos = Integer.parseInt(parts[2].trim());
		    } 
		    catch (Exception e) { }
		}
	    }
	    
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
	return lastPos;
    }

    public void doBackup() {	
	try {
	    String text = editor.getText();
	    FileOutputStream fos = new FileOutputStream(BACKUP_FILE);
	    OutputStreamWriter writer = new OutputStreamWriter(fos);
	    writer.write(text);
	    writer.close();
	    fos.close();
	}
	catch (Exception e) {
	    e.printStackTrace();
	}	    
    }
    
    public void doBackupBackground() {
	new Thread(new Runnable() {

	    public void run() {
		doBackup();
	    }
	    
	}).start();
    }
    
    public void loadBackup() {
	if (!new File(BACKUP_FILE).exists()) return;
	try {
	    FileInputStream fis = new FileInputStream(BACKUP_FILE);
	    InputStreamReader reader = new InputStreamReader(fis);
	    char[] buffer = new char[1024];
	    int nread;
	    StringBuffer sbuffer = new StringBuffer();
	    while ((nread = reader.read(buffer, 0, buffer.length)) > 0) {
		sbuffer.append(buffer, 0, nread);
	    }
	    fis.close();
	    editor.setText(sbuffer.toString());
	}
	catch (Exception e) {
	    e.printStackTrace();
	}
    }
    
    public void deleteBackup() {
	try {
	    new File(BACKUP_FILE).delete();
	}
	catch (Exception e) {	   
	}
    }
    
    public MainFrame() {
	try {
	    int startPos = getLastPos();
	    int d = (int) ((new Date().getTime() / (1000 * 60 * 60 * 24)) - 15073L);
	    System.out.println("day: " + d);
	    block = BookDAO.getText(startPos, 200 + d);
	    text = block.text;
	} catch (Exception e) {
	    e.printStackTrace();
	    JOptionPane.showMessageDialog(this, e.toString());
	}

	Font bigFont = new Font("Serif", Font.PLAIN, 22);

	getContentPane().setLayout(new GridBagLayout());

	setTitle("Simple example");
	setSize(1300, 1000);
	setLocationRelativeTo(null);
	setDefaultCloseOperation(EXIT_ON_CLOSE);



	JTextArea textArea = new JTextArea();
	JScrollPane scrollPane = new JScrollPane(textArea);

	GridBagConstraints c = new GridBagConstraints();
	c.fill = GridBagConstraints.BOTH;
	c.weighty = 1;
	c.weightx = 1;
	c.gridx = 0;
	c.gridy = 0;

	this.getContentPane().add(scrollPane, c);
	textArea.setText(text);
	textArea.setFocusable(false);
	textArea.setFont(bigFont);


	editor = new JEditorPane();
	editor.setFont(bigFont);
	JScrollPane scrollPaneEditor = new JScrollPane(editor);

	editor.getDocument().addDocumentListener(new DocumentListener() {

	    public void changedUpdate(DocumentEvent arg0) {
		calculateTime();
		doBackupBackground();
		btnStart.setEnabled(false);
	    }

	    public void insertUpdate(DocumentEvent arg0) {
		calculateTime();
		doBackupBackground();
		btnStart.setEnabled(false);
	    }

	    public void removeUpdate(DocumentEvent arg0) {
		calculateTime();
		doBackupBackground();
		btnStart.setEnabled(false);
	    }
	});
	c = new GridBagConstraints();
	c.fill = GridBagConstraints.BOTH;
	c.weighty = 1;
	c.weightx = 1;
	c.gridx = 0;
	c.gridy = 1;

	this.getContentPane().add(scrollPaneEditor, c);


	JPanel controlPanel = new JPanel();
	controlPanel.setLayout(new FlowLayout());
	btnCheck = new JButton();
	btnCheck.setText("Pruefen");
	btnCheck.addActionListener(new ActionListener() {

	    public void actionPerformed(ActionEvent arg0) {
		checkText();
	    }
	});
	controlPanel.add(btnCheck);

	txtDuration = new JTextField();
	txtDuration.setMinimumSize(new Dimension(60, 30));
	txtDuration.setPreferredSize(new Dimension(60, 30));
	controlPanel.add(txtDuration);

	btnStart = new JButton("Text einreichen");
	controlPanel.add(btnStart);
	btnStart.addActionListener(new ActionListener() {

	    public void actionPerformed(ActionEvent arg0) {
		//startInternet();
		submit();

	    }
	});
	btnStart.setEnabled(false);


//	btnEnd = new JButton("Internet Stoppen");
//	//controlPanel.add(btnEnd);
//	btnEnd.setEnabled(false);
//	btnEnd.addActionListener(new ActionListener() {
//
//	    public void actionPerformed(ActionEvent e) {
//		stopInternet();
//
//	    }
//	});
	c = new GridBagConstraints();
	c.fill = GridBagConstraints.BOTH;
	c.weighty = 0;
	c.weightx = 1;
	c.gridx = 0;
	c.gridy = 2;
	this.getContentPane().add(controlPanel, c);

        loadBackup();

    }

    protected static int realTextLength(String text) {
	return text.replace(" ", "").replace("\t", "").replace("\n", "").replace("\r", "").length();
    }

    protected static String formatTime(int sec) {
	int hour = sec / (60 * 60);
	sec -= hour * 60 * 60;
	int min = sec / 60;
	sec -= min * 60;
	return "" + hour + ":" + min + ":" + sec;
    }

    protected void calculateTime() {
	float orgSize = realTextLength(text);
	float editSize = realTextLength(editor.getText());
	//duration = (int) (((float)MAX_DURATION) / orgSize * editSize);
	//txtDuration.setText(formatTime(duration));                
	int percentage = (int) Math.round((100.f) / orgSize * editSize);
	txtDuration.setText("" + percentage + " %");
    }

    protected void checkText() {
	int pos = Checker.check(editor.getText(), text);
	if (pos == -1) {
	    // all right
	    btnCheck.setBackground(Color.GREEN);
	    btnStart.setEnabled(true);
	} else {
	    editor.setCaretPosition(pos);
	    btnCheck.setBackground(Color.RED);
	    editor.requestFocus();
	}
    }
    boolean terminateInternet = false;

    public void submit() {
	int nwords = BookDAO.countWords(editor.getText());

	float orgSize = realTextLength(text);
	float editSize = realTextLength(editor.getText());
	int perc = (int) ((100.f) / orgSize * editSize);
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	log(sdf.format(new Date()) + ", " + perc + ", " + (block.firstWord + nwords));
	editor.setText("");
	editor.setEnabled(false);
	btnCheck.setEnabled(false);
	btnStart.setEnabled(false);
	deleteBackup();
    }

    public void startInternet() {
	editor.setEnabled(false);
	btnCheck.setEnabled(false);
	btnStart.setEnabled(false);
	btnEnd.setEnabled(true);
	Network.start();
	terminateInternet = false;
	Thread countdownThread = new Thread(new Runnable() {

	    public void run() {
		runInternet();
	    }
	});
	countdownThread.start();
    }

    public void runInternet() {
	Date startTime = new Date();
	int timeLeft = 0;
	while (!terminateInternet) {
	    long timeUsed = ((new Date()).getTime() - startTime.getTime()) / 1000L;
	    timeLeft = duration - (int) timeUsed;

	    txtDuration.setText(formatTime(timeLeft));
	    try {
		Thread.sleep(1000);
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }

	    if (timeLeft <= 0) {
		break;
	    }
	}
	stopInternet();
	duration = timeLeft;
    }

    public void stopInternet() {
	terminateInternet = true;
	btnStart.setEnabled(true);
	btnEnd.setEnabled(false);
	Network.stop();
    }

    public static void main(String[] args) {
	SwingUtilities.invokeLater(new Runnable() {

	    public void run() {
		MainFrame ex = new MainFrame();
		ex.setVisible(true);
	    }
	});
    }
}
