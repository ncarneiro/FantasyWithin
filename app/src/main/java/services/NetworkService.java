package services;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import ncarneiro.org.fantasywithin.R;
import ncarneiro.org.fantasywithin.Referencer;

public class NetworkService extends Service {

    NetworkTask networkTask;
    String endereco = "http://"+ Referencer.getHost() + "/map_data.php";
    String resultado = "";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        networkTask = new NetworkTask();
        networkTask.execute(endereco);
        return 0;
    }

    @Override
    public void onDestroy() {
        System.out.println("Destroy");
        networkTask.cancel(true);
        super.onDestroy();
    }

    private class NetworkTask extends AsyncTask<String,String,String>{

        URL url = null;
        HttpURLConnection urlConnection = null;
        InputStream in = null;

        @Override
        protected String doInBackground(String... params) {
            /*
            while (!isCancelled()) {
                System.out.println(params[0]);
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (false)
                    return "Algo errado não está certo";
            }
*/
            try {
                URL url = new URL(params[0]);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                resultado = readStream(con.getInputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultado;
        }


        private String readStream(InputStream in) {
            BufferedReader reader = null;
            String result = "";
            try {
                reader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                result = "";
                while ((line = reader.readLine()) != null) {
                    result += line;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            String json_string = "";

            try {
                JSONArray json = new JSONArray(s);
                for (int i = 0; i < json.length() ; i++) {
                    JSONObject json_obj = json.getJSONObject(i);
                    String key;
                    while ( json_obj.keys().hasNext()) {
                        key = json_obj.keys().next();
                        json_string += key + ": " + json_obj.get(key).toString() + "\n";
                        json_obj.remove(key);
                    }
                    json_string += "\n";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            TextView tv = (TextView) (Referencer.getAct().findViewById(R.id.texto_resultado));
            tv.setText(json_string);
            System.out.println("Post Execute");
            super.onPostExecute(s);
        }

        @Override
        protected void onCancelled() {
            System.out.println("Cancel");
            super.onCancelled();
        }
    }
}