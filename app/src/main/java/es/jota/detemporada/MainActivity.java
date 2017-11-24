package es.jota.detemporada;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    Activity activity;
    CollectionReference coleccionAlimentosPais;
    ArrayList<Alimento> alimentos = new ArrayList<>();
    int mesSeleccionado;


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;

        // Calcular el mes actual
        mesSeleccionado = calcularMesActual();

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
        /*final Button botonMesMenos = (Button)findViewById(R.id.boton_mes_menos);
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
        });*/

        /*final Button botonMesMas = (Button)findViewById(R.id.boton_mes_mas);
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
        });*/
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1, alimentos);
        }

        @Override
        public int getCount() {
            return 12;
        }
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

        System.out.println("### Obtener alimentos pais");

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

                    enviarAlimentosAlFragment();
                } else {
                    Log.w(TAG, "obtenerAlimentosPais: No se pudo obtener la lista de alimentos por pais.");
                }
            }
        });
    }

    private void enviarAlimentosAlFragment() {
        System.out.println("### Enviar ALIMENTOS: " + alimentos);

        if(alimentos != null) {
            System.out.println("### Enviar ALIMENTOS TAMAÑO: " + alimentos.size());
        }

       /* Bundle bundle = new Bundle();
        bundle.putSerializable("alimentos", alimentos);
        PlaceholderFragment fragment = new PlaceholderFragment();
        fragment.setArguments(bundle);*/


        // Pintamos la pantalla
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(mesSeleccionado - 1);
    }

    public int getMesSeleccionado() { return mesSeleccionado; }
    public void setMesSeleccionado(int mesSeleccionado) { this.mesSeleccionado = mesSeleccionado; }

}
