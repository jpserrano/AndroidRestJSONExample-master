package es.jota.detemporada;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    public static final String EXTRA_ALIMENTO_SELECCIONADO = "es.jota.detemporada.ALIMENTOSELECCIONADO";
    public static final String EXTRA_MES_SELECCIONADO = "es.jota.detemporada.MESELECCIONADO";

    Activity activity;
    CollectionReference coleccionAlimentosPais;
    ArrayList<Alimento> alimentos = new ArrayList<>();
    GridView gridview;
    int mesSeleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;

        // Calcular el mes actual
        mesSeleccionado = calcularMesActual();
        modificarTextoMesActual();

        // Definimos la acción a realizar cuando se selecciona un alimento de la lista
        gridview = (GridView) findViewById(R.id.gridview);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent intent = new Intent(MainActivity.this, AlimentoActivity.class);
                intent.putExtra(EXTRA_ALIMENTO_SELECCIONADO, alimentos.get(position));
                intent.putExtra(EXTRA_MES_SELECCIONADO, mesSeleccionado);
                startActivity(intent);
            }
        });

        // Obtener el país del usuario
        TelephonyManager tm = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        String countryCodeValue;
        if (tm != null) {
            countryCodeValue = tm.getNetworkCountryIso().toUpperCase();
        } else {
            // TODO mostrar un selector de paises disponibles cuando haya más de un país
            countryCodeValue = "ES";
        }

        // Acceso a la BD de Cloud Firestore
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        coleccionAlimentosPais = database.collection("alimentos_" + countryCodeValue);
        obtenerAlimentosPais();

        // Gestión de los botones atrás/alante para cambiar de mes
        final Button botonMesMenos = (Button)findViewById(R.id.boton_mes_menos);
        botonMesMenos.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(mesSeleccionado == 1) {
                    mesSeleccionado = 12;
                } else {
                    mesSeleccionado--;
                }

                modificarTextoMesActual();
                ordenarAlimentos();
                mostrarAlimentos();
            }
        });

        final Button botonMesMas = (Button)findViewById(R.id.boton_mes_mas);
        botonMesMas.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(mesSeleccionado == 12) {
                    mesSeleccionado = 1;
                } else {
                    mesSeleccionado++;
                }

                modificarTextoMesActual();
                ordenarAlimentos();
                mostrarAlimentos();
            }
        });
    }

    /**
     * Calcula el mes actual para mostrar los alimentos del mes en el que nos encontramos.
     *
     * @return Mes actual.
     */
    private int calcularMesActual() {
        DateFormat dateFormat = new SimpleDateFormat("MM", Locale.US);
        return Integer.parseInt(dateFormat.format(new Date()));
    }

    /**
     * Obtiene de BD el listado de alimentos del pais actual ordenados por calidad en el mes que nos encontramos.
     */
    private void obtenerAlimentosPais() {
        coleccionAlimentosPais.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                alimentos.clear();

                if(task.isSuccessful()) {
                    Alimento alimento;

                    // Formar la lista de alimentos
                    for(DocumentSnapshot document : task.getResult()) {
                        alimento = document.toObject(Alimento.class);
                        alimentos.add(alimento);
                    }

                    ordenarAlimentos();
                    mostrarAlimentos();
                } else {
                    Log.w(TAG, "obtenerAlimentosPais: No se pudo obtener la lista de alimentos por pais.");
                }
            }
        });
    }

    /**
     * Ordena la lista de alimentos en función de la calidad para el mes seleccionado y el nombre del alimento.
     */
    private void ordenarAlimentos() {
        Comparator<Alimento> comparador = Alimento.getComparator(mesSeleccionado);
        Collections.sort(alimentos, comparador);
    }

    /**
     * Muestra la lista de alimentos ordenada en la vista.
     */
    private void mostrarAlimentos() {
        ListaAlimentos listaAlimentos = new ListaAlimentos(activity, alimentos, mesSeleccionado);
        gridview.setAdapter(listaAlimentos);
    }

    /**
     * Modifica el mes que se muestra en pantalla.
     */
    private void modificarTextoMesActual() {
        final TextView textViewToChange = (TextView) findViewById(R.id.texto_mes);
        int recursoNombre = MainActivity.this.getResources().getIdentifier("mes_" + mesSeleccionado, "string", MainActivity.this.getPackageName());
        textViewToChange.setText(recursoNombre);
    }

    public int getMesSeleccionado() { return mesSeleccionado; }
    public void setMesSeleccionado(int mesSeleccionado) { this.mesSeleccionado = mesSeleccionado; }

}
