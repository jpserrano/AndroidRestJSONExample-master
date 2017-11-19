package es.jota.detemporada;

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
            // TODO mostrar un log de error, aunque se pille el nombre de BD hay que dejar constancia para corregirlo
        } else {
            textAlimento.setText(recursoNombre);
        }

        // Si no existe la imagen del alimento mostramos una imagen genérica para que la interfaz no se descuadre
        ImageView imagenAlimento = (ImageView) findViewById(R.id.imagen_alimento);
        int recursoImagen = AlimentoActivity.this.getResources().getIdentifier("img_" + alimentoSeleccionado.getNombre(), "drawable", AlimentoActivity.this.getPackageName());
        if(recursoImagen == 0) {
            imagenAlimento.setImageResource(R.drawable.img_no_foto);
            // TODO mostrar un log para solucionar el problema
        } else {
            imagenAlimento.setImageResource(recursoImagen);
        }

        obtenerDatosGlobalesAlimento();
    }

    private void obtenerDatosGlobalesAlimento() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference docRef = database.collection("alimentos").document(nombreAlimento);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        alimentoGlobal = new AlimentoGlobal((String)document.get("nombre"));
                        alimentoGlobal.setCalorias((Long)document.get("val_calorias"));
                        alimentoGlobal.setCarbohidratos((Double)document.get("val_carbohidratos"));
                        alimentoGlobal.setProteinas((Double)document.get("val_proteinas"));
                        alimentoGlobal.setGrasas((Double)document.get("val_grasas"));

                        establecerDatosEnVista();
                    } else {
                        // TODO no se encuentra el documento, meter log
                        System.out.println("### ERROR 1");
                    }
                } else {
                    // TODO no se puede leer el documento, meter log
                    System.out.println("### ERROR 2");
                }
            }
        });
    }

    private void establecerDatosEnVista() {
        if(alimentoGlobal != null) {
            ((TextView) findViewById(R.id.valor_calorias)).setText(alimentoGlobal.getCalorias() + " kcal");
            ((TextView) findViewById(R.id.valor_carbohidratos)).setText(alimentoGlobal.getCarbohidratos() + " gr");
            ((TextView) findViewById(R.id.valor_proteinas)).setText(alimentoGlobal.getProteinas() + " gr");
            ((TextView) findViewById(R.id.valor_grasas)).setText(alimentoGlobal.getGrasas() + " gr");
        }
    }
}
