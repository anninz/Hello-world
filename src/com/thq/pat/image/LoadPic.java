package com.thq.pat.image;

import java.util.ArrayList;

import android.util.Log;

import com.thq.pat.image.provider.Image;
import com.thq.pat.image.provider.Images;

public class LoadPic {
    public static void loadurl(ArrayList<String> urls) {
        for(String url:urls){   
            Image image = new Image();   
            image.setUimageUrls(url);   
            image.setImageThumbUrls(url);
            Images.images.add(image);
            Log.d("THQ ","Loadpic:"+"-Loadpic url = " + url); 
        }  
    }
}
