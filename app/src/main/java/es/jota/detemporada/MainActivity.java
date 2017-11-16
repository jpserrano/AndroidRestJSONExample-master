package es.jota.detemporada;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity {

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

        /*************************/

        gridview = (GridView) findViewById(R.id.gridview);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent intent = new Intent(MainActivity.this, AlimentoActivity.class);
                intent.putExtra(EXTRA_ALIMENTO_SELECCIONADO, alimentos.get(position));
                intent.putExtra(EXTRA_MES_SELECCIONADO, mesSeleccionado);
                startActivity(intent);
            }
        });

        /*************************/

        // Obtener el país del usuario
        TelephonyManager tm = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        String countryCodeValue = tm.getNetworkCountryIso().toUpperCase();

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
        DateFormat dateFormat = new SimpleDateFormat("MM");
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
                    // Formar la lista de alimentos
                    // TODO Buscar alguna forma de hacerlo sin el for
                    for(DocumentSnapshot document : task.getResult()) {
                        Alimento alimento = new Alimento((String)document.get("nombre"), (ArrayList<Long>) document.get("calidades"));
                        alimentos.add(alimento);

                        ordenarAlimentos();
                        mostrarAlimentos();
                    }
                } else {
                    // TODO tratar excepción
                    System.out.println("### ERROR OBTENIENDO LA COLECCIÓN!! : " + task.getException());
                }
            }
        });
    }

    private void ordenarAlimentos() {
        Comparator<Alimento> comparador = Alimento.getComparator(mesSeleccionado);
        Collections.sort(alimentos, comparador);
    }

    private void mostrarAlimentos() {
        ListaAlimentos listaAlimentos = new ListaAlimentos(activity, alimentos, mesSeleccionado);
        gridview.setAdapter(listaAlimentos);
    }

    /**
     * Modifica el mes que se muestra en pantalla.
     */
    private void modificarTextoMesActual() {
        final TextView textViewToChange = (TextView) findViewById(R.id.texto_mes);

        switch (mesSeleccionado) {
            case 1:
                textViewToChange.setText(R.string.enero);
                break;
            case 2:
                textViewToChange.setText(R.string.febrero);
                break;
            case 3:
                textViewToChange.setText(R.string.marzo);
                break;
            case 4:
                textViewToChange.setText(R.string.abril);
                break;
            case 5:
                textViewToChange.setText(R.string.mayo);
                break;
            case 6:
                textViewToChange.setText(R.string.junio);
                break;
            case 7:
                textViewToChange.setText(R.string.julio);
                break;
            case 8:
                textViewToChange.setText(R.string.agosto);
                break;
            case 9:
                textViewToChange.setText(R.string.septiembre);
                break;
            case 10:
                textViewToChange.setText(R.string.octubre);
                break;
            case 11:
                textViewToChange.setText(R.string.noviembre);
                break;
            case 12:
                textViewToChange.setText(R.string.diciembre);
                break;
        }
    }

    public int getMesSeleccionado() { return mesSeleccionado; }
    public void setMesSeleccionado(int mesSeleccionado) { this.mesSeleccionado = mesSeleccionado; }

}
