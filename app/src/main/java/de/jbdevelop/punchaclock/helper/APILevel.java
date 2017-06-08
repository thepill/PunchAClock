package de.jbdevelop.punchaclock.helper;

import android.os.Build;

public final class APILevel {

    public static final boolean currentLevelfits(int levelCodeToCheck){
        if (Build.VERSION.SDK_INT >= levelCodeToCheck){
            return true;
        }
        else {
            return false;
        }
    }

}
