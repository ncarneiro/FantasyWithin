package ncarneiro.org.fantasywithin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import services.NetworkService;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Referencer.setAct(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void loadData(View view) {
        Intent intent = new Intent(this, NetworkService.class);
        intent.putExtra("OP","LOAD");
        startService(intent);
    }

    public void saveData(View view) {
        Intent intent = new Intent(this, NetworkService.class);
        intent.putExtra("OP","SAVE");
        String texto = ((EditText) findViewById(R.id.texto_salvar)).getText().toString();
        if (texto != null) {
            intent.putExtra("VALUE", texto);
            startService(intent);
        }
        else
            Toast.makeText(this,"Vazio não caramba, preenche essa merda",Toast.LENGTH_SHORT).show();
    }

    public void stopServices(View v){
        Intent intent = new Intent(this, NetworkService.class);
        stopService(intent);
        System.out.println("Stopped");
    }
}
