package es.jota.detemporada;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

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

import es.jota.detemporada.adapter.GridListAdapter;
import es.jota.detemporada.dominio.Alimento;
import es.jota.detemporada.dominio.GridItem;
import es.jota.detemporada.dominio.HeaderItem;
import es.jota.detemporada.dominio.Item;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

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
                    Alimento alimento;
                    int recursoNombre;

                    for(DocumentSnapshot documentoAlimento : task.getResult()) {
                        alimento = documentoAlimento.toObject(Alimento.class);

                        // Establecemos el nombre del alimento en el idioma seleccionado
                        // para que la ordenación se aplique correctamente
                        recursoNombre = getResources().getIdentifier("alim_" + alimento.getNombre(), "string", getPackageName());
                        if(recursoNombre != 0) {
                            alimento.setNombreTraducido(getResources().getString(recursoNombre));
                        } else {
                            alimento.setNombreTraducido(alimento.getNombre());
                            Log.w(TAG, "onCreate: el alimento '" + alimento.getNombre() + "' no está definido en el fichero string");
                        }

                        alimentos.add(alimento);
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            this.startActivity(intent);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

        private static final int DEFAULT_SPAN_COUNT = 2;
        private RecyclerView mRecyclerView;
        private GridListAdapter mAdapter;
        private List<Item> mItemList = new ArrayList<>();
        private int mGridCounter;

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

            View rootView = inflater.inflate(R.layout.fragment_alimentos, container, false);
            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerViewList);
            mRecyclerView.setRecycledViewPool(new RecyclerView.RecycledViewPool());
            mRecyclerView.setHasFixedSize(true);

            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), DEFAULT_SPAN_COUNT);

            mRecyclerView.setLayoutManager(gridLayoutManager);
            mAdapter = new GridListAdapter(mItemList, gridLayoutManager, DEFAULT_SPAN_COUNT);
            mRecyclerView.setAdapter(mAdapter);

            cargarAlimentosAdapter(alimentos, mesSeleccionado);

            return rootView;
        }

        private void cargarAlimentosAdapter(ArrayList<Alimento> alimentos, int mes) {
            int categoria = 0;
            int posicionMes = mes - 1;

            for(Alimento alimento : alimentos) {
                if(alimento.getCalidades().get(posicionMes) == 11) {
                    if(categoria != 11) {
                        mAdapter.addItem(new HeaderItem(getResources().getString(R.string.categoria_11)));
                        categoria = 11;
                    }
                } else if(alimento.getCalidades().get(posicionMes) == 10) {
                    if(categoria != 10) {
                        mAdapter.addItem(new HeaderItem(getResources().getString(R.string.categoria_10)));
                        categoria = 10;
                    }
                } else if(alimento.getCalidades().get(posicionMes) == 5) {
                    if(categoria != 5) {
                        mAdapter.addItem(new HeaderItem(getResources().getString(R.string.categoria_5)));
                        categoria = 5;
                    }
                } else {
                    if(categoria != 0) {
                        mAdapter.addItem(new HeaderItem(getResources().getString(R.string.categoria_0)));
                        categoria = 0;
                    }
                }

                mAdapter.addItem(new GridItem(alimento, alimento.getCalidades().get(posicionMes)));
            }
        }
    }


    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        private final ArrayList<Alimento> alimentosAdapter;

        private SectionsPagerAdapter(FragmentManager fm, ArrayList<Alimento> alimentos) {
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
