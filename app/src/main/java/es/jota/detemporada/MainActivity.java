package es.jota.detemporada;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

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
    public static final String EXTRA_MES_SELECCIONADO = "es.jota.detemporada.MESSELECCIONADO";

    Activity activity;
    CollectionReference coleccionAlimentosPais;
    ArrayList<Alimento> alimentos = new ArrayList<Alimento>();
    int mesSeleccionado;
    private Toolbar toolbar;
    private GridView gridview;

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

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        int recursoNombre = getResources().getIdentifier("mes_" + mesSeleccionado, "string", MainActivity.class.getPackage().getName());
        toolbar.setTitle(recursoNombre);

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

        enviarAlimentosAlFragment();

        obtenerAlimentosPais();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            System.out.println("*** getItem: " + position);

            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(/*position + 1, alimentos*/);
        }

        @Override
        public int getCount() {
            return 12;
        }

        //this is called when notifyDataSetChanged() is called
        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
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
        coleccionAlimentosPais.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                alimentos.clear();

                if(task.isSuccessful()) {
                    for(DocumentSnapshot documentoAlimento : task.getResult()) {
                        alimentos.add(documentoAlimento.toObject(Alimento.class));
                    }

                    ordenarAlimentos();
                    mostrarAlimentos();
                } else {
                    Log.w(TAG, "obtenerAlimentosPais: No se pudo obtener la lista de alimentos por pais.");
                }
            }
        });
    }

    private void enviarAlimentosAlFragment() {
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager()));
        mViewPager.setCurrentItem(mesSeleccionado - 1);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                System.out.println("*** onPageSelected: " + position);
                mesSeleccionado = position + 1;
                int recursoNombre = getResources().getIdentifier("mes_" + mesSeleccionado, "string", getPackageName());
                toolbar.setTitle(recursoNombre);

                mViewPager.getAdapter().notifyDataSetChanged();

                ordenarAlimentos();
                mostrarAlimentos();
            }

            @Override
            public void onPageScrollStateChanged(int state) { }
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
     *
     */
    private void mostrarAlimentos() {
        System.out.println("*********** mostrarAlimentos: " + mesSeleccionado);

        // Definimos la acción a realizar cuando se selecciona un alimento de la lista
        gridview = (GridView) findViewById(R.id.gridview);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ScrollingActivity.class);
                intent.putExtra(EXTRA_ALIMENTO_SELECCIONADO, alimentos.get(position));
                intent.putExtra(EXTRA_MES_SELECCIONADO, mesSeleccionado);
                startActivity(intent);
            }
        });

        ListaAlimentos listaAlimentos = new ListaAlimentos(MainActivity.this, alimentos, mesSeleccionado);
        gridview.setAdapter(listaAlimentos);
    }
}
