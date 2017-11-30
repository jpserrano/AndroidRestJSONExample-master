package es.jota.detemporada;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static es.jota.detemporada.MainActivity.EXTRA_ALIMENTO_SELECCIONADO;
import static es.jota.detemporada.MainActivity.EXTRA_MES_SELECCIONADO;

public class ScrollingActivity extends AppCompatActivity {

    private static final String TAG = ScrollingActivity.class.getName();

    private int mesSeleccionado;
    private Alimento alimentoSeleccionado;
    private AlimentoGlobal alimentoGlobal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // StatusBar transparente y NavigationBar translúcida
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
            w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        recuperarDatosMainActivity();
        establecerDatosToolbar();
        obtenerDatosGlobalesAlimento();
    }

    /**
     * Recupera el Intent del MainActivity obteniendo el alimento seleccionado
     * y el mes en el que nos encontramos.
     */
    private void recuperarDatosMainActivity() {
        Intent intent = getIntent();
        alimentoSeleccionado = (Alimento) intent.getSerializableExtra(EXTRA_ALIMENTO_SELECCIONADO);
        // TODO El mes seleccionado se usará para marcarlo en el panel de calidades por mes
        mesSeleccionado = intent.getIntExtra(EXTRA_MES_SELECCIONADO, 1);
    }

    /**
     * Establece el título y la imagen de fondo del toolbar.
     */
    private void establecerDatosToolbar() {
        // Si en el strings no está definido el nombre del alimento mostramos el nombre desde el objeto en BD
        int recursoNombre = getResources().getIdentifier(alimentoSeleccionado.getNombre(), "string", getPackageName());
        if(recursoNombre == 0) {
            setTitle(alimentoSeleccionado.getNombre());
            Log.w(TAG, "onCreate: el alimento '" + alimentoSeleccionado.getNombre() + "' no está definido en el fichero string");
        } else {
            setTitle(recursoNombre);
        }

        // Si no existe la imagen del alimento mostramos una imagen genérica para que la interfaz no se descuadre
        int recursoImagen = getResources().getIdentifier("img_" + alimentoSeleccionado.getNombre(), "drawable", getPackageName());
        ImageView imagenBackground = (ImageView) findViewById(R.id.img_background);
        if(recursoImagen == 0) {
            imagenBackground.setBackgroundResource(R.drawable.img_no_foto);
            Log.w(TAG, "onCreate: el alimento '" + alimentoSeleccionado.getNombre() + "' no tiene la imagen asociada");
        } else {
            imagenBackground.setBackgroundResource(recursoImagen);
        }
    }

    /**
     * Recupera de la BD los datos globales del alimento.
     */
    private void obtenerDatosGlobalesAlimento() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference docRef = database.collection("alimentos").document(alimentoSeleccionado.getNombre());

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        alimentoGlobal = document.toObject(AlimentoGlobal.class);
                        establecerDatosEnVista();
                    } else {
                        Log.w(TAG, "obtenerDatosGlobalesAlimento: el documento asociado a '" + alimentoSeleccionado.getNombre() + "' es nulo");
                    }
                } else {
                    Log.w(TAG, "obtenerDatosGlobalesAlimento: error al obtener el documento '" + alimentoSeleccionado.getNombre() + "'");
                }
            }
        });
    }

    /**
     * Muestra en la vista los valores nutricionales obtenidos de BD.
     */
    @SuppressLint("SetTextI18n")
    private void establecerDatosEnVista() {
        if(alimentoGlobal != null) {
            ((TextView) findViewById(R.id.valor_calorias)).setText(String.valueOf(alimentoGlobal.getCalorias()) + " kcal");
            ((TextView) findViewById(R.id.valor_carbohidratos)).setText(String.valueOf(alimentoGlobal.getCarbohidratos()) + " gr");
            ((TextView) findViewById(R.id.valor_proteinas)).setText(String.valueOf(alimentoGlobal.getProteinas()) + " gr");
            ((TextView) findViewById(R.id.valor_grasas)).setText(String.valueOf(alimentoGlobal.getGrasas()) + " gr");
        }
    }
}
