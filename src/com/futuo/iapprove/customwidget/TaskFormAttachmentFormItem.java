package com.futuo.iapprove.customwidget;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.futuo.iapprove.R;
import com.futuo.iapprove.task.IApproveTaskAttachmentType;
import com.richitec.commontoolkit.CTApplication;

public class TaskFormAttachmentFormItem extends FrameLayout {

	private static final String LOG_TAG = TaskFormAttachmentFormItem.class
			.getCanonicalName();

	// iApprove task form attachment type
	private TaskFormAttachmentType _mAttachmentType;

	// iApprove task form attachment info object
	private Object _mAttachmentInfo;

	// iApprove task form attachment text, image attachment parent frameLayout,
	// text attachment textView, image attachment imageView and voice attachment
	// play image view container relativeLayout, play image view
	private FrameLayout _mText7ImageAttachmentParentFrameLayout;
	private TextView _mTextAttachmentTextView;
	private NetLoadImageView _mImageAttachmentImgView;
	private RelativeLayout _mVoiceAttachmentPlayImgViewContainerRelativeLayout;
	private ImageView _mVoiceAttachmentPlayImgView;

	// iApprove task form attachment form item on click listener
	private OnClickListener _mAttachmentFormItemOnClickListener;

	// iApprove task form voice attachment play image view container
	// relativeLayout on click listener
	private TaskFormVoiceAttachmentPlayImgViewContainerRelativeLayoutOnClickListener _mVoiceAttachmentPlayImgViewContainerRelativeLayoutOnClickListener;

	private TaskFormAttachmentFormItem(Context context) {
		super(context);

		// inflate iApprove task form attachment form item layout
		LayoutInflater.from(context).inflate(
				R.layout.task_formattachment_form_item_layout, this);

		// get iApprove task form attachment text, image attachment parent
		// frameLayout
		_mText7ImageAttachmentParentFrameLayout = (FrameLayout) findViewById(R.id.taskfafi_text7image_parent_frameLayout);

		// get iApprove task form text attachment textView and image attachment
		// imageView
		_mTextAttachmentTextView = (TextView) findViewById(R.id.taskfafi_textAttachment_textView);
		_mImageAttachmentImgView = (NetLoadImageView) findViewById(R.id.taskfafi_imageAttachment_imageView);

		// get iApprove task form voice attachment play image view container
		// relativeLayout
		_mVoiceAttachmentPlayImgViewContainerRelativeLayout = (RelativeLayout) findViewById(R.id.taskfafi_voice_playImgView_container_relativeLayout);

		// get iApprove task form voice attachment play image view
		_mVoiceAttachmentPlayImgView = (ImageView) findViewById(R.id.taskfafi_voiceAttachment_play_imageView);

		// set iApprove task form attachment type default value
		_mAttachmentType = TaskFormAttachmentType.TEXT_ATTACHMENT;
	}

	@Override
	public void setOnClickListener(OnClickListener l) {
		// save attachment on click listener
		_mAttachmentFormItemOnClickListener = l;

		// check iApprove task form attachment type
		switch (_mAttachmentType) {
		case TEXT_ATTACHMENT:
		case IMAGE_ATTACHMENT:
			// set text and image attachment parent frameLayout on click
			// listener
			_mText7ImageAttachmentParentFrameLayout
					.setOnClickListener(new TaskFormText7ImageAttachmentParentFrameLayoutOnClickListener());
			break;

		case VOICE_ATTACHMENT:
			// set voice attachment play image view container relativeLayout on
			// click listener
			_mVoiceAttachmentPlayImgViewContainerRelativeLayout
					.setOnClickListener(_mVoiceAttachmentPlayImgViewContainerRelativeLayoutOnClickListener = new TaskFormVoiceAttachmentPlayImgViewContainerRelativeLayoutOnClickListener());
			break;

		case APPLICATION_ATTACHMENT:
			// nothing to do
			break;
		}
	}

	private Boolean isVoicePlaying() {
		Boolean _isVoicePlaying = false;

		// get and check voice attachment play imageView tag
		Object _tag = _mVoiceAttachmentPlayImgView.getTag();
		if (null != _tag) {
			_isVoicePlaying = (Boolean) _tag;
		}

		return _isVoicePlaying;
	}

