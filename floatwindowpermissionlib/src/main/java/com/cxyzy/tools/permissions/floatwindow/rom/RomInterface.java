package com.cxyzy.tools.permissions.floatwindow.rom;

import android.content.Context;

public interface RomInterface {
    boolean checkRom();

    boolean checkFloatWindowPermission(Context context);

    void applyPermission(Context context);
}
