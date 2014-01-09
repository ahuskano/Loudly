import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;


public class MainWindow {

	private JFrame frmLoudlyPcServer;
	private JTextField txtfldPort;
	private JTextField txtfldInterval;
	private JTextField txtfldFileName;
	
	private JTextArea areaClients;
	private JTextArea areaLog;

	private JComboBox<String> comboChannels;
	private JComboBox<String> comboEncoding;
	private JComboBox<String> comboSamplingRate;
	
	private JButton btnPlay;
	private JButton btnBrowseFile;
	private JButton btnStop;
	
	private Server server = null;
	
	private JFileChooser fc = new JFileChooser();
	
	private MainWindow window;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					MainWindow window = new MainWindow();
					window.frmLoudlyPcServer.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		window = this;
		
		frmLoudlyPcServer = new JFrame();
		frmLoudlyPcServer.setTitle("Loudly PC Server");
		frmLoudlyPcServer.setBounds(100, 100, 611, 385);
		frmLoudlyPcServer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmLoudlyPcServer.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel windowContent = new JPanel();
		frmLoudlyPcServer.getContentPane().add(windowContent, BorderLayout.CENTER);
		windowContent.setLayout(new BoxLayout(windowContent, BoxLayout.Y_AXIS));
		
		JPanel panelConfig = new JPanel();
		windowContent.add(panelConfig);
		panelConfig.setLayout(new GridLayout(1, 3, 0, 0));
		
