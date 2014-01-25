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
import com.futuo.iapprove.customwidget.TaskFormAttachmentFormItem.TaskFormAttachmentType;
import com.richitec.commontoolkit.CTApplication;

public class NAAFormAttachmentFormItem extends FrameLayout {

	private static final String LOG_TAG = NAAFormAttachmentFormItem.class
			.getCanonicalName();

	// new approve application form attachment type
	private TaskFormAttachmentType _mAttachmentType;

	// new approve application form attachment info object
	private Object _mAttachmentInfo;

	// new approve application form attachment text, image attachment parent
	// frameLayout, text attachment textView, image attachment imageView and
	// voice attachment play image view container relativeLayout, play image
	// view
	private FrameLayout _mText7ImageAttachmentParentFrameLayout;
	private TextView _mTextAttachmentTextView;
	private ImageView _mImageAttachmentImgView;
	private RelativeLayout _mVoiceAttachmentPlayImgViewContainerRelativeLayout;
	private ImageView _mVoiceAttachmentPlayImgView;

	// new approve application form attachment form item on click and long click
	// listener
	private OnClickListener _mAttachmentFormItemOnClickListener;
	private OnLongClickListener _mAttachmentFormItemOnLongClickListener;

	// new approve application form voice attachment play image view container
	// relativeLayout on click listener
	private NAAFormVoiceAttachmentPlayImgViewContainerRelativeLayoutOnClickListener _mVoiceAttachmentPlayImgViewContainerRelativeLayoutOnClickListener;

	private NAAFormAttachmentFormItem(Context context) {
		super(context);

		// inflate new approve application form attachment form item layout
		LayoutInflater.from(context).inflate(
				R.layout.naa_formattachment_form_item_layout, this);

		// get new approve application form attachment text, image attachment
		// parent frameLayout
		_mText7ImageAttachmentParentFrameLayout = (FrameLayout) findViewById(R.id.naafafi_text7image_parent_frameLayout);

		// get new approve application form text attachment textView and image
		// attachment imageView
		_mTextAttachmentTextView = (TextView) findViewById(R.id.naafafi_textAttachment_textView);
		_mImageAttachmentImgView = (ImageView) findViewById(R.id.naafafi_imageAttachment_imageView);

		// get new approve application form voice attachment play image view
		// container relativeLayout
		_mVoiceAttachmentPlayImgViewContainerRelativeLayout = (RelativeLayout) findViewById(R.id.naafafi_voice_playImgView_container_relativeLayout);

		// get new approve application form voice attachment play image view
		_mVoiceAttachmentPlayImgView = (ImageView) findViewById(R.id.naafafi_voiceAttachment_play_imageView);

		// set new approve application form attachment type default value
		_mAttachmentType = TaskFormAttachmentType.TEXT_ATTACHMENT;
	}

	@Override
	public void setOnClickListener(OnClickListener l) {
		// save attachment on click listener
		_mAttachmentFormItemOnClickListener = l;

		// check new approve application form attachment type
		switch (_mAttachmentType) {
		case TEXT_ATTACHMENT:
		case IMAGE_ATTACHMENT:
			// set text and image attachment parent frameLayout on click
			// listener
			_mText7ImageAttachmentParentFrameLayout
					.setOnClickListener(new NAAFormText7ImageAttachmentParentFrameLayoutOnClickListener());
			break;

		case VOICE_ATTACHMENT:
			// set voice attachment play image view container relativeLayout on
			// click listener
			_mVoiceAttachmentPlayImgViewContainerRelativeLayout
					.setOnClickListener(_mVoiceAttachmentPlayImgViewContainerRelativeLayoutOnClickListener = new NAAFormVoiceAttachmentPlayImgViewContainerRelativeLayoutOnClickListener());
			break;

		case APPLICATION_ATTACHMENT:
			// nothing to do
			break;
		}
	}

