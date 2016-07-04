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

package com.thq.pat.sina.provider;

import java.util.ArrayList;
import java.util.List;

import com.thq.pat.sina.Utils;

/**
 * tweets.
 */
public class Tweets {
	private static Tweets instance;

	public static Tweets getInstance() {
		if(null == instance) {
			instance = new Tweets();
		}
		return instance;             
	}

	private ArrayList<Tweet> tweets = new ArrayList<Tweet>();

	public Tweet getTweet(int num) {
		return tweets.get(num);
	}
	
	public ArrayList<Tweet> getTweets() {
	    if (tweets.size() == 0) {
            loadFromXml();
        }
	    return tweets;
	}

	public int getSize() {
		return tweets.size();
	}
	
	public void addTweet(Tweet tweet) {
		tweets.add(tweet);
	}
	
	public void clear() {
		tweets.clear();
	}
	
	public void loadFromXml() {
        ParseXml.parserSinaXmls(tweets, Utils.myPath + "/tweetxml/");
    }
}
