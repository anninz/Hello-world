/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.thq.pat.image.util;

import android.content.Context;

import com.thq.pat.image.provider.Images;
import com.thq.pat.image.ui.TouchView;

/**
 * This fragment will populate the children of the ViewPager from {@link ImageDetailActivity}.
 */
public class ImageDownload {
    private static final String IMAGE_DATA_EXTRA = "resId";
    private TouchView mImageView;
    private ImageWorker mImageWorker;
    
    private int longest = 0;
    

    private static final String IMAGE_CACHE_DIR = "images";
    public static final String EXTRA_IMAGE = "extra_image";


    /**
     * Empty constructor as per the Fragment documentation
     */
    public ImageDownload() {super();}

    public void init(int screenH, int screenW,TouchView imageView, Context context, String url) {
        

        
//        final DisplayMetrics displaymetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
//        final int screenH = displaymetrics.heightPixels;
//        final int screenW = displaymetrics.widthPixels;
        longest = screenH > screenW ? screenH : screenW;

        
        Utils.setScreenW(screenW);
        Utils.setScreenH(screenH);
        mImageView = imageView;
//        mImageView.setScreenW(screenW);
//        mImageView.setScreenH(screenH);
//        mImageView.setPadding(0, 0, 0, 0);
        
        mImageWorker = new ImageFetcher(context, longest);
        mImageWorker.setAdapter(Images.imageWorkerUrlsAdapter);
        mImageWorker.setImageCache(ImageCache.findOrCreateCache(context, IMAGE_CACHE_DIR));
        mImageWorker.setImageFadeIn(false);
        ImageWorker.flag=1;
        mImageWorker.loadImage(url, mImageView);
    }
    
    
    /**
     * Cancels the asynchronous work taking place on the ImageView, called by the adapter backing
     * the ViewPager when the child is destroyed.
     */
    public void cancelWork() {
        ImageWorker.cancelWork(mImageView);
        mImageView.setImageDrawable(null);
        mImageView = null;
    }
}
