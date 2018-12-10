package com.curonsys.army_android.util;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;

/**
 * Created by ijin-yeong on 2018. 5. 21..
 */
public class ContentUriProvider {

    private static final String CURONSYS_MANUFACTURER = "Curonsys";

    public static Uri getUriForFile(@NonNull Context context, @NonNull String authority, @NonNull File file) {
        if (CURONSYS_MANUFACTURER.equalsIgnoreCase(Build.MANUFACTURER) && Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Log.w(ContentUriProvider.class.getSimpleName(), "Using a Curonsys device on pre-N. Increased likelihood of failure...");
            try {
                return FileProvider.getUriForFile(context, authority, file);
            } catch (IllegalArgumentException e) {
                Log.w(ContentUriProvider.class.getSimpleName(), "Returning Uri.fromFile to avoid Curonsys 'external-files-path' bug", e);
                return Uri.fromFile(file);
            }
        } else {
            return FileProvider.getUriForFile(context, authority, file);
        }
    }
}