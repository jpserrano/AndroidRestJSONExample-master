package es.jota.detemporada;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    // The fragment argument representing the section number for this fragment
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_ALIMENTOS = "alimentos";

    public static final String EXTRA_ALIMENTO_SELECCIONADO = "es.jota.detemporada.ALIMENTOSELECCIONADO";
    public static final String EXTRA_MES_SELECCIONADO = "es.jota.detemporada.MESELECCIONADO";

    int mesSeleccionado;
    GridView gridview;
    List<Alimento> alimentos = new ArrayList<>();

    public PlaceholderFragment() { }

    /**
     * Returns a new instance of this fragment for the given section number.
     */
    public static PlaceholderFragment newInstance(int sectionNumber, ArrayList<Alimento> alimentos) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putSerializable(ARG_ALIMENTOS, alimentos);
        fragment.setArguments(args);

        System.out.println("### newInstance");

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_alimentos, container, false);
        mesSeleccionado = getArguments().getInt(ARG_SECTION_NUMBER);
        alimentos = (ArrayList<Alimento>)getArguments().getSerializable(ARG_ALIMENTOS);

        System.out.println("### onCreateView");

        // Definimos la acción a realizar cuando se selecciona un alimento de la lista
        gridview = (GridView) rootView.findViewById(R.id.gridview);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent intent = new Intent(getContext(), AlimentoActivity.class);
                intent.putExtra(EXTRA_ALIMENTO_SELECCIONADO, alimentos.get(position));
                intent.putExtra(EXTRA_MES_SELECCIONADO, mesSeleccionado);
                startActivity(intent);
            }
        });

        modificarTextoMesActual(rootView);
        ordenarAlimentos();
        mostrarAlimentos();

        return rootView;
    }

    /**
     * Modifica el mes que se muestra en pantalla.
     */
    private void modificarTextoMesActual(View rootView) {
        final TextView textViewToChange = (TextView) rootView.findViewById(R.id.texto_mes);
        int recursoNombre = getResources().getIdentifier("mes_" + mesSeleccionado, "string", MainActivity.class.getPackage().getName());
        textViewToChange.setText(recursoNombre);
    }

    /**
     * Ordena la lista de alimentos en función de la calidad para el mes seleccionado y el nombre del alimento.
     */
    private void ordenarAlimentos() {
        System.out.println("### ALIMENTOS: " + alimentos);
        if(alimentos != null) {
            System.out.println("### TAMAÑO: " + alimentos.size());
        }

        Comparator<Alimento> comparador = Alimento.getComparator(mesSeleccionado);
        Collections.sort(alimentos, comparador);
    }

    /**
     * Muestra la lista de alimentos ordenada en la vista.
     */
    private void mostrarAlimentos() {
        System.out.println("### MOSTRAR ALIMENTOS");
        ListaAlimentos listaAlimentos = new ListaAlimentos(getActivity(), alimentos, mesSeleccionado);
        gridview.setAdapter(listaAlimentos);
    }
}