	// get text attachment text
	private String getTextAttachmentText() {
		String _textAttachmentText = null;

		// check iApprove task form attachment type
		if (TaskFormAttachmentType.TEXT_ATTACHMENT == _mAttachmentType) {
			// get text attachment text
			_textAttachmentText = null != _mAttachmentInfo ? _mAttachmentInfo
					.toString() : "";
		}

		return _textAttachmentText;
	}

	// get image attachment image bitmap
	private Bitmap getImageAttachmentImgBitmap() {
		Bitmap _imageAttachmentImgBitmap = null;

		// check iApprove task form attachment type
		if (TaskFormAttachmentType.IMAGE_ATTACHMENT == _mAttachmentType) {
			// convert attachment info object to bitmap
			try {
				_imageAttachmentImgBitmap = (Bitmap) _mAttachmentInfo;
			} catch (Exception e) {
				Log.e(LOG_TAG,
						"Get image attachment image bitmap error, exception message = "
								+ e.getMessage());

				e.printStackTrace();
			}
		}

		return _imageAttachmentImgBitmap;
	}

	// get image attachment image remote url
	private String getImageAttachmentImgRemoteUrl() {
		String _imageAttachmentImgRemoteUrl = null;

		// check iApprove task form attachment type
		if (TaskFormAttachmentType.IMAGE_ATTACHMENT == _mAttachmentType) {
			// convert attachment info object to string
			_imageAttachmentImgRemoteUrl = (String) _mAttachmentInfo;
		}

		return _imageAttachmentImgRemoteUrl;
	}

	// get voice attachment voice file path
	private String getVoiceAttachmentVoiceFilePath() {
		String _voiceAttachmentVoiceFilePath = null;

		// check iApprove task form attachment type
		if (TaskFormAttachmentType.VOICE_ATTACHMENT == _mAttachmentType) {
			// convert attachment info object to map
			try {
				@SuppressWarnings("unchecked")
				Map<String, Object> _voiceAttachmentInfo = (Map<String, Object>) _mAttachmentInfo;

				// get voice attachment voice file path
				_voiceAttachmentVoiceFilePath = (String) _voiceAttachmentInfo
						.get(TaskFormVoiceAttachmentInfoDataKeys.VOICEATTACHMENT_VOICE_FILEPATH);
			} catch (Exception e) {
				Log.e(LOG_TAG,
						"Get voice attachment voice file path error, exception message = "
								+ e.getMessage());

				e.printStackTrace();
			}
		}

		return _voiceAttachmentVoiceFilePath;
	}

	// get voice attachment voice duration
	private Integer getVoiceAttachmentVoiceDuration() {
		Integer _voiceAttachmentVoiceDuration = null;

		// check iApprove task form attachment type
		if (TaskFormAttachmentType.VOICE_ATTACHMENT == _mAttachmentType) {
			// convert attachment info object to map
			try {
				@SuppressWarnings("unchecked")
				Map<String, Object> _voiceAttachmentInfo = (Map<String, Object>) _mAttachmentInfo;

				// get voice attachment voice duration
				_voiceAttachmentVoiceDuration = (Integer) _voiceAttachmentInfo
						.get(TaskFormVoiceAttachmentInfoDataKeys.VOICEATTACHMENT_VOICE_DURATION);
			} catch (Exception e) {
				Log.e(LOG_TAG,
						"Get voice attachment voice duration error, exception message = "
								+ e.getMessage());

				e.printStackTrace();
			}
		}

		return _voiceAttachmentVoiceDuration;
	}

