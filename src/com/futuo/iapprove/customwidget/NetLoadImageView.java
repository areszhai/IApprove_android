package com.futuo.iapprove.customwidget;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.futuo.iapprove.utils.AsyncImageLoader;
import com.futuo.iapprove.utils.ImageCallback;

public class NetLoadImageView extends ImageView implements ImageCallback {

	public NetLoadImageView(Context context) {
		super(context);
	}

	public NetLoadImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public NetLoadImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void loadUrl(String url) {
		Bitmap img = AsyncImageLoader.getInstance().loadImage(url, this);
		setImageBitmap(img);
	}

	@Override
	public void imageLoaded(Bitmap bitmap) {
		setImageBitmap(bitmap);
	}

}
