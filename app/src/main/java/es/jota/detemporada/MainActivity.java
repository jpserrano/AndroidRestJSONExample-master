package es.jota.detemporada;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import es.jota.detemporada.es.jota.detemporada.dominio.Alimento;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    public static final String EXTRA_ALIMENTO_SELECCIONADO = "es.jota.detemporada.ALIMENTOSELECCIONADO";
    public static final String EXTRA_MES_SELECCIONADO = "es.jota.detemporada.MESSELECCIONADO";

    int mesSeleccionado;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtener el país del usuario
        TelephonyManager tm = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        String countryCodeValue;
        if (tm != null) {
            countryCodeValue = tm.getNetworkCountryIso().toUpperCase();
        } else {
            // TODO mostrar un selector de paises disponibles cuando haya más de un país
            countryCodeValue = "ES";
        }

        mesSeleccionado = calcularMesActual();

        // Acceso a la BD de Cloud Firestore
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        CollectionReference coleccionAlimentosPais = database.collection("alimentos_" + countryCodeValue);
        coleccionAlimentosPais.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    ArrayList<Alimento> alimentos = new ArrayList<>();

                    for(DocumentSnapshot documentoAlimento : task.getResult()) {
                        alimentos.add(documentoAlimento.toObject(Alimento.class));
                    }

                    // Lo hacemos una vez se han obtenido los alimentos
                    crearAdaptador(alimentos);
                } else {
                    Log.w(TAG, "obtenerAlimentosPais: No se pudo obtener la lista de alimentos por pais.");
                }
            }
        });

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        int recursoNombre = getResources().getIdentifier("mes_" + mesSeleccionado, "string", getPackageName());
        toolbar.setTitle(recursoNombre);
    }

    private void crearAdaptador(ArrayList<Alimento> alimentos) {
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), alimentos);
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(mesSeleccionado - 1);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                mesSeleccionado = position + 1;
                int recursoNombre = getResources().getIdentifier("mes_" + mesSeleccionado, "string", getPackageName());
                toolbar.setTitle(recursoNombre);
            }

            @Override
            public void onPageScrollStateChanged(int state) { }
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
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String ARG_ALIMENTOS = "alimentos";

        public PlaceholderFragment() { }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int mesSeleccionado, ArrayList<Alimento> alimentos) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, mesSeleccionado);
            args.putSerializable(ARG_ALIMENTOS, alimentos);
            fragment.setArguments(args);

            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final ArrayList<Alimento> alimentos = (ArrayList<Alimento>)getArguments().getSerializable(ARG_ALIMENTOS);
            final int mesSeleccionado = getArguments().getInt(ARG_SECTION_NUMBER);
            final ListaAlimentos listaAlimentos = new ListaAlimentos(getActivity(), alimentos, mesSeleccionado);

            View rootView = inflater.inflate(R.layout.fragment_alimentos, container, false);
            GridView gridview = (GridView) rootView.findViewById(R.id.gridview);
            gridview.setAdapter(listaAlimentos);

            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    Intent intent = new Intent(getContext(), ScrollingActivity.class);
                    intent.putExtra(EXTRA_ALIMENTO_SELECCIONADO, alimentos.get(position));
                    intent.putExtra(EXTRA_MES_SELECCIONADO, mesSeleccionado);
                    startActivity(intent);
                }
            });

            return rootView;
        }
    }


    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        private final ArrayList<Alimento> alimentosAdapter;

        public SectionsPagerAdapter(FragmentManager fm, ArrayList<Alimento> alimentos) {
            super(fm);
            alimentosAdapter = alimentos;
        }

        @Override
        public Fragment getItem(int position) {
            ordenarAlimentos(position + 1);
            ArrayList<Alimento> alimentosTemp = (ArrayList<Alimento>)alimentosAdapter.clone();

            return PlaceholderFragment.newInstance(position + 1, alimentosTemp);
        }

        @Override
        public int getCount() {
            return 12;
        }

        /**
         * Ordena la lista de alimentos en función de la calidad para el mes seleccionado y el nombre del alimento.
         */
        private void ordenarAlimentos(int mes) {
            Comparator<Alimento> comparador = Alimento.getComparator(mes);
            Collections.sort(alimentosAdapter, comparador);
        }
    }
}
