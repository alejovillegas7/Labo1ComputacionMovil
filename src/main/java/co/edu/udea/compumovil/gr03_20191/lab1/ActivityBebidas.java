package co.edu.udea.compumovil.gr03_20191.lab1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ActivityBebidas extends AppCompatActivity {

    Button seleccionarBebida;
    Button guardarInfo;
    private int REQUEST_CODE=1;
    ImageView imagenSeleccionada;
    static EditText textoGuardado;
    static TextView nombreTextView;
    static TextView precio;
    static TextView ingredientes;
    static ImageView imagenGuardada;
    static SharedPreferences sharedPreferences;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        super.onOptionsItemSelected(item);

        switch (item.getItemId()){

            case R.id.limpiarFormulario:
                imagenSeleccionada.setImageResource(R.drawable.suma);
                nombreTextView.setText("");
                precio.setText("");
                ingredientes.setText("");
                return true;

            case R.id.salirApp:
                onDestroy();
                System.exit(0);
                return true;

            default:
                return false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String informacionGuardadaText = sharedPreferences.getString("informacionBebidas", "");
        String imagenString = sharedPreferences.getString("imagenBitmapBebidas", "");

        imagenGuardada.setImageBitmap(decodeToBase64(imagenString));
        textoGuardado.setText(informacionGuardadaText);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bebidas);
        setTitle("Agregar Bebida");
        seleccionarBebida = (Button) findViewById(R.id.seleccionarImagen);
        imagenSeleccionada = (ImageView) findViewById(R.id.imagenSeleccionada);
        guardarInfo=(Button) findViewById(R.id.botonGuardarInfo);
        textoGuardado = (EditText) findViewById(R.id.informaciónGuardada);
        nombreTextView = (TextView) findViewById(R.id.nombreTextView);
        precio = (TextView) findViewById(R.id.precio);
        ingredientes = (TextView) findViewById(R.id.ingredientes);
        imagenGuardada = (ImageView) findViewById(R.id.imagenGuardada);

        nombreTextView.setSingleLine(false);

        sharedPreferences = this.getSharedPreferences("co.edu.udea.compumovil.gr03_20191.lab1", Context.MODE_PRIVATE);
        String informacionGuardadaTextBebidas = sharedPreferences.getString("informacionBebidas", "");
        String imagenStringBebidas = sharedPreferences.getString("imagenBitmapBebidas", "");

        imagenGuardada.setImageBitmap(decodeToBase64(imagenStringBebidas));
        textoGuardado.setText(informacionGuardadaTextBebidas);

        seleccionarBebida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Seleccione Imagen"), REQUEST_CODE);
            }
        });

    }

    public static Bitmap decodeToBase64(String input) {//metodo para convertir String en bitmap
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    public void guardarInfo(View view){

        if(imagenSeleccionada.getDrawable()==null || nombreTextView.getText().toString().matches("")|| precio.getText().toString().matches("") || ingredientes.getText().toString().matches("")){

            Toast.makeText(this, "Debe de completar todos los campos", Toast.LENGTH_SHORT).show();

        }else {

            imagenGuardada.setImageDrawable(imagenSeleccionada.getDrawable());
            textoGuardado.setText("Nombre Bebida: " + nombreTextView.getText().toString() + "\n" + "Precio: " + precio.getText().toString() + " COP" + "\n" + "Ingredientes: " + ingredientes.getText().toString());

            BitmapDrawable bitmanImagenBebida = (BitmapDrawable) imagenGuardada.getDrawable();
            Bitmap bitmap = bitmanImagenBebida.getBitmap();

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("informacionBebidas", textoGuardado.getText().toString());
            editor.putString("imagenBitmapBebidas", encodeToBase64(bitmap));
            editor.commit();
        }

    }

    public static String encodeToBase64(Bitmap image) { // metodo para convertir el bitmap del ImageView en String y almacenarlo
        Bitmap immage = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immage.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        Log.d("Image Log:", imageEncoded);
        return imageEncoded;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQUEST_CODE && resultCode==RESULT_OK && data!=null && data.getData()!=null){

            Uri uri = data.getData();
            try{

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imagenSeleccionada.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