	// generate iApprove task form attachment form item with type and info
	public static TaskFormAttachmentFormItem generateTaskFormAttachmentFormItem(
			TaskFormAttachmentType type, Object info) {
		// get application context
		Context _appContext = CTApplication.getContext();

		// new iApprove task form attachment form item
		TaskFormAttachmentFormItem _newTaskFormAttachmentFormItem = new TaskFormAttachmentFormItem(
				_appContext);

		// check iApprove task form attachment type and set it
		if (null != type) {
			_newTaskFormAttachmentFormItem._mAttachmentType = type;
		}

		// set iApprove task form attachment info object
		_newTaskFormAttachmentFormItem._mAttachmentInfo = info;

		// check iApprove task form attachment type again
		switch (_newTaskFormAttachmentFormItem._mAttachmentType) {
		case TEXT_ATTACHMENT:
			// show text and image attachment parent frameLayout
			_newTaskFormAttachmentFormItem._mText7ImageAttachmentParentFrameLayout
					.setVisibility(View.VISIBLE);

			// show text attachment textView
			_newTaskFormAttachmentFormItem._mTextAttachmentTextView
					.setVisibility(View.VISIBLE);

			// get, check text attachment text and set its text
			String _textAttachmentText = _newTaskFormAttachmentFormItem
					.getTextAttachmentText();
			if (null != _textAttachmentText) {
				_newTaskFormAttachmentFormItem._mTextAttachmentTextView
						.setText(_textAttachmentText);
			}
			break;

		case IMAGE_ATTACHMENT:
			// show text and image attachment parent frameLayout
			_newTaskFormAttachmentFormItem._mText7ImageAttachmentParentFrameLayout
					.setVisibility(View.VISIBLE);

			// show image attachment imageView
			_newTaskFormAttachmentFormItem._mImageAttachmentImgView
					.setVisibility(View.VISIBLE);

			// get, check image attachment image bitmap and set its image bitmap
			Bitmap _imageAttachmentImgBitmap = _newTaskFormAttachmentFormItem
					.getImageAttachmentImgBitmap();
			if (null != _imageAttachmentImgBitmap) {
				_newTaskFormAttachmentFormItem._mImageAttachmentImgView
						.setImageBitmap(_imageAttachmentImgBitmap);
			} else {
				_newTaskFormAttachmentFormItem._mImageAttachmentImgView
						.loadUrl(_newTaskFormAttachmentFormItem
								.getImageAttachmentImgRemoteUrl());
			}
			break;

		case VOICE_ATTACHMENT:
			// show voice attachment parent relativeLayout
			_newTaskFormAttachmentFormItem.findViewById(
					R.id.taskfafi_voice_parent_relativeLayout).setVisibility(
					View.VISIBLE);

			// get and check voice attachment voice duration
			Integer _voiceAttachmentVoiceDuration = _newTaskFormAttachmentFormItem
					.getVoiceAttachmentVoiceDuration();
			if (null != _voiceAttachmentVoiceDuration) {
				// set voice duration textView text
				((TextView) _newTaskFormAttachmentFormItem
						.findViewById(R.id.taskfafi_voiceAttachment_duration_textView))
						.setText(String
								.format(_appContext
										.getResources()
										.getString(
												R.string.task_voiceAttachment_voiceDuration_format),
										_voiceAttachmentVoiceDuration));

				// update voice play image view container relativeLayout padding
				// right
				_newTaskFormAttachmentFormItem._mVoiceAttachmentPlayImgViewContainerRelativeLayout
						.setPadding(
								_newTaskFormAttachmentFormItem._mVoiceAttachmentPlayImgViewContainerRelativeLayout
										.getPaddingLeft(),
								_newTaskFormAttachmentFormItem._mVoiceAttachmentPlayImgViewContainerRelativeLayout
										.getPaddingTop(),
								Math.min(
										_appContext
												.getResources()
												.getDimensionPixelSize(
														R.dimen.task_formVoiceAttachment_playImgViewContainerRelativeLayout_paddingRight_min)
												+ (_voiceAttachmentVoiceDuration - 1)
												* _appContext
														.getResources()
														.getDimensionPixelSize(
																R.dimen.task_formVoiceAttachment_playImgViewContainerRelativeLayout_paddingRight_increaseStep),
										_appContext
												.getResources()
												.getDimensionPixelSize(
														R.dimen.task_formVoiceAttachment_playImgViewContainerRelativeLayout_paddingRight_max)),
								_newTaskFormAttachmentFormItem._mVoiceAttachmentPlayImgViewContainerRelativeLayout
										.getPaddingBottom());
			}
			break;

		case APPLICATION_ATTACHMENT:
			// nothing to do
			break;
		}

		return _newTaskFormAttachmentFormItem;
	}

	// inner class
	// iApprove task form attachment type
	public static enum TaskFormAttachmentType {

		// text, image, voice and application attachment
		TEXT_ATTACHMENT, IMAGE_ATTACHMENT, VOICE_ATTACHMENT, APPLICATION_ATTACHMENT;

