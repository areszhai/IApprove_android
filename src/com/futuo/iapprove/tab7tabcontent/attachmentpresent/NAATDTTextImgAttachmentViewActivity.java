package com.futuo.iapprove.tab7tabcontent.attachmentpresent;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.futuo.iapprove.R;
import com.futuo.iapprove.utils.AppDataSaveRestoreUtils;

public class NAATDTTextImgAttachmentViewActivity extends Activity {

	private static final String LOG_TAG = NAATDTTextImgAttachmentViewActivity.class
			.getCanonicalName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.naa_tdt_text_image_attachment_view_activity_layout);

		// get the extra data
		final Bundle _extraData = getIntent().getExtras();

		// define new approve application or to-do task text or image attachment
		// object
		Object _naa6tdtText6ImgAttachmentObject = null;

		// check the data
		if (null != _extraData) {
			// get task application attachment name and open url
			_naa6tdtText6ImgAttachmentObject = _extraData
					.get(NAATDTTextImgAttachmentViewExtraData.NAA_TDT_TEXT_IMAGE_ATTCHMENT_OBJECT);
		}

		// set subViews
		// check new approve application or to-do task text or image attachment
		// object
		if (null != _naa6tdtText6ImgAttachmentObject) {
			if (_naa6tdtText6ImgAttachmentObject instanceof Bitmap) {
				// image attachment object
				// get image attachment image imageView
				ImageView _imgAttachmentImageView = (ImageView) findViewById(R.id.naatdt_imgAttachment_image_imageView);

				// shown it
				_imgAttachmentImageView.setVisibility(View.VISIBLE);

				// set its image bitmap
				_imgAttachmentImageView
						.setImageBitmap((Bitmap) _naa6tdtText6ImgAttachmentObject);

				// set its on click listener
				_imgAttachmentImageView
						.setOnClickListener(new NAATDTTextImgAttachmentOnClickListener());
			} else if (_naa6tdtText6ImgAttachmentObject instanceof String) {
				// text attachment object
				// get text attachment text textView
				TextView _textAttachmentTextView = (TextView) findViewById(R.id.naatdt_textAttachment_text_textView);

				// shown it
				_textAttachmentTextView.setVisibility(View.VISIBLE);

				// set its text
				_textAttachmentTextView
						.setText(_naa6tdtText6ImgAttachmentObject.toString());

				// set its on click listener
				_textAttachmentTextView
						.setOnClickListener(new NAATDTTextImgAttachmentOnClickListener());
			} else {
				Log.e(LOG_TAG,
						"Unrecognize new approve application or to-do task text or image attachment object = "
								+ _naa6tdtText6ImgAttachmentObject);
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
	// new approve application or to-do task text or image attachment view extra
	// data constant
	public static final class NAATDTTextImgAttachmentViewExtraData {

		// new approve application or to-do task text or image attachment object
		public static final String NAA_TDT_TEXT_IMAGE_ATTCHMENT_OBJECT = "naa_tdt_text_image_attachment_object";

	}

	// new approve application or to-do task text or image attachment on click
	// listener
	class NAATDTTextImgAttachmentOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// finish the new approve application or to-do task text or image
			// attachment view activity
			finish();
		}

	}

}
