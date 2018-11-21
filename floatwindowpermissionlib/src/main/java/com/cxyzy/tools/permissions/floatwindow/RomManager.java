package com.cxyzy.tools.permissions.floatwindow;

import android.content.Context;
import android.os.Build;

import com.cxyzy.tools.permissions.floatwindow.rom.CommonRom;
import com.cxyzy.tools.permissions.floatwindow.rom.HuaweiRom;
import com.cxyzy.tools.permissions.floatwindow.rom.MeizuRom;
import com.cxyzy.tools.permissions.floatwindow.rom.MiuiRom;
import com.cxyzy.tools.permissions.floatwindow.rom.OppoRom;
import com.cxyzy.tools.permissions.floatwindow.rom.QikuRom;
import com.cxyzy.tools.permissions.floatwindow.rom.RomInterface;

import java.util.ArrayList;
import java.util.List;

public class RomManager {
    private static List<RomInterface> romList = new ArrayList<>();
    private static RomInterface meizu = new MeizuRom();

    static {
        romList.add(new MiuiRom());
        romList.add(new HuaweiRom());
        romList.add(new QikuRom());
        romList.add(new OppoRom());
    }

    private static RomInterface getRom() {
        //6.0 版本之后由于 google 增加了对悬浮窗权限的管理，所以方式就统一了
        if (Build.VERSION.SDK_INT < 23) {
            for (RomInterface rom : romList) {
                if (rom.checkRom()) {
                    return rom;
                }
            }
        } else if (meizu.checkRom()) {
            return meizu;
        }
        return new CommonRom();
    }

    public static boolean checkFloatWindowPermission(Context context) {
        return getRom().checkFloatWindowPermission(context);
    }

    public static void applyPermission(Context context) {
        getRom().applyPermission(context);
    }
}
