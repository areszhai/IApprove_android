package com.futuo.iapprove.utils;

import java.lang.ref.SoftReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v4.util.LruCache;

public class AsyncImageLoader {

	private static final int CACHE_SIZE = 10;
	public LruCache<String, SoftReference<Bitmap>> imageCache = new LruCache<String, SoftReference<Bitmap>>(
			CACHE_SIZE);
	private ExecutorService executorService = Executors.newFixedThreadPool(5);
	private final Handler handler = new Handler();

	private static AsyncImageLoader instance = null;

	public static AsyncImageLoader getInstance() {
		if (instance == null) {
			instance = new AsyncImageLoader();
		}
		return instance;
	}

	// 如果缓存过就从缓存中取出数据
	public Bitmap loadImage(final String imageUrl, final ImageCallback callback) {
		if (imageCache.get(imageUrl) != null) {
			SoftReference<Bitmap> softReference = imageCache.get(imageUrl);
			if (softReference.get() != null) {
				return softReference.get();
			}
		}

		// 缓存中没有图像，则从网络上取出数据，并将取出的数据缓存到内存中
		executorService.submit(new Runnable() {
			public void run() {
				final Bitmap bitmap = IApproveUtils.getHttpBitmap(imageUrl);
				imageCache.put(imageUrl, new SoftReference<Bitmap>(bitmap));
				handler.post(new Runnable() {
					public void run() {
						if (callback != null) {
							callback.imageLoaded(bitmap);
						}
					}
				});
			}
		});

		return null;
	}

}
