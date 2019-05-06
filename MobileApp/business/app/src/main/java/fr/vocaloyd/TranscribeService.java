package fr.vocaloyd;

import android.os.AsyncTask;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.net.URI;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.client.methods.CloseableHttpResponse;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.client.utils.URIBuilder;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.impl.client.CloseableHttpClient;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.util.EntityUtils;

public class TranscribeService extends AsyncTask<File, Void, String>
{
    /**
     * Inspired from https://stackoverflow.com/questions/2304663/apache-httpclient-making-multipart-form-post
     * @param file
     * @return
     */
    protected String doInBackground(File... file)
    {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        String result = "";

        try
        {
            URI uri = new URIBuilder()
                .setScheme("http")
                .setHost("192.168.1.15")
                .setPort(3131)
                .setPath("/transcribe/newTranscript")
                .build();

            HttpEntity entity = MultipartEntityBuilder
                .create()
                .addTextBody("device", android.os.Build.MODEL)
                .addBinaryBody("inputAudio", file[0], ContentType.MULTIPART_FORM_DATA, file[0].getName())
                .build();

            HttpPost httpPost = new HttpPost(uri);
            httpPost.setEntity(entity);

            CloseableHttpResponse res = client.execute(httpPost);
            result = EntityUtils.toString(res.getEntity());
            client.close();
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }

        return result;
    }

    protected void onPostExecute(String result)
    {
        EventBus.getDefault().post(new TranscribeEvent(result));
    }
}
