package fr.vocaloyd;

import android.os.AsyncTask;

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

public class TranscribeTask extends AsyncTask<File, Void, String>
{
    String command;

    /**
     * Inspired from https://stackoverflow.com/questions/2304663/apache-httpclient-making-multipart-form-post
     * @param file
     * @return
     */
    @Override
    protected String doInBackground(File... file)
    {
        CloseableHttpClient client = HttpClientBuilder.create().build();

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
            String result = EntityUtils.toString(res.getEntity());
            client.close();

            return result;
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }

        return null;
    }

    protected void onPostExecute(String result)
    {
        if (result != null)
        {
            AnalyzeTask task = new AnalyzeTask();
            task.execute(result);
        }
    }
}
