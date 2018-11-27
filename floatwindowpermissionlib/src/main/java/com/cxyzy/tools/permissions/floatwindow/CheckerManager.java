package com.cxyzy.tools.permissions.floatwindow;

import android.content.Context;

import com.cxyzy.tools.permissions.floatwindow.checkers.CheckerInterface;
import com.cxyzy.tools.permissions.floatwindow.checkers.CommonChecker;
import com.cxyzy.tools.permissions.floatwindow.checkers.HuaweiChecker;
import com.cxyzy.tools.permissions.floatwindow.checkers.MeizuChecker;
import com.cxyzy.tools.permissions.floatwindow.checkers.MiuiChecker;
import com.cxyzy.tools.permissions.floatwindow.checkers.OppoChecker;
import com.cxyzy.tools.permissions.floatwindow.checkers.QikuChecker;

import java.util.ArrayList;
import java.util.List;

public class CheckerManager {
    private static List<CheckerInterface> checkerList = new ArrayList<>();

    static {
        checkerList.add(new HuaweiChecker());
        checkerList.add(new MiuiChecker());
        checkerList.add(new OppoChecker());
        checkerList.add(new MeizuChecker());
        checkerList.add(new QikuChecker());
    }

    private static CheckerInterface getChecker() {
        for (CheckerInterface checker : checkerList) {
            if (checker.shouldCheckByMe()) {
                return checker;
            }
        }
        return new CommonChecker();
    }

    public static void checkFloatWindowPermission(Context context, FloatWinPermissionUtil.CheckAndApplyPermissionCallback callback) {
        getChecker().checkFloatWindowPermission(context, callback);
    }

    public static void applyPermission(Context context) {
        getChecker().applyPermission(context);
    }
}