	@Override
	public void setOnLongClickListener(OnLongClickListener l) {
		// save attachment on long click listener
		_mAttachmentFormItemOnLongClickListener = l;

		// new new approve application form text, image attachment parent
		// frameLayout and voice attachment play image view container
		// relativeLayout on long click listener
		NAAFormText7ImgAttaPF7VoiceAttaPlayImgViewCROnLongClickListener _naaFormAttachmentFormItemOnLongClickListener = new NAAFormText7ImgAttaPF7VoiceAttaPlayImgViewCROnLongClickListener();

		// check new approve application form attachment type
		switch (_mAttachmentType) {
		case TEXT_ATTACHMENT:
		case IMAGE_ATTACHMENT:
			// set text and image attachment parent frameLayout on long click
			// listener
			_mText7ImageAttachmentParentFrameLayout
					.setOnLongClickListener(_naaFormAttachmentFormItemOnLongClickListener);
			break;

		case VOICE_ATTACHMENT:
			// set voice attachment play image view container relativeLayout on
			// long click listener
			_mVoiceAttachmentPlayImgViewContainerRelativeLayout
					.setOnLongClickListener(_naaFormAttachmentFormItemOnLongClickListener);
			break;

		case APPLICATION_ATTACHMENT:
			// nothing to do
			break;
		}
	}

	public TaskFormAttachmentType getAttachmentType() {
		return _mAttachmentType;
	}

	public Boolean isVoicePlaying() {
		Boolean _isVoicePlaying = false;

		// get and check voice attachment play imageView tag
		Object _tag = _mVoiceAttachmentPlayImgView.getTag();
		if (null != _tag) {
			_isVoicePlaying = (Boolean) _tag;
		}

		return _isVoicePlaying;
	}

	// fake click voice attachment play imageView container relaticeLayout
	public void fakeClickVoiceAttachmentPlayImgViewContainerRelativeLayout() {
		_mVoiceAttachmentPlayImgViewContainerRelativeLayoutOnClickListener
				.onClick(_mVoiceAttachmentPlayImgViewContainerRelativeLayout);
	}

