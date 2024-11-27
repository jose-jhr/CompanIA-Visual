package com.ingenieriajhr.visualcontroll.accessibility;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;

public class AccessibilityServiceUtil {

    public static boolean isAccessibilityServiceEnabled(Context context, Class<?> service) {
        int enabledServices = Settings.Secure.getInt(context.getContentResolver(),
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES, 0);

        TextUtils.SimpleStringSplitter colonSplitter = new TextUtils.SimpleStringSplitter(':');
        colonSplitter.setString(String.valueOf(enabledServices));

        for (String colonSeparatedService : colonSplitter) {
            if (colonSeparatedService.equals(service.getName())) {
                return true; // El servicio de accesibilidad está habilitado
            }
        }
        return false; // El servicio de accesibilidad no está habilitado
    }
}