		// get iApprove task form attachment type with iApprove task attachment
		// type
		public static TaskFormAttachmentType getType(
				IApproveTaskAttachmentType taskAttachmentType) {
			TaskFormAttachmentType _taskFormAttachmentType = null;

			// check task iApprove task attachment type
			switch (taskAttachmentType) {
			case AUDIO_AMR:
			case AUDIO_WAV:
			case AUDIO_3GPP:
				// voice attachment
				_taskFormAttachmentType = VOICE_ATTACHMENT;
				break;

			case IMAGE_JPG:
			case IMAGE_JPEG:
			case IMAGE_PNG:
			case IMAGE_BMP:
			case IMAGE_GIF:
				// image attachment
				_taskFormAttachmentType = IMAGE_ATTACHMENT;
				break;

			case COMMON_TEXT:
				// text attachment
				_taskFormAttachmentType = TEXT_ATTACHMENT;
				break;

			case COMMON_FILE:
				// application attachment
				_taskFormAttachmentType = APPLICATION_ATTACHMENT;
				break;

			default:
				// nothing to do
				break;
			}

			return _taskFormAttachmentType;
		}

	}

	// iApprove task form voice attachment info data keys
	public static class TaskFormVoiceAttachmentInfoDataKeys {

		// voice attachment voice file path and duration
		public static final String VOICEATTACHMENT_VOICE_FILEPATH = "voiceattachment_voice_filepath";
		public static final String VOICEATTACHMENT_VOICE_DURATION = "voiceattachment_voice_duration";

	}

	// iApprove task form text and image attachment parent frameLayout on click
	// listener
	class TaskFormText7ImageAttachmentParentFrameLayoutOnClickListener
			implements OnClickListener {

		@Override
		public void onClick(View v) {
			// check iApprove task form attachment type and set text attachment
			// textView or image attachment imageView as text or image
			// attachment form item on click response view
			if (TaskFormAttachmentType.TEXT_ATTACHMENT == _mAttachmentType) {
				_mAttachmentFormItemOnClickListener
						.onClick(_mTextAttachmentTextView);
			} else {
				_mAttachmentFormItemOnClickListener
						.onClick(_mImageAttachmentImgView);
			}
		}

	}

