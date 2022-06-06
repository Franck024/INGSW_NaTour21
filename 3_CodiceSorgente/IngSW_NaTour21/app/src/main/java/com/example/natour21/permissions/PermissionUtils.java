package com.example.natour21.permissions;

import android.os.Build;

public class PermissionUtils {

    public static boolean shouldAskForPermissions(){
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
    }
}
