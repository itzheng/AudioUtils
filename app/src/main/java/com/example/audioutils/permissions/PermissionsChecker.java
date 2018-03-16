package com.example.audioutils.permissions;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

/**
 * 权限检测
 * <p>
 * Created by wangchenlong on 16/2/11.
 */
public class PermissionsChecker {

    /**
     * 判断是否缺少一组权限中的某个
     *
     * @param context
     * @param permissions
     * @return
     */
    public static boolean lacksPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            if (lacksPermission(context, permission))
                return true;
        }
        return false;
    }

    /**
     * 判断是否缺少某个权限
     *
     * @param context
     * @param permission
     * @return
     */
    public static boolean lacksPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED;
    }

}
