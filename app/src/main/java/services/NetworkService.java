package services;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
    String loadData = "http://"+ Referencer.getHost() + "/loadData.php";
    String saveData = "http://"+ Referencer.getHost() + "/saveData.php";
    String resultado = "";
    String op = "";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        networkTask = new NetworkTask();
        op = intent.getStringExtra("OP");
        String value = intent.getStringExtra("VALUE");
        if (op.compareTo("LOAD") == 0)
            networkTask.execute(loadData);
        else
            networkTask.execute(saveData,value);
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
        HttpURLConnection con = null;

        @Override
        protected String doInBackground(String... params) {
            try {
                String end = params[0];
                if (op.compareTo("LOAD")==0)
                    end += "?table=recurso";
                else {
                    String param = params[1].trim().toUpperCase();
                    end += "?table=recurso&descricao="+param;
                }

                url = new URL(end);
                con = (HttpURLConnection) url.openConnection();
                resultado = readStream(con.getInputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultado;
        }

        @Override
        protected void onPostExecute(String s) {
            String resultado_final = "";
            TextView tv = null;
            if (op.compareTo("LOAD")==0) {
                resultado_final = json_parse(s);
                tv = (TextView) (Referencer.getAct().findViewById(R.id.texto_carregar));
                tv.setText(resultado_final);
            }
            else {
                // TODO: IF OK OR NOT OK
                Toast.makeText(
                        Referencer.getAct(),"Conteúdo salvo!",Toast.LENGTH_SHORT).show();
            }

            System.out.println("Post Execute");
            super.onPostExecute(s);
        }

        public String json_parse(String str){
            String json_string = "";
            try {
                JSONArray json = new JSONArray(str);
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
            return json_string;
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
        protected void onCancelled() {
            System.out.println("Cancel");
            super.onCancelled();
        }
    }
}