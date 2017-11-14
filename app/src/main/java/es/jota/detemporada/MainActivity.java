package es.jota.detemporada;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    Activity activity;
    CollectionReference coleccionAlimentosPais;
    ArrayList<Alimento> alimentos = new ArrayList<>();
    ListView listView;

    int mesSeleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;

        // Calcular el mes actual
        mesSeleccionado = calcularMesActual();
        modificarTextoMesActual();

        listView = (ListView) findViewById(android.R.id.list);

        // Obtener el país del usuario
        TelephonyManager tm = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        String countryCodeValue = tm.getNetworkCountryIso().toUpperCase();

        // Access a Cloud Firestore instance from your Activity
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
                obtenerAlimentosPais();
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
                obtenerAlimentosPais();
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
        final String mesFormateado = formatearMesSeleccionado();

        coleccionAlimentosPais.orderBy("mes" + mesFormateado, Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                alimentos.clear();

                if(task.isSuccessful()) {
                    for(DocumentSnapshot document : task.getResult()) {
                        Alimento alimento = new Alimento(((Long)document.get("mes" + mesFormateado)).intValue(), (String)document.get("nombre"));
                        alimentos.add(alimento);

                        ListaAlimentos listaAlimentos = new ListaAlimentos(activity, alimentos);
                        listView.setAdapter(listaAlimentos);
                    }
                } else {
                    System.out.println("### ERROR OBTENIENDO LA COLECCIÓN!! : " + task.getException());
                }
            }
        });
    }

    /**
     * Devuelve un string con el mes seleccionado en formato dos caracteres, añadiendo un 0 en los meses inferiores al 10.
     * Esto se hace porque en la BD los campos de calidades del mes son del estilo mes01, mes02...
     *
     * @return String con el mes formateado.
     */
    private String formatearMesSeleccionado() {
        String resultado = "";

        if(mesSeleccionado >= 10) {
            resultado = mesSeleccionado + "";
        } else {
            resultado = "0" + mesSeleccionado;
        }

        return resultado;
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

