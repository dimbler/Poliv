package com.example.dimbler.webview; /**
 * Created by dimbler on 22.05.2015.
 */
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

public class GetSensorData extends AsyncTask<String, Void, JSONObject> {
    private final String DeviceUrl = "https://api.smartliving.io/device/c8TSQK105hWL7vPnbpJ8ol7";

    private Context context;

    public GetSensorData (Context context){
        this.context = context;
    }

    @Override
    protected JSONObject doInBackground(String... paramms) {

        HttpParams params = new BasicHttpParams();
        int timeoutConnection = 3000;
        HttpConnectionParams.setConnectionTimeout(params, timeoutConnection);
        int timeoutSocket = 5000;
        HttpConnectionParams.setSoTimeout(params, timeoutSocket);

        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
        HttpProtocolParams.setUseExpectContinue(params, true);

        SchemeRegistry schReg = new SchemeRegistry();
        schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);

        HttpClient client = new DefaultHttpClient(conMgr, params);
        HttpGet GetRequest = new HttpGet(DeviceUrl);
        GetRequest.setHeader("Accept", "application/json, text/plain, */*");
        GetRequest.setHeader("Authorization", "Bearer " + paramms[0]);
        String response;

        try {
            HttpResponse responce = client.execute(GetRequest);
            HttpEntity httpEntity = responce.getEntity();

            response = EntityUtils.toString(httpEntity);

            if (response != null){
                if (new JSONObject(response).has("key")) {
                    MainActivity.access_token = null;
                }
            }

            Log.d("response is", response);
            Log.d("auth id", paramms[0]);
            return new JSONObject(response);


        } catch (UnsupportedEncodingException e) {
            Log.e("AsyncOperationFailed", e.getMessage());
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            Log.e("AsyncOperationFailed", e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("AsyncOperationFailed", e.getMessage());
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject result)
    {
        super.onPostExecute(result);
        if(result != null)
        {
            try
            {
                Log.d("JSON RESULT", result.toString());
                //JSONObject jobj = result.getJSONObject("result");
                //String status = jobj.getString("status");
                //if(status.equals("true"))
                //{
                //   JSONArray array = jobj.getJSONArray("data");
                //}
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            //Toast.makeText(this.context, "Ошибка соединения с сервером", Toast.LENGTH_SHORT).show();
        }
    }
}
