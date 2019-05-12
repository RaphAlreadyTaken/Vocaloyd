package fr.vocaloyd;

import android.app.Application;
import android.content.Context;

/**
 * Inspired from : https://www.dev2qa.com/android-get-application-context-from-anywhere-example/
 */
public class VocaloydApp extends Application
{
    private static Context appContext;
    private static int port = 0;

    @Override
    public void onCreate()
    {
        super.onCreate();
        appContext = getApplicationContext();
    }

    public static Context getAppContext()
    {
        return appContext;
    }


    public static int getPort()
    {
        return port;
    }

    public static void setPort(int paramPort)
    {
        port = paramPort;
    }
}
