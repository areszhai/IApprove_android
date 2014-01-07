package com.futuo.iapprove.customwidget;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.futuo.iapprove.R;
import com.richitec.commontoolkit.CTApplication;

public class NAAFormAttachmentFormItem extends FrameLayout {

	private static final String LOG_TAG = NAAFormAttachmentFormItem.class
			.getCanonicalName();

	// new approve application form attachment type
	private NAAFormAttachmentType _mAttachmentType;

	// new approve application form attachment info object
	private Object _mAttachmentInfo;

	// new approve application form attachment text, image attachment parent
	// frameLayout and voice attachment play image view container
	// relativeLayout, play image view
	private FrameLayout _mText7ImageAttachmentParentFrameLayout;
	private RelativeLayout _mVoiceAttachmentPlayImgViewContainerRelativeLayout;
	private ImageView _mVoiceAttachmentPlayImgView;

	private NAAFormAttachmentFormItem(Context context) {
		super(context);

		// inflate new approve application form attachment form item layout
		LayoutInflater.from(context).inflate(
				R.layout.naa_formattachment_form_item_layout, this);

		// get new approve application form attachment text, image attachment
		// parent frameLayout
		_mText7ImageAttachmentParentFrameLayout = (FrameLayout) findViewById(R.id.naafafi_text7image_parent_frameLayout);

		// get new approve application form attachment voice attachment play
		// image view container relativeLayout
		_mVoiceAttachmentPlayImgViewContainerRelativeLayout = (RelativeLayout) findViewById(R.id.naafafi_voice_playImgView_container_relativeLayout);

		// get new approve application form attachment voice attachment play
		// image view
		_mVoiceAttachmentPlayImgView = (ImageView) findViewById(R.id.naafafi_voiceAttachment_play_imageView);

		// set new approve application form attachment type default value
		_mAttachmentType = NAAFormAttachmentType.TEXT_ATTACHMENT;
	}

	@Override
	public void setOnClickListener(OnClickListener l) {
		// check new approve application form attachment type
		switch (_mAttachmentType) {
		case TEXT_ATTACHMENT:
		case IMAGE_ATTACHMENT:
			// set text and image attachment parent frameLayout on click
			// listener
			_mText7ImageAttachmentParentFrameLayout.setOnClickListener(l);
			break;

		case VOICE_ATTACHMENT:
			// set voice attachment play image view container relativeLayout on
			// click listener
			_mVoiceAttachmentPlayImgViewContainerRelativeLayout
					.setOnClickListener(l);
			break;

		case APPLICATION_ATTACHMENT:
			// nothing to do
			break;
		}
	}

	@Override
	public void setOnLongClickListener(OnLongClickListener l) {
		// check new approve application form attachment type
		switch (_mAttachmentType) {
		case TEXT_ATTACHMENT:
		case IMAGE_ATTACHMENT:
			// set text and image attachment parent frameLayout on long click
			// listener
			_mText7ImageAttachmentParentFrameLayout.setOnLongClickListener(l);
			break;

		case VOICE_ATTACHMENT:
			// set voice attachment play image view container relativeLayout on
			// long click listener
			_mVoiceAttachmentPlayImgViewContainerRelativeLayout
					.setOnLongClickListener(l);
			break;

		case APPLICATION_ATTACHMENT:
			// nothing to do
			break;
		}
	}

	public RelativeLayout getVoicePlayImageViewContainer() {
		return _mVoiceAttachmentPlayImgViewContainerRelativeLayout;
	}

	public ImageView getVoicePlayImageView() {
		return _mVoiceAttachmentPlayImgView;
	}

	// get text attachment text
	public String getTextAttachmentText() {
		String _textAttachmentText = null;

		// check new approve application form attachment type
		if (NAAFormAttachmentType.TEXT_ATTACHMENT == _mAttachmentType) {
			// get text attachment text
			_textAttachmentText = null != _mAttachmentInfo ? _mAttachmentInfo
					.toString() : "";
		}

		return _textAttachmentText;
	}

	// get image attachment image bitmap
	public Bitmap getImageAttachmentImgBitmap() {
		Bitmap _imageAttachmentImgBitmap = null;

		// check new approve application form attachment type
		if (NAAFormAttachmentType.IMAGE_ATTACHMENT == _mAttachmentType) {
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

	// get voice attachment voice duration
	public Integer getVoiceAttachmentVoiceDuration() {
		Integer _voiceAttachmentVoiceDuration = null;

		// check new approve application form attachment type
		if (NAAFormAttachmentType.VOICE_ATTACHMENT == _mAttachmentType) {
			// convert attachment info object to integer
			try {
				_voiceAttachmentVoiceDuration = (Integer) _mAttachmentInfo;
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
			NAAFormAttachmentType type, Object info) {
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

			// get text attachment textView
			TextView _textAttachmentTextView = (TextView) _newNAAFormAttachmentFormItem
					.findViewById(R.id.naafafi_textAttachment_textView);

			// show it
			_textAttachmentTextView.setVisibility(View.VISIBLE);

			// get, check text attachment text and set its text
			String _textAttachmentText = _newNAAFormAttachmentFormItem
					.getTextAttachmentText();
			if (null != _textAttachmentText) {
				_textAttachmentTextView.setText(_textAttachmentText);
			}
			break;

		case IMAGE_ATTACHMENT:
			// show text and image attachment parent frameLayout
			_newNAAFormAttachmentFormItem._mText7ImageAttachmentParentFrameLayout
					.setVisibility(View.VISIBLE);

			// get image attachment imageView
			ImageView _imageAttachmentImgView = (ImageView) _newNAAFormAttachmentFormItem
					.findViewById(R.id.naafafi_imageAttachment_imageView);

			// show it
			_imageAttachmentImgView.setVisibility(View.VISIBLE);

			// get, check image attachment image bitmap and set its image bitmap
			Bitmap _imageAttachmentImgBitmap = _newNAAFormAttachmentFormItem
					.getImageAttachmentImgBitmap();
			if (null != _imageAttachmentImgBitmap) {
				_imageAttachmentImgView
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
	// new approve application form attachment type
	public static enum NAAFormAttachmentType {

		// text, image, voice and application attachment
		TEXT_ATTACHMENT, IMAGE_ATTACHMENT, VOICE_ATTACHMENT, APPLICATION_ATTACHMENT

	}

}
