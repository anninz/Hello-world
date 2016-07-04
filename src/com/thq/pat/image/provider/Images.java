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

package com.thq.pat.image.provider;

import java.util.ArrayList;

import com.thq.pat.image.util.ImageWorker.ImageWorkerAdapter;

/**
 * Image vector
 */
public class Images {

    /**
     * This are PicasaWeb URLs and could potentially change. Ideally the PicasaWeb API should be
     * used to fetch the URLs.
     */
    public  static ArrayList<Image> images=new ArrayList<Image>();
	/**
     * This are PicasaWeb thumbnail URLs and could potentially change. Ideally the PicasaWeb API
     * should be used to fetch the URLs.
     */
   
    /**
     * Simple static adapter to use for images.
     */
    public final static ImageWorkerAdapter imageWorkerUrlsAdapter = new ImageWorkerAdapter() {
        @Override
        public Object getItem(int num) {
            return Images.images.get(num).getUimageUrls();
        }

        @Override
        public int getSize() {
            return Images.images.size();
        }
    };

    /**
     * Simple static adapter to use for image thumbnails.
     */
    public final static ImageWorkerAdapter imageThumbWorkerUrlsAdapter = new ImageWorkerAdapter() {
        @Override
        public Object getItem(int num) {
            return Images.images.get(num).getImageThumbUrls();
        }

        @Override
        public int getSize() {
            return Images.images.size();
        }
    };
}
