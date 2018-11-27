package com.cxyzy.tools.permissions.floatwindow.checkers;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.cxyzy.tools.permissions.floatwindow.FloatWinPermissionUtil;

import java.lang.reflect.Method;

import static com.cxyzy.tools.permissions.floatwindow.checkers.CommonChecker.commonROMPermissionApplyInternal;
import static com.cxyzy.tools.permissions.floatwindow.checkers.CommonChecker.getSystemProperty;

/**
 * 魅族rom悬浮窗权限检测类
 */
public class MeizuChecker extends BaseChecker {
    private static final String TAG = "MeizuChecker";

    @Override
    public boolean shouldCheckByMe() {
        return isThisRom();
    }

    private boolean isThisRom() {
        String meizuFlymeOSFlag = getSystemProperty("ro.build.display.id");
        if (TextUtils.isEmpty(meizuFlymeOSFlag)) {
            return false;
        } else if (meizuFlymeOSFlag.contains("flyme") || meizuFlymeOSFlag.toLowerCase().contains("flyme")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 检测 meizu 悬浮窗权限
     */
    @Override
    public void checkFloatWindowPermission(Context context, FloatWinPermissionUtil.CheckAndApplyPermissionCallback callback) {
        boolean isPermitted = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            isPermitted = Settings.canDrawOverlays(context);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            isPermitted = checkOp(context, Build.VERSION_CODES.N);
        }
        checkFloatWindowPermission(isPermitted, callback);
    }

    /**
     * 去魅族权限申请页面
     */
    @Override
    public void applyPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            commonROMPermissionApplyInternal(context);
        } else {
            try {
                Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
                intent.putExtra("packageName", context.getPackageName());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } catch (Exception e) {
                try {
                    Log.e(TAG, "获取悬浮窗权限, 打开AppSecActivity失败, " + Log.getStackTraceString(e));
                    // 最新的魅族flyme 6.2.5 用上述方法获取权限失败, 不过又可以用下述方法获取权限了
                    commonROMPermissionApplyInternal(context);
                } catch (Exception eFinal) {
                    Log.e(TAG, "获取悬浮窗权限失败, 通用获取方法失败, " + Log.getStackTraceString(eFinal));
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static boolean checkOp(Context context, int op) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= Build.VERSION_CODES.KITKAT) {
            AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            try {
                Class clazz = AppOpsManager.class;
                Method method = clazz.getDeclaredMethod("checkOp", int.class, int.class, String.class);
                return AppOpsManager.MODE_ALLOWED == (int) method.invoke(manager, op, Binder.getCallingUid(), context.getPackageName());
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        } else {
            Log.e(TAG, "Below API " + Build.VERSION_CODES.KITKAT + " cannot invoke!");
        }
        return false;
    }
}
