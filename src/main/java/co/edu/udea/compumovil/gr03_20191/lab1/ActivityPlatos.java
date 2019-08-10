package co.edu.udea.compumovil.gr03_20191.lab1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Time;
import java.text.SimpleDateFormat;

public class ActivityPlatos extends AppCompatActivity {

    Button seleccionarImagen;
    Button guardarInfo;
    EditText tiempoTexto;
    static EditText textoGuardado;
    static TextView nombreTextView;
    static TextView precio;
    static TextView ingredientes;
    ImageView imagenSeleccionada;
    RadioGroup radioGroup;
    RadioButton radioButton1;
    RadioButton radioButton2;
    static ImageView imagenGuardada;
    static SharedPreferences sharedPreferences;
    Calendar tiempoMinutos;
    private int REQUEST_CODE=1;

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
                tiempoTexto.setText("");

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_platos);
        setTitle("Agregar Plato");
        seleccionarImagen = (Button) findViewById(R.id.seleccionarImagen);
        guardarInfo = (Button) findViewById(R.id.botonGuardarInfo);
        imagenSeleccionada=(ImageView) findViewById(R.id.imagenSeleccionada);
        imagenGuardada =(ImageView) findViewById(R.id.imagenGuardada);
        tiempoTexto=(EditText) findViewById(R.id.tiempo);
        textoGuardado=(EditText) findViewById(R.id.informaciónGuardada);
        ingredientes=(EditText) findViewById(R.id.ingredientes);
        nombreTextView=(TextView) findViewById(R.id.nombreTextView);
        precio = (TextView) findViewById(R.id.precio);
        radioGroup=(RadioGroup)findViewById(R.id.radioTipo);
        radioButton1=(RadioButton) findViewById(R.id.radioEntrada);
        radioButton2=(RadioButton)findViewById(R.id.radioPlatoFuerte);

        nombreTextView.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS|InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        sharedPreferences = this.getSharedPreferences("co.edu.udea.compumovil.gr03_20191.lab1", Context.MODE_PRIVATE);
        String informacionGuardadaText = sharedPreferences.getString("informacion", "");
        String imagenString = sharedPreferences.getString("imagenBitmap", "");

        imagenGuardada.setImageBitmap(decodeToBase64(imagenString));
        textoGuardado.setText(informacionGuardadaText);

        tiempoTexto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tiempoMinutos = Calendar.getInstance();
                int minutos = tiempoMinutos.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog;
                timePickerDialog = new TimePickerDialog(ActivityPlatos.this, new OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hora_seleccionada, int minutos_seleccionado) {
                        tiempoTexto.setText(minutos_seleccionado + " minutos");
                    }
                }, 0, minutos,true);
                timePickerDialog.setTitle("Selecciones el tiempo");
                timePickerDialog.show();

            }
        });

        seleccionarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Seleccione Imagen"), REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        String informacionGuardadaText = sharedPreferences.getString("informacion", "");
        String imagenString = sharedPreferences.getString("imagenBitmap", "");

        imagenGuardada.setImageBitmap(decodeToBase64(imagenString));
        textoGuardado.setText(informacionGuardadaText);
    }

    public void guardarInfo(View view){

        if(imagenSeleccionada.getDrawable()==null || nombreTextView.getText().toString().matches("")|| precio.getText().toString().matches("") || ingredientes.getText().toString().matches("")){

            Toast.makeText(this, "Debe de completar todos los campos", Toast.LENGTH_SHORT).show();

        }else {
            int selectedId=radioGroup.getCheckedRadioButtonId();
            RadioButton seleccionado = (RadioButton)findViewById(selectedId);
            imagenGuardada.setImageDrawable(imagenSeleccionada.getDrawable());
            textoGuardado.setText("Nombre Plato: " + nombreTextView.getText().toString() + "\n" + "Precio: " + precio.getText().toString() + " COP" + "\n" + "Ingredientes: " + ingredientes.getText().toString()+"\n"+"Se sirve como: "+seleccionado.getText());

            BitmapDrawable bitmanImagen = (BitmapDrawable) imagenGuardada.getDrawable();
            Bitmap bitmap = bitmanImagen.getBitmap();

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("informacion", textoGuardado.getText().toString());
            editor.putString("imagenBitmap", encodeToBase64(bitmap));
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

    public static Bitmap decodeToBase64(String input) {//metodo para convertir String en bitmap
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
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
