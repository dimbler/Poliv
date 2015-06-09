package com.example.dimbler.webview; /**
 * Created by dimbler on 22.05.2015.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;
import android.os.AsyncTask;
import android.util.Log;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import android.widget.Toast;

public class ExecNasos extends AsyncTask<String, Boolean, String> {
    private final String serviceUrl = "https://api.smartliving.io/asset/c8TSQK105hWL7vPnbpJ8ol722/command";

    @Override
    protected String doInBackground(String... paramms) {
        try {
            String url = new String("https://api.smartliving.io/asset/c8TSQK105hWL7vPnbpJ8ol722/command");
            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
            HttpProtocolParams.setUseExpectContinue(params, true);

            SchemeRegistry schReg = new SchemeRegistry();
            schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
            ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);



            HttpClient client = new DefaultHttpClient(conMgr, params);
            HttpPut putRequest = new HttpPut(serviceUrl);
            putRequest.setHeader("Content-type", "application/json;charset=utf-8");
            putRequest.setHeader("Accept", "application/json, text/plain, */*");
            putRequest.setHeader("Authorization", "Bearer " + paramms[0]);

            putRequest.setEntity(new StringEntity("{'value':" + paramms[1] + "}"));
            HttpResponse response = client.execute(putRequest);
            InputStream content = response.getEntity().getContent();
            BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
            String result = "";
            String s = "";
            while ((s = buffer.readLine()) != null) {
                result += s;
            }
            return result;

        }
        catch (UnsupportedEncodingException e) {
            Log.e("AsyncOperationFailed", e.getMessage());
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            Log.e("AsyncOperationFailed", e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("AsyncOperationFailed", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(String result) {
        Log.d("MESSAGE", result);
    }
}
