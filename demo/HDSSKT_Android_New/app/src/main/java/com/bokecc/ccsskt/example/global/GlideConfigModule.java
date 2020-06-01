package com.bokecc.ccsskt.example.global;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

/**
 * @author swh
 * @Description  Glide全局配置类
 */
@GlideModule
public class GlideConfigModule extends AppGlideModule {

    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        //磁盘缓存配置（默认缓存大小250M，默认保存在内部存储中）

    }


    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        //替换组件，如网络请求组件
    }


}
