package com.futuo.iapprove.tab7tabcontent.attachmentpresent;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.futuo.iapprove.R;
import com.futuo.iapprove.utils.AppDataSaveRestoreUtils;
import com.richitec.commontoolkit.utils.DisplayScreenUtils;

public class NAATaskTextImgAttachmentViewActivity extends Activity {

	private static final String LOG_TAG = NAATaskTextImgAttachmentViewActivity.class
			.getCanonicalName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.naa_task_text_image_attachment_view_activity_layout);

		// get the extra data
		final Bundle _extraData = getIntent().getExtras();

		// define new approve application or iApprove task text or image
		// attachment object
		Object _naa6taskText6ImgAttachmentObject = null;

		// check the data
		if (null != _extraData) {
			// get task application attachment name and open url
			_naa6taskText6ImgAttachmentObject = _extraData
					.get(NAATaskTextImgAttachmentViewExtraData.NAA_TASK_TEXT_IMAGE_ATTCHMENT_OBJECT);
		}

		// set subViews
		// check new approve application or iApprove task text or image
		// attachment object
		if (null != _naa6taskText6ImgAttachmentObject) {
			if (_naa6taskText6ImgAttachmentObject instanceof Bitmap) {
				// image attachment object
				// get image attachment image imageView
				ImageView _imgAttachmentImageView = (ImageView) findViewById(R.id.naatask_imgAttachment_image_imageView);

				// shown it
				_imgAttachmentImageView.setVisibility(View.VISIBLE);

				// set its image bitmap
				_imgAttachmentImageView
						.setImageBitmap((Bitmap) _naa6taskText6ImgAttachmentObject);

				// set its on click listener
				_imgAttachmentImageView
						.setOnClickListener(new NAATaskTextImgAttachmentOnClickListener());
			} else if (_naa6taskText6ImgAttachmentObject instanceof String) {
				// text attachment object
				// shown text attachment textView container scrollView
				((ScrollView) findViewById(R.id.naatask_textAttachment_textView_container_scrollView))
						.setVisibility(View.VISIBLE);

				// get text attachment text textView
				TextView _textAttachmentTextView = (TextView) findViewById(R.id.naatask_textAttachment_text_textView);

				// set text attachment textView min height
				_textAttachmentTextView.setMinHeight(DisplayScreenUtils
						.screenHeight() - DisplayScreenUtils.statusBarHeight());

				// set its text
				_textAttachmentTextView
						.setText(_naa6taskText6ImgAttachmentObject.toString());

				// set its on click listener
				_textAttachmentTextView
						.setOnClickListener(new NAATaskTextImgAttachmentOnClickListener());
			} else {
				Log.e(LOG_TAG,
						"Unrecognize new approve application or to-do task text or image attachment object = "
								+ _naa6taskText6ImgAttachmentObject);
			}
		}
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		AppDataSaveRestoreUtils.onRestoreInstanceState(savedInstanceState);

		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		AppDataSaveRestoreUtils.onSaveInstanceState(outState);

		super.onSaveInstanceState(outState);
	}

	// inner class
	// new approve application or iApprove task text or image attachment view
	// extra data constant
	public static final class NAATaskTextImgAttachmentViewExtraData {

		// new approve application or iApprove task text or image attachment
		// object
		public static final String NAA_TASK_TEXT_IMAGE_ATTCHMENT_OBJECT = "naa_task_text_image_attachment_object";

	}

	// new approve application or iApprove task text or image attachment on
	// click listener
	class NAATaskTextImgAttachmentOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// finish the new approve application or iApprove task text or image
			// attachment view activity
			finish();
		}

	}

}
