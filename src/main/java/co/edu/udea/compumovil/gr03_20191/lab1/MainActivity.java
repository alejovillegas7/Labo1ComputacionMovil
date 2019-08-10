package co.edu.udea.compumovil.gr03_20191.lab1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.menu_mainactivity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){

            case R.id.salirAppMain:
                onDestroy();
                System.exit(0);
                return true;

            default:
                return false;
        }
    }

    public void toPlatos(View view){

        Intent intent = new Intent(getApplicationContext(), ActivityPlatos.class);
        startActivity(intent);

    }

    public void toBebidas(View view){

        Intent intent = new Intent(getApplicationContext(), ActivityBebidas.class);
        startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Restaurante");
        getSharedPrefernces();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getSharedPrefernces();
    }

    public void getSharedPrefernces() {
        SharedPreferences sharedPreferences = getSharedPreferences("co.edu.udea.compumovil.gr03_20191.lab1", Context.MODE_PRIVATE);
        String informacionGuardadaText = sharedPreferences.getString("informacion", "");
        String imagenString = sharedPreferences.getString("imagenBitmap", "");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String informacionGuardadaTextBebidas = sharedPreferences.getString("informacionBebidas", "");
        String imagenStringBebidas = sharedPreferences.getString("imagenBitmapBebidas", "");
        editor.putString("informacion", informacionGuardadaText);
        editor.putString("imagenBitmap", imagenString);
        editor.putString("informacionBebidas", informacionGuardadaTextBebidas);
        editor.putString("imagenBitmapBebidas", imagenStringBebidas);
        editor.commit();

    }

    public static Bitmap decodeToBase64(String input) {//metodo para convertir String en bitmap
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }
}
