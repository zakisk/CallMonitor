package com.example.callmonitor;

import android.app.Activity;
import android.app.role.RoleManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class JavaCode {

    public static final int REQUEST_ID = 1;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static void requestRole(Activity activity) {
        RoleManager roleManager = (RoleManager) activity.getSystemService(Context.ROLE_SERVICE);
        Intent intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING);
        activity.startActivityForResult(intent, REQUEST_ID);
    }

}
