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
 

package com.bysj.PurePicture.util;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

*//**
 * 一个简单的没有UI的 Fragment，用于存储单个 对象 和保留 配置 的改变.
 *也用于保留 ImageCache 对象.
 *//*
public class RetainFragment extends Fragment {
    private static final String TAG = "RetainFragment";
    private Object mObject;

    *//**
     * 空构造函数，
     *//*
    public RetainFragment() {}

    *//**
     * Locate an existing instance of this Fragment or if not found, create and
     * add it using FragmentManager.
     *
     * @param fm The FragmentManager manager to use.
     * @return 现有的 Fragment实例 或是刚刚生成的实例。
     *         
     *//*
    public static RetainFragment findOrCreateRetainFragment(FragmentManager fm) {
        // 查看是否已经保留当前内容到 fragment.
        RetainFragment mRetainFragment = (RetainFragment) fm.findFragmentByTag(TAG);

        // 如果没有保存 (或是第一次运行), 需要创建并添加.
        if (mRetainFragment == null) {
            mRetainFragment = new RetainFragment();
            fm.beginTransaction().add(mRetainFragment, TAG).commit();
        }

        return mRetainFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 确保Fragment已经保留 配置的改变
        setRetainInstance(true);
    }

    *//**
     * 保存单个object到 Fragment.
     *
     * @param object The object to store
     *//*
    public void setObject(Object object) {
        mObject = object;
    }

    *//**
     * 获取以后的object.
     *
     * @return The stored object
     *//*
    public Object getObject() {
        return mObject;
    }

}
*/