	// get text attachment text
	private String getTextAttachmentText() {
		String _textAttachmentText = null;

		// check new approve application form attachment type
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

		// check new approve application form attachment type
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

	// get voice attachment voice file path
	private String getVoiceAttachmentVoiceFilePath() {
		String _voiceAttachmentVoiceFilePath = null;

		// check new approve application form attachment type
		if (TaskFormAttachmentType.VOICE_ATTACHMENT == _mAttachmentType) {
			// convert attachment info object to map
			try {
				@SuppressWarnings("unchecked")
				Map<String, Object> _voiceAttachmentInfo = (Map<String, Object>) _mAttachmentInfo;

				// get voice attachment voice file path
				_voiceAttachmentVoiceFilePath = (String) _voiceAttachmentInfo
						.get(NAAFormVoiceAttachmentInfoDataKeys.VOICEATTACHMENT_VOICE_FILEPATH);
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

		// check new approve application form attachment type
		if (TaskFormAttachmentType.VOICE_ATTACHMENT == _mAttachmentType) {
			// convert attachment info object to map
			try {
				@SuppressWarnings("unchecked")
				Map<String, Object> _voiceAttachmentInfo = (Map<String, Object>) _mAttachmentInfo;

				// get voice attachment voice duration
				_voiceAttachmentVoiceDuration = (Integer) _voiceAttachmentInfo
						.get(NAAFormVoiceAttachmentInfoDataKeys.VOICEATTACHMENT_VOICE_DURATION);
			} catch (Exception e) {
				Log.e(LOG_TAG,
						"Get voice attachment voice duration error, exception message = "
								+ e.getMessage());

				e.printStackTrace();
			}
		}

		return _voiceAttachmentVoiceDuration;
	}

	// generate new approve application form attachment form item with type and
	// info
	public static NAAFormAttachmentFormItem generateNAAFormAttachmentFormItem(
			TaskFormAttachmentType type, Object info) {
		// get application context
		Context _appContext = CTApplication.getContext();

		// new new approve application form attachment form item
		NAAFormAttachmentFormItem _newNAAFormAttachmentFormItem = new NAAFormAttachmentFormItem(
				_appContext);

		// check new approve application form attachment type and set it
		if (null != type) {
			_newNAAFormAttachmentFormItem._mAttachmentType = type;
		}

		// set new approve application form attachment info object
		_newNAAFormAttachmentFormItem._mAttachmentInfo = info;

		// check new approve application form attachment type again
		switch (_newNAAFormAttachmentFormItem._mAttachmentType) {
		case TEXT_ATTACHMENT:
			// show text and image attachment parent frameLayout
			_newNAAFormAttachmentFormItem._mText7ImageAttachmentParentFrameLayout
					.setVisibility(View.VISIBLE);

			// show text attachment textView
			_newNAAFormAttachmentFormItem._mTextAttachmentTextView
					.setVisibility(View.VISIBLE);

			// get, check text attachment text and set its text
			String _textAttachmentText = _newNAAFormAttachmentFormItem
					.getTextAttachmentText();
			if (null != _textAttachmentText) {
				_newNAAFormAttachmentFormItem._mTextAttachmentTextView
						.setText(_textAttachmentText);
			}
			break;

		case IMAGE_ATTACHMENT:
			// show text and image attachment parent frameLayout
			_newNAAFormAttachmentFormItem._mText7ImageAttachmentParentFrameLayout
					.setVisibility(View.VISIBLE);

			// show image attachment imageView
			_newNAAFormAttachmentFormItem._mImageAttachmentImgView
					.setVisibility(View.VISIBLE);

			// get, check image attachment image bitmap and set its image bitmap
			Bitmap _imageAttachmentImgBitmap = _newNAAFormAttachmentFormItem
					.getImageAttachmentImgBitmap();
			if (null != _imageAttachmentImgBitmap) {
				_newNAAFormAttachmentFormItem._mImageAttachmentImgView
						.setImageBitmap(_imageAttachmentImgBitmap);
			}
			break;

		case VOICE_ATTACHMENT:
			// show voice attachment parent relativeLayout
			_newNAAFormAttachmentFormItem.findViewById(
					R.id.naafafi_voice_parent_relativeLayout).setVisibility(
					View.VISIBLE);

			// get and check voice attachment voice duration
			Integer _voiceAttachmentVoiceDuration = _newNAAFormAttachmentFormItem
					.getVoiceAttachmentVoiceDuration();
			if (null != _voiceAttachmentVoiceDuration) {
				// set voice duration textView text
				((TextView) _newNAAFormAttachmentFormItem
						.findViewById(R.id.naafafi_voiceAttachment_duration_textView))
						.setText(String
								.format(_appContext
										.getResources()
										.getString(
												R.string.naa_voiceAttachment_voiceDuration_format),
										_voiceAttachmentVoiceDuration));

				// get voice play image view layout params
				RelativeLayout.LayoutParams _layoutParams = (RelativeLayout.LayoutParams) _newNAAFormAttachmentFormItem._mVoiceAttachmentPlayImgView
						.getLayoutParams();

				// update voice play image view margin left
				_layoutParams
						.setMargins(
								Math.min(
										_appContext
												.getResources()
												.getDimensionPixelSize(
														R.dimen.naa_formVoiceAttachment_playImgView_marginLeft_min)
												+ (_voiceAttachmentVoiceDuration - 1)
												* _appContext
														.getResources()
														.getDimensionPixelSize(
																R.dimen.naa_formVoiceAttachment_playImgView_marginLeft_increaseStep),
										_appContext
												.getResources()
												.getDimensionPixelSize(
														R.dimen.naa_formVoiceAttachment_playImgView_marginLeft_max)),
								_layoutParams.topMargin,
								_layoutParams.rightMargin,
								_layoutParams.bottomMargin);
				_newNAAFormAttachmentFormItem._mVoiceAttachmentPlayImgView
						.setLayoutParams(_layoutParams);
			}
			break;

		case APPLICATION_ATTACHMENT:
			// nothing to do
			break;
		}

		return _newNAAFormAttachmentFormItem;
	}

	// inner class
	// new approve application form voice attachment info data keys
	public static class NAAFormVoiceAttachmentInfoDataKeys {

		// voice attachment voice file path and duration
		public static final String VOICEATTACHMENT_VOICE_FILEPATH = "voiceattachment_voice_filepath";
		public static final String VOICEATTACHMENT_VOICE_DURATION = "voiceattachment_voice_duration";

	}

	// new approve application form text and image attachment parent frameLayout
	// on click listener
	class NAAFormText7ImageAttachmentParentFrameLayoutOnClickListener implements
			OnClickListener {

		@Override
		public void onClick(View v) {
			// check new approve application form attachment type and set text
			// attachment textView or image attachment imageView as text or
			// image attachment form item on click response view
			if (TaskFormAttachmentType.TEXT_ATTACHMENT == _mAttachmentType) {
				_mAttachmentFormItemOnClickListener
						.onClick(_mTextAttachmentTextView);
			} else {
				_mAttachmentFormItemOnClickListener
						.onClick(_mImageAttachmentImgView);
			}
		}

	}

	// new approve application form voice attachment play image view container
	// relativeLayout on click listener
	class NAAFormVoiceAttachmentPlayImgViewContainerRelativeLayoutOnClickListener
			implements OnClickListener {

		// voice attachment voice playing timer
		private final Timer VOICEPLAYING_TIMER = new Timer();

		// milliseconds per second
		private final Long MILLISECONDS_PER_SECOND = 1000L;

		// recover voice attachment voice ready to play status handle
		private final Handler RECOVER_VOICEREADY2PLAYSTATUS_HANDLE = new Handler();

		// get voice attachment voice playing drawable
		private final Drawable VOICEPLAYING_DRAWABLE = getResources()
				.getDrawable(R.anim.naa_voiceattachment_voiceplaying);

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
			ViewGroup _rarentViewGroup = (ViewGroup) NAAFormAttachmentFormItem.this
					.getParent();

			// stop play other voice attachment voice
			for (int i = 1; i < _rarentViewGroup.getChildCount(); i++) {
				// get and check each voice attachment form item
				NAAFormAttachmentFormItem _formAttachmentFormItem = (NAAFormAttachmentFormItem) _rarentViewGroup
						.getChildAt(i);
				if (TaskFormAttachmentType.VOICE_ATTACHMENT == _formAttachmentFormItem._mAttachmentType
						&& NAAFormAttachmentFormItem.this != _formAttachmentFormItem
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
					.setTag(NAAFormVoiceAttachmentInfoDataKeys.VOICEATTACHMENT_VOICE_FILEPATH
							.hashCode(), getVoiceAttachmentVoiceFilePath());
			_mVoiceAttachmentPlayImgView
					.setTag(_mIsVoicePlaying = !_mIsVoicePlaying);

			// check is voice playing
			if (_mIsVoicePlaying) {
				// update voice attachment play imageView container
				// relativeLayout background
				_mVoiceAttachmentPlayImgViewContainerRelativeLayout
						.setBackgroundResource(R.drawable.img_naa_formattachment_formitem_voice_playing_bg);
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
														.setBackgroundResource(R.drawable.naa_formattachment_formitem_voice_bg);
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
														.setImageResource(R.drawable.img_naa_formattachment_formitem_voice_play);

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
						.setBackgroundResource(R.drawable.naa_formattachment_formitem_voice_bg);
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
						.setImageResource(R.drawable.img_naa_formattachment_formitem_voice_play);

				// stop voice attachment voice playing animation drawable
				((AnimationDrawable) VOICEPLAYING_DRAWABLE).stop();
			}

			// set voice attachment voice play imageView as voice attachment
			// form item on click response view
			_mAttachmentFormItemOnClickListener
					.onClick(_mVoiceAttachmentPlayImgView);
		}

	}

	// new approve application form text, image attachment parent frameLayout
	// and voice attachment play image view container relativeLayout on long
	// click listener
	class NAAFormText7ImgAttaPF7VoiceAttaPlayImgViewCROnLongClickListener
			implements OnLongClickListener {

		@Override
		public boolean onLongClick(View v) {
			// set form attachment form item as text, image attachment parent
			// frameLayout and voice attachment play image view container
			// relativeLayout on long click response view
			return _mAttachmentFormItemOnLongClickListener
					.onLongClick(NAAFormAttachmentFormItem.this);
		}

	}

}
