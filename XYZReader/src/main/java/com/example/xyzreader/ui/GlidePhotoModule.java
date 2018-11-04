package com.example.xyzreader.ui;

import com.bumptech.glide.annotation.GlideExtension;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.annotation.GlideOption;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;

/**
 * Created by micha on 10/30/2018.
 */

@GlideModule
public class GlidePhotoModule extends AppGlideModule {

    // Make an extension to load the large article images easily
    @GlideExtension
    public static class ArticlePhotoGlideExtension {

        // Empty Constructor
        private ArticlePhotoGlideExtension() {}

        // Annotated method
        @GlideOption
        public static void articleBackground(RequestOptions requestOptions) {

            requestOptions
                    .centerCrop();

        }

    }

}
