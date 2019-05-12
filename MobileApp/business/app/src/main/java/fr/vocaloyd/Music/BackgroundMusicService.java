package fr.vocaloyd.Music;

import android.content.Context;

import fr.vocaloyd.MainActivity;

public class BackgroundMusicService extends Thread
{
    String action;
    Boolean stop;

    public BackgroundMusicService(String act)
    {
        action = act;
        stop = false;
    }

    public void run()
    {
        while (stop == false)
        {
            MusicService mServ = new MusicService();
            mServ.execute(action);

            try
            {
                Thread.sleep(10000);
            }
            catch (Exception ex)
            {
                System.out.println(ex.getMessage());
            }
        }
    }

    public void interrupt()
    {
        stop = true;
    }
}
