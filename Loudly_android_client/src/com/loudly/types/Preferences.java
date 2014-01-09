package com.loudly.types;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences {

	public static String preferenceKeys[] = { "prefServerIP", "prefServerPort", "prefChannels", "prefEncoding", "prefSamplingRate", "prefInterval" };

	private String serverIP;
	private int serverPort;
	private int samplingRate;
	private int sendRate;
	private int channels;
	private int encoding;

	public Preferences() {
		serverIP = "192.168.173.1";
		serverPort = 4445;
		samplingRate = 24000;
		sendRate = 20;
		channels = 1;
		encoding = 16;
	}

	public String getServerIP() {
		return serverIP;
	}

	public void setServerIP(String serverIP) {
		this.serverIP = serverIP;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public int getSamplingRate() {
		return samplingRate;
	}

	public void setSamplingRate(int samplingRate) {
		this.samplingRate = samplingRate;
	}

	public int getSendRate() {
		return sendRate;
	}

	public void setSendRate(int sendRate) {
		this.sendRate = sendRate;
	}

	public int getChannels() {
		return channels;
	}

	public void setChannels(int channels) {
		this.channels = channels;
	}

	public int getEncoding() {
		return encoding;
	}

	public void setEncoding(int encoding) {
		this.encoding = encoding;
	}

	public void updatePreferences(Context c) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
		String serverIP = prefs.getString(preferenceKeys[0], this.serverIP);
		if (serverIP != null)
			this.serverIP = serverIP;
		int serverPort = Integer.parseInt(prefs.getString(preferenceKeys[1], String.valueOf(this.serverPort)));
		if ((Integer) serverPort != null)
			this.serverPort = serverPort;
		int channels = Integer.parseInt(prefs.getString(preferenceKeys[2], String.valueOf(this.channels)));
		if ((Integer) channels != null)
			this.channels = channels;
		int encoding = Integer.parseInt(prefs.getString(preferenceKeys[3], String.valueOf(this.encoding)));
		if ((Integer) encoding != null)
			this.encoding = encoding;
		int samplingRate = Integer.parseInt(prefs.getString(preferenceKeys[4], String.valueOf(this.samplingRate)));
		if ((Integer) samplingRate != null)
			this.samplingRate = samplingRate;

		int sendRate = Integer.parseInt(prefs.getString(preferenceKeys[5], String.valueOf(this.sendRate)));
		if ((Integer) sendRate != null)
			this.sendRate = sendRate;

	}

}
