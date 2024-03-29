package com.abdipor.music1;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.LocaleList;

import java.util.Locale;

import com.abdipor.appthemehelper.util.VersionUtils;

public class LanguageContextWrapper extends android.content.ContextWrapper {

    public LanguageContextWrapper(Context base) {
        super(base);
    }

    public static LanguageContextWrapper wrap(Context context, Locale newLocale) {
        Resources res = context.getResources();
        Configuration configuration = res.getConfiguration();

        if (VersionUtils.INSTANCE.hasNougatMR()) {
            configuration.setLocale(newLocale);

            LocaleList localeList = new LocaleList(newLocale);
            LocaleList.setDefault(localeList);
            configuration.setLocales(localeList);

            context = context.createConfigurationContext(configuration);

        } else if (VersionUtils.INSTANCE.hasLollipop()) {
            configuration.setLocale(newLocale);
            context = context.createConfigurationContext(configuration);

        } else {
            configuration.locale = newLocale;
            res.updateConfiguration(configuration, res.getDisplayMetrics());
        }

        return new LanguageContextWrapper(context);
    }
}