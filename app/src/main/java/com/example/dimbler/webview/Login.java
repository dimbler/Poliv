package com.example.dimbler.webview; /**
 * Created by dimbler on 22.05.2015.
 */
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
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
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import android.widget.Toast;
import org.apache.http.NameValuePair;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;

public class Login extends AsyncTask<String, Void, String> {
    private final String LoginUrl = "https://api.smartliving.io/login";

    private Context context;

    public Login (Context context){
        this.context = context;
    }

    @Override
    protected String doInBackground(String... paramms) {

        HttpParams params = new BasicHttpParams();
        int timeoutConnection = 3000;
        HttpConnectionParams.setConnectionTimeout(params, timeoutConnection);
        int timeoutSocket = 5000;
        HttpConnectionParams.setSoTimeout(params, timeoutSocket);

        /*
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
        HttpProtocolParams.setUseExpectContinue(params, true);
        */

        SchemeRegistry schReg = new SchemeRegistry();
        schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("grant_type", "password"));
        nameValuePairs.add(new BasicNameValuePair("username", "dimbler"));
        nameValuePairs.add(new BasicNameValuePair("password", "yCnEX28V"));
        nameValuePairs.add(new BasicNameValuePair("client_id", "webSiteApp"));

        HttpClient client = new DefaultHttpClient(conMgr, params);
        HttpPost PostRequest = new HttpPost(LoginUrl);
        PostRequest.setHeader("Accept", "application/json, text/plain, */*");
        PostRequest.setHeader("Accept", "application/x-www-form-urlencoded; charset=UTF-8");
        String response;

        try {
            PostRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
            HttpResponse responce = client.execute(PostRequest);
            HttpEntity httpEntity = responce.getEntity();

            response = EntityUtils.toString(httpEntity);

            Log.d("response is", response);
            if (responce != null){
                return new JSONObject(response).getString("access_token");
            }
            //return new JSONObject(response);


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
    protected void onPostExecute(String result)
    {
        super.onPostExecute(result);
        if(result != null)
        {
            try
            {
                Log.d("JSON RESULT", result.toString());
                /*
                JSONObject jobj = result.getJSONObject("result");
                String status = jobj.getString("status");
                if(status.equals("true"))
                {
                    JSONArray array = jobj.getJSONArray("data");
                }
                */
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            Toast.makeText(context, R.string.error_connect_to_server, Toast.LENGTH_SHORT).show();
        }
    }
}
