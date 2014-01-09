package com.loudly.core;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

public class Client {
	public boolean connected = false;
	public boolean playing = false;

	// private static final String LOG_TAG = "Z";
	private static int AUDIO_PORT = 2048;
	private int ENCODING; // bits (resolution)
	private int CHANNELS; // 1 for MONO, 2 for STEREO
	// private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
	private int SAMPLE_RATE; // Hz
	private int BITRATE;
	private int SAMPLE_INTERVAL = 20; // milliseconds
	// private static final int SAMPLE_SIZE = 2; // bytes per sample
	private int BUF_SIZE;

	private int serverPort;
	private InetAddress serverIP;

	private DatagramSocket serverSocket;

	public Client(int _serverPort, InetAddress _serverIP, int channel, int encoding, int sample_rate, int inter) {

		// INITIALIZATION OF SERVER ON GIVEN PORT
		// Log.d("Z", "Client initialization");
		serverPort = _serverPort;
		serverIP = _serverIP;
		try {
			// CREATE SERVER SOCKET
			serverSocket = new DatagramSocket();
			AUDIO_PORT = serverSocket.getLocalPort();
			// Log.d("Z", "Socket created " + AUDIO_PORT);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		CHANNELS = channel;
		SAMPLE_RATE = sample_rate;
		SAMPLE_INTERVAL = inter;
		ENCODING = encoding;
		BITRATE = (SAMPLE_RATE * ENCODING * CHANNELS) / 1000;
		BUF_SIZE = BITRATE / 8 * SAMPLE_INTERVAL;

	}

	public boolean sendRequest() {
		// SETUP SEND PARAMETERS SUCH AS IP
		// Log.d("Z", "Send INIT");

		// IN AND OUT DATA BUFFERS
		// Log.d("Z", "SALJEM ZAHTJEV");
		byte[] rqstData = { (byte) 1 };
		DatagramPacket sendPacket = new DatagramPacket(rqstData, rqstData.length, serverIP, serverPort);
		try {
			serverSocket.send(sendPacket);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;

	}

	public boolean rcvConfig() {
		// INPUT BUFFER
		byte[] rcvData = new byte[1];
		DatagramPacket rcvPacket = new DatagramPacket(rcvData, rcvData.length);

		// Log.d("Z", "CEKAM POTVRDU");
		try {
			serverSocket.setSoTimeout(1500);
			serverSocket.receive(rcvPacket);
			return true;
		} catch (SocketTimeoutException e) {
			// Log.d("Z", "Timeout");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public void receive() {

		// CLOSE OLD SOCKET BECAUSE OF TIMEOUT AND OTHER POSSIBLE SETTINGS
		serverSocket.close();
		// Log.e("Z", "start recv thread, thread id: " +
		// Thread.currentThread().getId());
		int ch;
		if (CHANNELS == 1)
			ch = AudioFormat.CHANNEL_OUT_MONO;
		else
			ch = AudioFormat.CHANNEL_OUT_STEREO;

		int enc;
		if (ENCODING == 16)
			enc = AudioFormat.ENCODING_PCM_16BIT;
		else
			enc = AudioFormat.ENCODING_PCM_8BIT;

		String params = "BR: " + SAMPLE_RATE + "\nCH: " + ch + "\nENC: " + enc;
		Log.d("DAM", params);

		BUF_SIZE = AudioTrack.getMinBufferSize(SAMPLE_RATE, ch, enc);
		Log.d("DAM", "BufSize: " + BUF_SIZE);
		AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
				BUF_SIZE * 2, AudioTrack.MODE_STREAM);
		Log.d("DAM", "p2");
		track.play();
		Log.d("DAM", "p3");
		try {
			// CREATE SOCKET ON REGISTERED PORT
			DatagramSocket sock = new DatagramSocket(AUDIO_PORT);
			byte[] buf = new byte[BUF_SIZE];

			while (playing == true) {
				DatagramPacket pack = new DatagramPacket(buf, BUF_SIZE);
				sock.receive(pack);
				// Log.d("Z", "recv pack: " + pack.getLength());
				track.write(pack.getData(), 0, pack.getLength());
			}
			track.stop();
			sock.close();
		} catch (SocketException se) {
			// Log.e("Z", "SocketException: " + se.toString());
		} catch (IOException ie) {
			// Log.e("Z", "IOException" + ie.toString());
		}
	}
}
