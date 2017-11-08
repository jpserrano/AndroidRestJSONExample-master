package es.jota.detemporada;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    Activity activity;
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

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("alimentos");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                alimentos.clear();

                // This method is called once with the initial value and again whenever data at this location is updated.
                GenericTypeIndicator<List<Alimento>> genericTypeIndicator = new GenericTypeIndicator<List<Alimento>>(){};
                List<Alimento> alimentosTmp = dataSnapshot.getValue(genericTypeIndicator);

                if(alimentosTmp != null) {
                    alimentos.addAll(alimentosTmp);

                    ListaAlimentos listaAlimentos = new ListaAlimentos(activity, alimentos);
                    listView.setAdapter(listaAlimentos);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

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

