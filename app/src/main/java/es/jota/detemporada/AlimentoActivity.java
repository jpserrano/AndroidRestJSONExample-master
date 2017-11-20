package es.jota.detemporada;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AlimentoActivity extends AppCompatActivity {

    private static final String TAG = AlimentoActivity.class.getName();

    private String nombreAlimento;
    private AlimentoGlobal alimentoGlobal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alimento);

        Intent intent = getIntent();
        Alimento alimentoSeleccionado = (Alimento) intent.getSerializableExtra(MainActivity.EXTRA_ALIMENTO_SELECCIONADO);

        nombreAlimento = alimentoSeleccionado.getNombre();

        // TODO El mes seleccionado se usará para marcarlo en el panel de calidades por mes
        int mesSeleccionado = intent.getIntExtra(MainActivity.EXTRA_MES_SELECCIONADO, 1);

        // Si en el strings no está definido el nombre del alimento mostramos el nombre desde el objeto en BD
        TextView textAlimento = (TextView) findViewById(R.id.texto_alimento);
        int recursoNombre = AlimentoActivity.this.getResources().getIdentifier(alimentoSeleccionado.getNombre(), "string", AlimentoActivity.this.getPackageName());
        if(recursoNombre == 0) {
            textAlimento.setText(alimentoSeleccionado.getNombre());
            Log.w(TAG, "onCreate: el alimento '" + alimentoSeleccionado.getNombre() + "' no está definido en el fichero string");
        } else {
            textAlimento.setText(recursoNombre);
        }

        // Si no existe la imagen del alimento mostramos una imagen genérica para que la interfaz no se descuadre
        ImageView imagenAlimento = (ImageView) findViewById(R.id.imagen_alimento);
        int recursoImagen = AlimentoActivity.this.getResources().getIdentifier("img_" + alimentoSeleccionado.getNombre(), "drawable", AlimentoActivity.this.getPackageName());
        if(recursoImagen == 0) {
            imagenAlimento.setImageResource(R.drawable.img_no_foto);
            Log.w(TAG, "onCreate: el alimento '" + alimentoSeleccionado.getNombre() + "' no tiene la imagen asociada");
        } else {
            imagenAlimento.setImageResource(recursoImagen);
        }

        obtenerDatosGlobalesAlimento();
    }

    /**
     * Recupera de la BD los datos globales del alimento.
     */
    private void obtenerDatosGlobalesAlimento() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference docRef = database.collection("alimentos").document(nombreAlimento);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null) {
                    alimentoGlobal = document.toObject(AlimentoGlobal.class);
                    establecerDatosEnVista();
                } else {
                    Log.w(TAG, "obtenerDatosGlobalesAlimento: el documento asociado a '" + nombreAlimento + "' es nulo");
                }
            } else {
                Log.w(TAG, "obtenerDatosGlobalesAlimento: error al obtener el documento '" + nombreAlimento + "'");
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
