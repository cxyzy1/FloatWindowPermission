package com.cxyzy.tools.permissions.floatwindow;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

public class FloatWinPermissionUtil {
    private static final String TAG = "FloatWinPermissionUtil";

    /**
     * 检查是否有悬浮窗权限，如果没有，则弹出悬浮窗申请界面。
     */
    public static void applyOrShowFloatWindow(@NonNull final Context context, @NonNull final CheckPermissionCallback callback) {
        CheckAndApplyPermissionCallback checkAndApplyPermissionCallback = new CheckAndApplyPermissionCallback() {
            @Override
            public void onNotPermitted() {
                applyPermission(context);
            }

            @Override
            public void onPermitted() {
                if (callback != null) {
                    callback.onPermitted();
                }
            }
        };

        CheckerManager.checkFloatWindowPermission(context, checkAndApplyPermissionCallback);
    }

    public static void applyPermission(final Context context) {
        showConfirmDialog(context, confirmed -> {
            if (confirmed) {
                CheckerManager.applyPermission(context);
            } else {
                Log.e(TAG, "User manually refuse OVERLAY_PERMISSION");
            }
        });

    }

    private static void showConfirmDialog(Context context, OnConfirmResult result) {
        showConfirmDialog(context, context.getString(R.string.no_permission_notice), result);
    }

    private static void showConfirmDialog(Context context, String message, final OnConfirmResult result) {
        Dialog dialog = new AlertDialog.Builder(context).setCancelable(true).setTitle("")
                .setMessage(message)
                .setPositiveButton("现在去开启",
                        (dialog1, which) -> {
                            result.confirmResult(true);
                            dialog1.dismiss();
                        }).setNegativeButton("暂不开启",
                        (dialog12, which) -> {
                            result.confirmResult(false);
                            dialog12.dismiss();
                        }).create();
        dialog.show();
    }

    interface OnConfirmResult {
        /**
         * 用户是否点击了确认
         *
         * @param confirm
         */
        void confirmResult(boolean confirm);
    }

    public interface CheckPermissionCallback {
        /**
         * 在权限已被授予的情况下调用
         */
        void onPermitted();
    }

    public interface CheckAndApplyPermissionCallback extends CheckPermissionCallback {
        /**
         * 在权限未被授予时进行调用
         */
        void onNotPermitted();
    }
}
