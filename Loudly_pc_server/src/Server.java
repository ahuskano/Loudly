import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Server {
	private volatile boolean wait = true;
	private volatile boolean stream = false;

	// SERVER CONSTANTS
	private String AUDIO_FILE_PATH = "music/Miley_Cyrus_-_Wrecking_Ball.wav";
	private int AUDIO_PORT = 4445;
	private int ENCODING = 16; // bits (resolution)
	private int CHANNELS = 1; // 1 for MONO, 2 for STEREO
	private int SAMPLE_RATE = 24000; // Hz
	private int BITRATE = (SAMPLE_RATE * ENCODING * CHANNELS) / 1000;
	private int SAMPLE_INTERVAL = 40; // milliseconds
	private int SAMPLE_SIZE = 2; // bytes per sample
	private int BUF_SIZE = BITRATE / 8 * SAMPLE_INTERVAL;

	private MainWindow parent = null;

	ArrayList<Client> clients = new ArrayList<Client>();

	// argument je tipa MainWindow, da Server mo�e pozivati funkcije
	// nad su�eljem (logovi...)
	Server(MainWindow parent) {
		this.parent = parent;
		try {
			parent.log("SERVER INITIALIZED: "
					+ Inet4Address.getLocalHost().getHostAddress() + " "
					+ AUDIO_PORT);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void acceptClients() {
		Thread thrd = new Thread(new Runnable() {
			@Override
			public void run() {
				parent.log("ACCEPTING CLIENTS");
				try {
					DatagramSocket sock = new DatagramSocket(AUDIO_PORT);
					sock.setSoTimeout(1000);
					byte[] buf = new byte[1];
					wait = true;
					while (wait) {
						DatagramPacket pack = new DatagramPacket(buf,
								buf.length);

						try {
							// RECEIVE JOIN REQUEST
							sock.receive(pack);
						} catch (SocketTimeoutException e) {
							continue;
						}
						

						// Stvara novog klijenta
						Client cli = new Client(pack.getPort(), pack
								.getAddress());

						// RETURN RESPONSE 1 OR 0
						int response = 0;
						if (clients.contains(cli)) {
							response = 1;
						} else if (clients.add(cli)) { // ADD CLIENT TO ARRAYLIST OF CLIENTS
							response = 1;
							parent.log("NEW CLIENT REQUEST: "
									+ pack.getPort() + " " + pack.getAddress());

						} else {
							response = 0;
						}
						if (response == 1) {
							DatagramPacket sndPack = new DatagramPacket(buf,
									buf.length, cli.getIp(), cli.getPort());
							sock.send(sndPack);
							parent.log("Client " + cli.getIp() + " " + cli.getPort());
							parent.logClients(cli.getIp() + "  " + cli.getPort());
						}
					}
					sock.close();	
				} catch (SocketException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				parent.log("Stopped accepting clients.");
			}
		});
		thrd.start();
	}

	void sendAudio() {
		Thread thrd = new Thread(new Runnable() {
			@Override
			public void run() {
				long file_size = 0;
				int bytes_read = 0;
				int bytes_count = 0;
				File audio = new File(AUDIO_FILE_PATH);
				FileInputStream audio_stream = null;
				file_size = audio.length();
				byte[] buf = new byte[BUF_SIZE];
				try {

					DatagramSocket sock = new DatagramSocket();
					audio_stream = new FileInputStream(audio);
					parent.log("Sending song...");

					while (bytes_count < file_size && stream) {
						// READ AUDIO CHUNK
						bytes_read = audio_stream.read(buf, 0, BUF_SIZE);

						// CREATE UNIVERSAL PACKET
						DatagramPacket pack = new DatagramPacket(buf,
								bytes_read);

						// ITERATE OVER CLIENTS AND SEND PACKET
						for (int i = 0; i < clients.size(); i++) {
							pack.setAddress(clients.get(i).getIp());
							pack.setPort(clients.get(i).getPort());
							sock.send(pack);
						}
						bytes_count += bytes_read;
						// parent.log(bytes_read + "B sent.");
						Thread.sleep(SAMPLE_INTERVAL, 0);
					}
					sock.close();
					if (true == stream) {
						parent.log("Song was streamed successfully.");
					} else {
						parent.log("Streaming stoppped.");
					}

					acceptClients();
					parent.enablePlayButton(true);

				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (SocketException e) {
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		thrd.start();
	}

	public String getAUDIO_FILE_PATH() {
		return AUDIO_FILE_PATH;
	}

	public int getAUDIO_PORT() {
		return AUDIO_PORT;
	}

	public int getENCODING() {
		return ENCODING;
	}

	public int getCHANNELS() {
		return CHANNELS;
	}

	public int getSAMPLE_RATE() {
		return SAMPLE_RATE;
	}

	public int getSAMPLE_INTERVAL() {
		return SAMPLE_INTERVAL;
	}

	public int getSAMPLE_SIZE() {
		return SAMPLE_SIZE;
	}

	public void setAUDIO_FILE_PATH(String aUDIO_FILE_PATH) {
		AUDIO_FILE_PATH = aUDIO_FILE_PATH;
	}

	public void setAUDIO_PORT(int aUDIO_PORT) {
		AUDIO_PORT = aUDIO_PORT;
	}

	public void setENCODING(int eNCODING) {
		ENCODING = eNCODING;
	}

	public void setCHANNELS(int cHANNELS) {
		CHANNELS = cHANNELS;
	}

	public void setSAMPLE_RATE(int sAMPLE_RATE) {
		SAMPLE_RATE = sAMPLE_RATE;
	}

	public void setSAMPLE_INTERVAL(int sAMPLE_INTERVAL) {
		SAMPLE_INTERVAL = sAMPLE_INTERVAL;
	}

	public void setSAMPLE_SIZE(int sAMPLE_SIZE) {
		SAMPLE_SIZE = sAMPLE_SIZE;
	}

	public void calculateBitrate() {
		BITRATE = (SAMPLE_RATE * ENCODING * CHANNELS) / 1000;
	}

	public void calculateBufferSize() {
		BUF_SIZE = BITRATE / 8 * SAMPLE_INTERVAL;
	}

	public boolean isWait() {
		return wait;
	}

	public void setWait(boolean wait) {
		this.wait = wait;
	}

	public void stopAcceptingClients() {
		setWait(false);
	}

	public void setStream(boolean stream) {
		this.stream = stream;
	}

}
