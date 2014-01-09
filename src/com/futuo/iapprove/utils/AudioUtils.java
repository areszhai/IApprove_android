package com.futuo.iapprove.utils;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.util.Log;

import com.richitec.commontoolkit.CTApplication;

public class AudioUtils {

	private static final String LOG_TAG = AudioUtils.class.getCanonicalName();

	// application context
	private static final Context APP_CONTEXT = CTApplication.getContext();

	// audio recorder and audio player singleton instance
	private static volatile AudioRecorder _audioRecorderSingletonInstance;
	private static volatile AudioPlayer _audioPlayerSingletonInstance;

	// get audio recorder singleton instance
	private static AudioRecorder getAudioRecorderInstance() {
		if (null == _audioRecorderSingletonInstance) {
			synchronized (AudioRecorder.class) {
				if (null == _audioRecorderSingletonInstance) {
					_audioRecorderSingletonInstance = new AudioRecorder();
				}
			}
		}

		return _audioRecorderSingletonInstance;
	}

	// start record audio with ownership and return path
	public static String startRecordAudio(String userName) {
		String _audioFilePath = null;

		// check user name
		if (null != userName && !"".equalsIgnoreCase(userName)) {
			// create the file to save audio record
			File _recordAudio = new File(APP_CONTEXT.getDir(userName,
					Context.MODE_PRIVATE).getAbsolutePath(),
					System.currentTimeMillis() + ".arm");

			// start record audio
			try {
				getAudioRecorderInstance().startRecord(
						_recordAudio.getAbsolutePath());

				// update audio file path
				_audioFilePath = _recordAudio.getAbsolutePath();
			} catch (Exception e) {
				Log.e(LOG_TAG, "Start record audio form user = " + userName
						+ " error, exception message = " + e.getMessage());

				e.printStackTrace();

				// delete the file for saving record audio
				Log.d(LOG_TAG,
						"Delete the file = " + _recordAudio.getAbsolutePath()
								+ " for saving record audio, the result = "
								+ _recordAudio.delete());
			}
		} else {
			Log.e(LOG_TAG, "Start record audio for user = " + userName
					+ " error");
		}

		return _audioFilePath;
	}

	// get audio recording amplitude
	public static double getRecordingAmplitude() {
		// get amplitude
		return getAudioRecorderInstance().getAmplitude();
	}

	// stop record audio
	public static void stopRecordAudio() {
		// stop record audio
		getAudioRecorderInstance().stopRecord();
	}

	// get audio player singleton instance
	private static AudioPlayer getAudioPlayerInstance() {
		if (null == _audioPlayerSingletonInstance) {
			synchronized (AudioRecorder.class) {
				if (null == _audioPlayerSingletonInstance) {
					_audioPlayerSingletonInstance = new AudioPlayer();
				}
			}
		}

		return _audioPlayerSingletonInstance;
	}

	// play recorded audio with audio file path
	public static void playRecorderAudio(String audioFilePath) {
		// check audio file path
		if (null != audioFilePath && !"".equalsIgnoreCase(audioFilePath)) {
			// play audio
			try {
				getAudioPlayerInstance().playAudio(audioFilePath);
			} catch (Exception e) {
				Log.e(LOG_TAG, "Play recorded audio = " + audioFilePath
						+ " error, exception message = " + e.getMessage());

				e.printStackTrace();
			}
		}
	}

	// stop play recorded audio
	public static void stopPlayRecorderAudio() {
		// stop play audio
		getAudioPlayerInstance().stopPlayAudio();
	}

	// clear data
	//

	// inner class
	// audio recorder
	static class AudioRecorder {

		// media recorder
		private MediaRecorder _mMediaRecorder;

		public AudioRecorder() {
			super();

			// initialized media recorder
			_mMediaRecorder = new MediaRecorder();
		}

		// start record
		public void startRecord(String outputFilePath)
				throws IllegalStateException, IOException {
			// set audio source, output format, encoder and output file
			_mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			_mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
			_mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			_mMediaRecorder.setOutputFile(outputFilePath);

			// prepare media recorder and start record
			_mMediaRecorder.prepare();
			_mMediaRecorder.start();
		}

		// get amplitude
		public double getAmplitude() {
			double _amplitude = 0.0f;

			// check media recorder
			if (null != _mMediaRecorder) {
				_amplitude = _mMediaRecorder.getMaxAmplitude() / 2700.0;
			}

			return _amplitude;
		}

		// get amplitude EMA
		public double getAmplitudeEMA() {
			return 0.6 * getAmplitude() + (1.0 - 0.6) * 0.0;
		}

		// stop record
		public void stopRecord() {
			// check media recorder
			if (null != _mMediaRecorder) {
				// stop media recorder and reset
				_mMediaRecorder.stop();
				_mMediaRecorder.reset();
			}
		}

	}

	// audio player
	static class AudioPlayer {

		// media player
		private MediaPlayer _mMediaPlayer;

		public AudioPlayer() {
			super();

			// initialized media player
			_mMediaPlayer = new MediaPlayer();
		}

		// play audio
		public void playAudio(String audioDataSource)
				throws IllegalArgumentException, SecurityException,
				IllegalStateException, IOException {
			// reset media player
			_mMediaPlayer.reset();

			// set audio stream type and data source
			_mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			_mMediaPlayer.setDataSource(audioDataSource);

			// prepare media player and start play
			_mMediaPlayer.prepare();
			_mMediaPlayer.start();
		}

		// stop play audio
		public void stopPlayAudio() {
			// check media player and its if is playing or not
			if (null != _mMediaPlayer && _mMediaPlayer.isPlaying()) {
				// stop media player
				_mMediaPlayer.stop();
			}
		}

	}

}
