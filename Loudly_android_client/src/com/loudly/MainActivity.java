package com.loudly;

import java.net.InetAddress;
import java.net.UnknownHostException;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.loudly.core.Client;
import com.loudly.types.Preferences;

public class MainActivity extends SherlockFragmentActivity implements OnClickListener {

	volatile MediaPlayer mediaPlayer = null;
	volatile Thread playThrd = null;
	volatile Thread[] clientThreads;

	private Button btnPlay;
	private Button btnConnect;
	private Button btnStop;

	private TextView tvPlayState;

	private boolean doubleBackToExitPressedOnce = false;

	ProgressDialog dialog;

	private Animation animation;

	private ImageView ivRotatingRing;

	private Preferences loudlyPrefs;

	private Client client;

	private void init() {
		tvPlayState = (TextView) findViewById(R.id.tvState);
		animation = AnimationUtils.loadAnimation(this, R.anim.rotation_center);
		ivRotatingRing = (ImageView) findViewById(R.id.ivCircle);
		clientThreads = new Thread[3];
		loudlyPrefs = new Preferences();
		btnPlay = (Button) findViewById(R.id.btnPlay);
		btnPlay.setVisibility(View.INVISIBLE);
		btnConnect = (Button) findViewById(R.id.btnConnect);
		btnStop = (Button) findViewById(R.id.btnStop);
		btnStop.setVisibility(View.INVISIBLE);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
		btnConnect.setOnClickListener(this);
		btnPlay.setOnClickListener(this);
		btnStop.setOnClickListener(this);
		// ivRotatingRing.startAnimation(animation);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.menu_preferences:
			startActivity(new Intent(this, PrefsActivity.class));
			return true;
		case R.id.menu_restart:
			resetActivity();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void zToast(final String text) {
		this.runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void mDialog(final boolean show) {
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (show)
					dialog = ProgressDialog.show(MainActivity.this, "", "Connecting", true);
				else
					dialog.dismiss();
			}
		});
	}

	private void btnSetEnabled(final Button btn, final boolean enabled) {
		this.runOnUiThread(new Runnable() {
			public void run() {
				btn.setEnabled(enabled);
			}
		});
	}

	private void resetActivity() {

		if (clientThreads[1] != null && clientThreads[2] != null) {
			if (clientThreads[1].isAlive())
				clientThreads[2].start();
			else if (clientThreads[2].isAlive())
				clientThreads[2].interrupt();
		}

		Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
	}

	@Override
	public void onBackPressed() {
		if (doubleBackToExitPressedOnce) {
			super.onBackPressed();
			return;
		}
		this.doubleBackToExitPressedOnce = true;
		Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				doubleBackToExitPressedOnce = false;

			}
		}, 2000);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		// / CONNECT ///
		case R.id.btnConnect:

			initThreads(0);
			loudlyPrefs.updatePreferences(this);
			btnConnect.setVisibility(View.INVISIBLE);
			btnPlay.setVisibility(View.VISIBLE);
			btnStop.setVisibility(View.VISIBLE);

			if (!clientThreads[0].isAlive() && !clientThreads[1].isAlive())
				clientThreads[0].start();
			break;

		// / PLAY ///
		case R.id.btnPlay:
			ivRotatingRing.startAnimation(animation);
			ivRotatingRing.setVisibility(View.VISIBLE);
			initThreads(1);
			clientThreads[1].start();
			break;

		// / STOP ///
		case R.id.btnStop:
			ivRotatingRing.clearAnimation();
			initThreads(2);
			clientThreads[2].start();
			break;

		}
	}

	private void updateTextViewOnUIThread(final String text) {
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				tvPlayState.setText(text);
			}
		});
	}

	private void initThreads(int id) {

		clientThreads[0] = new Thread(new Runnable() {
			public void run() {

				mDialog(true);

				InetAddress ipAddr;
				try {
					ipAddr = InetAddress.getByName(loudlyPrefs.getServerIP());
					int channel = loudlyPrefs.getChannels();

					int encoding = loudlyPrefs.getEncoding();
					int rate = loudlyPrefs.getSamplingRate();
					int inter = loudlyPrefs.getSendRate();
					client = new Client(loudlyPrefs.getServerPort(), ipAddr, channel, encoding, rate, inter);

					int brPok = 3;
					boolean rcvd = false;

					do {
						client.sendRequest();
						rcvd = client.rcvConfig();
					} while (!rcvd && brPok-- > 0);

					if (rcvd) {

						client.connected = true;
						btnSetEnabled(btnPlay, true);
						mDialog(false);
						updateTextViewOnUIThread("Connected");
						// zToast("Connected");
					} else {
						mDialog(false);
						zToast("Error connecting");
					}

				} catch (UnknownHostException e) {
					e.printStackTrace();
				}

			}
		});
		clientThreads[0].setName("connect");

		clientThreads[1] = new Thread(new Runnable() {
			public void run() {
				if (client.connected) {
					btnSetEnabled(btnStop, true);
					btnSetEnabled(btnPlay, false);
					client.playing = true;
					updateTextViewOnUIThread("Playing");
					// zToast("Playing");
					client.receive();
				}
			}
		});
		clientThreads[0].setName("play");

		clientThreads[2] = new Thread(new Runnable() {
			public void run() {
				if (client.connected && client.playing) {
					client.playing = false;
					// zToast("Stopped");
					updateTextViewOnUIThread("Paused");
					btnSetEnabled(btnPlay, true);
					btnSetEnabled(btnStop, false);
					// client.receive();
				}
			}
		});
		clientThreads[0].setName("stop");
	}
}
