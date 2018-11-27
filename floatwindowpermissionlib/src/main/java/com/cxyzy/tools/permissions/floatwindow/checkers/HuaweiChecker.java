package com.cxyzy.tools.permissions.floatwindow.checkers;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.cxyzy.tools.permissions.floatwindow.FloatWinPermissionUtil;

import java.lang.reflect.Method;

import static com.cxyzy.tools.permissions.floatwindow.checkers.CommonChecker.commonROMPermissionApplyInternal;
import static com.cxyzy.tools.permissions.floatwindow.checkers.CommonChecker.getSystemProperty;

/**
 * 华为rom悬浮窗权限检测类
 */
public class HuaweiChecker extends BaseChecker {
    private static final String TAG = "HuaweiChecker";
    private boolean shouldImmediateCheck = true;//是否应该立即执行检测

    @Override
    public boolean shouldCheckByMe() {
        return isThisRom();
    }

    private boolean isThisRom() {
        return Build.MANUFACTURER.contains("HUAWEI");
    }

    /**
     * 检测 Huawei 悬浮窗权限,如果没有，则弹出悬浮窗申请界面。
     * 2018.11.27 测试过程中，发现华为P10 Plus(Android 8.0.0)存在bug。如果获得悬浮窗权限后，立即返回对应界面，会出现再次检测仍然没有权限,所以增加了延时处理。
     */
    @Override
    public void checkFloatWindowPermission(Context context, FloatWinPermissionUtil.CheckAndApplyPermissionCallback callback) {
        final long DELAY_CHECK_TIME = 1000;
        //如果不是第一次检测，都需要等待DELAY_CHECK_TIME后执行。原因见方法注释。
        if (shouldImmediateCheck) {
            shouldImmediateCheck = false;
            checkFloatWindowPermissionInternal(context, callback);
        } else {
            new Handler().postDelayed(() -> checkFloatWindowPermissionInternal(context, callback), DELAY_CHECK_TIME);
        }
    }

    private void checkFloatWindowPermissionInternal(Context context, FloatWinPermissionUtil.CheckAndApplyPermissionCallback callback) {
        boolean isPermitted = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            isPermitted = Settings.canDrawOverlays(context);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            isPermitted = checkOp(context, Build.VERSION_CODES.N);
        }
        if (isPermitted) {
            shouldImmediateCheck = true;
        }
        checkFloatWindowPermission(isPermitted, callback);
    }

    /**
     * 获取 emui 版本号
     *
     * @return
     */
    public static double getEmuiVersion() {
        try {
            String emuiVersion = getSystemProperty("ro.build.version.emui");
            String version = emuiVersion.substring(emuiVersion.indexOf("_") + 1);
            return Double.parseDouble(version);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 4.0;
    }

    /**
     * 去华为权限申请页面
     */
    @Override
    public void applyPermission(Context context) {
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.addviewmonitor.AddViewMonitorActivity");//悬浮窗管理页面
            intent.setComponent(comp);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                commonROMPermissionApplyInternal(context);
            } else if (getEmuiVersion() == 3.1) {
                //emui 3.1 的适配
                context.startActivity(intent);
            } else {
                //emui 3.0 的适配
                comp = new ComponentName("com.huawei.systemmanager", "com.huawei.notificationmanager.ui.NotificationManagmentActivity");//悬浮窗管理页面
                intent.setComponent(comp);
                context.startActivity(intent);
            }
        } catch (SecurityException e) {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName comp = new ComponentName("com.huawei.systemmanager",
                    "com.huawei.permissionmanager.ui.MainActivity");//华为权限管理，跳转到本app的权限管理页面,这个需要华为接口权限，未解决
            intent.setComponent(comp);
            context.startActivity(intent);
            Log.e(TAG, Log.getStackTraceString(e));
        } catch (ActivityNotFoundException e) {
            /**
             * 手机管家版本较低 HUAWEI SC-UL10
             */
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName comp = new ComponentName("com.Android.settings", "com.android.settings.permission.TabItem");//权限管理页面 android4.4
            intent.setComponent(comp);
            context.startActivity(intent);
            e.printStackTrace();
            Log.e(TAG, Log.getStackTraceString(e));
        } catch (Exception e) {
            Toast.makeText(context, "进入设置页面失败，请手动设置", Toast.LENGTH_LONG).show();
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private boolean checkOp(Context context, int op) {
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