		JPanel panelServerConfig = new JPanel();
		panelServerConfig.setBorder(new TitledBorder(null, "Server Configuration", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelConfig.add(panelServerConfig);
		panelServerConfig.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),}));
		
		JLabel lblPort = new JLabel("Port:");
		panelServerConfig.add(lblPort, "2, 2, right, default");
		
		txtfldPort = new JTextField();
		txtfldPort.setText("4445");
		panelServerConfig.add(txtfldPort, "4, 2, fill, default");
		txtfldPort.setColumns(10);
		
		JLabel lblInterval = new JLabel("Interval:");
		panelServerConfig.add(lblInterval, "2, 4, right, default");
		
		txtfldInterval = new JTextField();
		txtfldInterval.setText("50");
		panelServerConfig.add(txtfldInterval, "4, 4, fill, default");
		txtfldInterval.setColumns(10);
		
		JButton btnStartServer = new JButton("Start server");
		btnStartServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				server = new Server(window);
				
				// uzima i postavlja parametre: port i interval, po�inje slu�ati
				server.setAUDIO_PORT(Integer.parseInt(txtfldPort.getText()));
				server.setSAMPLE_INTERVAL(Integer.parseInt(txtfldInterval.getText()));
				server.acceptClients();
				
				txtfldPort.setEditable(false);
				
				txtfldFileName.setEditable(true);
				btnBrowseFile.setEnabled(true);
			
				enablePlayButton(true);
			}
		});
		panelServerConfig.add(btnStartServer, "4, 6");
		
		JPanel panelAudioConfig = new JPanel();
		panelAudioConfig.setBorder(new TitledBorder(null, "Audio Configuration", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelConfig.add(panelAudioConfig);
		panelAudioConfig.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));
		
		JLabel lblChannels = new JLabel("Channels:");
		panelAudioConfig.add(lblChannels, "2, 2, right, default");
		
		comboChannels = new JComboBox<String>();
		comboChannels.setModel(new DefaultComboBoxModel(new String[] {"Mono", "Stereo"}));
		comboChannels.setSelectedIndex(0);
		panelAudioConfig.add(comboChannels, "4, 2, fill, default");
		
		JLabel lblEncoding = new JLabel("Encoding:");
		panelAudioConfig.add(lblEncoding, "2, 4, right, default");
		
		comboEncoding = new JComboBox<String>();
		comboEncoding.setModel(new DefaultComboBoxModel(new String[] {"8 Bit", "16 Bit", "24 Bit", "32 Bit"}));
		comboEncoding.setSelectedIndex(1);
		panelAudioConfig.add(comboEncoding, "4, 4, fill, default");
		
		JLabel lblSamplingRate = new JLabel("Sampling rate:");
		panelAudioConfig.add(lblSamplingRate, "2, 6, right, default");
		
		comboSamplingRate = new JComboBox<String>();
		comboSamplingRate.setModel(new DefaultComboBoxModel(new String[] {"1000 Hz", "8000 Hz", "11025 Hz", "16000 Hz", "22050 Hz", "24000 Hz", "32000 Hz", "41000 Hz", "48000 Hz", "96000 Hz"}));
		comboSamplingRate.setSelectedIndex(5);
		panelAudioConfig.add(comboSamplingRate, "4, 6, fill, default");
		
		JPanel panelFileConfig = new JPanel();
		panelFileConfig.setBorder(new TitledBorder(null, "Play File", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelConfig.add(panelFileConfig);
		panelFileConfig.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));
		
		txtfldFileName = new JTextField();
		txtfldFileName.setText("music/Miley_Cyrus_-_Wrecking_Ball.wav"); // defaultna pjesma
		txtfldFileName.setEditable(false);
		panelFileConfig.add(txtfldFileName, "2, 2, 10, 1, fill, default");
		txtfldFileName.setColumns(100);
		
		btnBrowseFile = new JButton("Browse");
		btnBrowseFile.setEnabled(false);
		btnBrowseFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int returnVal = fc.showOpenDialog(frmLoudlyPcServer);
				
				if (returnVal == JFileChooser.APPROVE_OPTION && txtfldFileName.isEditable()) { // ako je enableano polje zna�i da je server pokrenut
			            File file = fc.getSelectedFile();
			            txtfldFileName.setText(file.getAbsolutePath());
			            server.setAUDIO_FILE_PATH(file.getAbsolutePath());
			        } else {
			        	
			        }
			   } 
		});
		panelFileConfig.add(btnBrowseFile, "6, 4, 3, 1");
		
		btnPlay = new JButton("Play");
		btnPlay.setEnabled(false);
		btnPlay.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(server != null) {
					enablePlayButton(false);
					
					// swingworker dretva poziva doInbackground da su�elje ostane responzivno
					new SwingWorker<Void, Void>() {

						@Override
						protected Void doInBackground() throws Exception {
							// namje�ta channel
							String channels = (String) comboChannels.getSelectedItem();
							if(channels.equals("Mono")){
								server.setCHANNELS(1);
							} else if(channels.equals("Stereo")) {
								server.setCHANNELS(2);
							}
							
							// encoding
							String encoding = (String) comboEncoding.getSelectedItem();
							String splitted[] = encoding.split("\\s");
							System.out.println(splitted[0]); // makni ili log 
							server.setENCODING(Integer.parseInt(splitted[0]));
							
							//sample size = encoding/8 - splitted0 je jo� uvijek encoding
							server.setSAMPLE_SIZE(Integer.parseInt(splitted[0])/8);
							System.out.println(Integer.parseInt(splitted[0])/8); // makni
							
							// sampleRate
							String sampleRate = (String) comboSamplingRate.getSelectedItem();
							splitted = sampleRate.split("\\s");
							System.out.println(splitted[0]); // makni ili log
							server.setSAMPLE_RATE(Integer.parseInt(splitted[0]));
							
							//bitRate - ra�una se iz prethodnih postavljenih parametara 
							server.calculateBitrate();
							
							//buffer size - ra�una se...
							server.calculateBufferSize();
							
							// putanja pjesme
							if(!txtfldFileName.getText().equals("")) {
								server.setAUDIO_FILE_PATH(txtfldFileName.getText());
							}
							else {
								// defaultna pjesma
							}
							
							//prestaje prihva�ati kove klijente
							server.stopAcceptingClients();
							
							//postavlja varijablu stream u serveru na true
							server.setStream(true);
							
							// �alje pjesmu
							server.sendAudio();
							
							return null;
						}
						
					}.execute();
				}	
			}
		});
		panelFileConfig.add(btnPlay, "6, 6");
		
		btnStop = new JButton("Stop");
		btnStop.setEnabled(false);
		btnStop.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				server.setStream(false);
			}
		});
		panelFileConfig.add(btnStop, "8, 6");
		
		
		
		JPanel panelStatistics = new JPanel();
		panelStatistics.setBorder(new TitledBorder(null, "Info", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		windowContent.add(panelStatistics);
		panelStatistics.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),}));
		
		JLabel lblLog = new JLabel("Log");
		panelStatistics.add(lblLog, "2, 2, left, default");
		
		JLabel lblClients = new JLabel("Clients");
		panelStatistics.add(lblClients, "4, 2, left, default");
		
		areaLog = new JTextArea();
		areaLog.setWrapStyleWord(true);
		panelStatistics.add(areaLog, "2, 4, fill, fill");
		
		areaClients = new JTextArea();
		panelStatistics.add(areaClients, "4, 4, fill, fill");
	}
	
	public void log(final String message) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				areaLog.append(message + "\n");	
			}
		});
	}
	
	public void logClients(final String message) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				areaClients.append(message + "\n");
			}
		});
	}
	
	public void enablePlayButton(final boolean enabled) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				btnPlay.setEnabled(enabled);
				// ako je play dozvoljen, dozvoljeno je i mijenjanje intervala 
				// i obrnuto, ako nije...nije
				txtfldInterval.setEditable(enabled);
				// kad je play, nije stop i obrnuto
				btnStop.setEnabled(!enabled);
			}
		});
	}

}