	// iApprove task form voice attachment play image view container
	// relativeLayout on click listener
	class TaskFormVoiceAttachmentPlayImgViewContainerRelativeLayoutOnClickListener
			implements OnClickListener {

		// voice attachment voice playing timer
		private final Timer VOICEPLAYING_TIMER = new Timer();

		// milliseconds per second
		private final Long MILLISECONDS_PER_SECOND = 1000L;

		// recover voice attachment voice ready to play status handle
		private final Handler RECOVER_VOICEREADY2PLAYSTATUS_HANDLE = new Handler();

		// get voice attachment voice playing drawable
		private final Drawable VOICEPLAYING_DRAWABLE = getResources()
				.getDrawable(R.anim.task_voiceattachment_voiceplaying);

		// voice attachment play imageView container relativeLayout padding top
		private Integer VOICEPLAYIMGVIEWCONTAINERRELATIVELAYOUT_PT = _mVoiceAttachmentPlayImgViewContainerRelativeLayout
				.getPaddingTop();

		// voice playing timer task
		private TimerTask _mVoicePlayingTimerTask;

		// is voice playing
		private Boolean _mIsVoicePlaying = false;

		@Override
		public void onClick(View v) {
			// get voice attachment form item parent viewGroup
			ViewGroup _rarentViewGroup = (ViewGroup) TaskFormAttachmentFormItem.this
					.getParent();

			// stop play other voice attachment voice
			for (int i = 1; i < _rarentViewGroup.getChildCount(); i++) {
				// get and check each voice attachment form item
				TaskFormAttachmentFormItem _formAttachmentFormItem = (TaskFormAttachmentFormItem) _rarentViewGroup
						.getChildAt(i);
				if (TaskFormAttachmentType.VOICE_ATTACHMENT == _formAttachmentFormItem._mAttachmentType
						&& TaskFormAttachmentFormItem.this != _formAttachmentFormItem
						&& _formAttachmentFormItem.isVoicePlaying()) {
					// click voice attachment play imageView container
					// relaticeLayout
					_formAttachmentFormItem._mVoiceAttachmentPlayImgViewContainerRelativeLayoutOnClickListener
							.onClick(_mVoiceAttachmentPlayImgViewContainerRelativeLayout);

					// break immediately
					break;
				}
			}

			// update voice playing flag, voice file path and set as voice
			// attachment voice play imageView tag
			_mVoiceAttachmentPlayImgView
					.setTag(TaskFormVoiceAttachmentInfoDataKeys.VOICEATTACHMENT_VOICE_FILEPATH
							.hashCode(), getVoiceAttachmentVoiceFilePath());
			_mVoiceAttachmentPlayImgView
					.setTag(_mIsVoicePlaying = !_mIsVoicePlaying);

			// check is voice playing
			if (_mIsVoicePlaying) {
				// update voice attachment play imageView container
				// relativeLayout background
				_mVoiceAttachmentPlayImgViewContainerRelativeLayout
						.setBackgroundResource(R.drawable.img_task_formattachment_formitem_voice_playing_bg);
				_mVoiceAttachmentPlayImgViewContainerRelativeLayout.setPadding(
						_mVoiceAttachmentPlayImgViewContainerRelativeLayout
								.getPaddingLeft(),
						VOICEPLAYIMGVIEWCONTAINERRELATIVELAYOUT_PT,
						_mVoiceAttachmentPlayImgViewContainerRelativeLayout
								.getPaddingRight(),
						_mVoiceAttachmentPlayImgViewContainerRelativeLayout
								.getPaddingBottom());

				// update voice attachment play imageView image drawable
				_mVoiceAttachmentPlayImgView
						.setImageDrawable(VOICEPLAYING_DRAWABLE);

				// start voice attachment voice playing animation drawable
				((AnimationDrawable) VOICEPLAYING_DRAWABLE).start();

				// voice playing timer schedule delay duration to recover voice
				// ready to play status
				VOICEPLAYING_TIMER.schedule(
						_mVoicePlayingTimerTask = new TimerTask() {

							@Override
							public void run() {
								// handle on UI thread with handle
								RECOVER_VOICEREADY2PLAYSTATUS_HANDLE
										.post(new Runnable() {

											@Override
											public void run() {
												// update voice playing flag and
												// set as voice attachment voice
												// play imageView tag
												_mVoiceAttachmentPlayImgView
														.setTag(_mIsVoicePlaying = !_mIsVoicePlaying);

												// recover voice attachment play
												// imageView container
												// relativeLayout background
												_mVoiceAttachmentPlayImgViewContainerRelativeLayout
														.setBackgroundResource(R.drawable.task_formattachment_formitem_voice_bg);
												_mVoiceAttachmentPlayImgViewContainerRelativeLayout.setPadding(
														_mVoiceAttachmentPlayImgViewContainerRelativeLayout
																.getPaddingLeft(),
														VOICEPLAYIMGVIEWCONTAINERRELATIVELAYOUT_PT,
														_mVoiceAttachmentPlayImgViewContainerRelativeLayout
																.getPaddingRight(),
														_mVoiceAttachmentPlayImgViewContainerRelativeLayout
																.getPaddingBottom());

												// recover voice attachment play
												// imageView image drawable
												_mVoiceAttachmentPlayImgView
														.setImageResource(R.drawable.img_task_formattachment_formitem_voice_play);

												// stop voice attachment voice
												// playing animation drawable
												((AnimationDrawable) VOICEPLAYING_DRAWABLE)
														.stop();

												// set voice attachment voice
												// play imageView as voice
												// attachment form item on click
												// response view
												_mAttachmentFormItemOnClickListener
														.onClick(_mVoiceAttachmentPlayImgView);
											}

										});
							}

						}, getVoiceAttachmentVoiceDuration()
								* MILLISECONDS_PER_SECOND);
			} else {
				// cancel voice playing timer task
				_mVoicePlayingTimerTask.cancel();

				// recover voice attachment play imageView container
				// relativeLayout background
				_mVoiceAttachmentPlayImgViewContainerRelativeLayout
						.setBackgroundResource(R.drawable.task_formattachment_formitem_voice_bg);
				_mVoiceAttachmentPlayImgViewContainerRelativeLayout.setPadding(
						_mVoiceAttachmentPlayImgViewContainerRelativeLayout
								.getPaddingLeft(),
						VOICEPLAYIMGVIEWCONTAINERRELATIVELAYOUT_PT,
						_mVoiceAttachmentPlayImgViewContainerRelativeLayout
								.getPaddingRight(),
						_mVoiceAttachmentPlayImgViewContainerRelativeLayout
								.getPaddingBottom());

				// recover voice attachment play imageView image drawable
				_mVoiceAttachmentPlayImgView
						.setImageResource(R.drawable.img_task_formattachment_formitem_voice_play);

				// stop voice attachment voice playing animation drawable
				((AnimationDrawable) VOICEPLAYING_DRAWABLE).stop();
			}

			// set voice attachment voice play imageView as voice attachment
			// form item on click response view
			_mAttachmentFormItemOnClickListener
					.onClick(_mVoiceAttachmentPlayImgView);
		}

	}

}
