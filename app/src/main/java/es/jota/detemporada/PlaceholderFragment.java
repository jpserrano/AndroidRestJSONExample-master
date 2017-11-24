package es.jota.detemporada;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    // The fragment argument representing the section number for this fragment
    private static final String ARG_SECTION_NUMBER = "section_number";

    int mesSeleccionado;

    public PlaceholderFragment() { }

    /**
     * Returns a new instance of this fragment for the given section number.
     */
    public static PlaceholderFragment newInstance(int sectionNumber) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);

        System.out.println("### newInstance");

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_alimentos, container, false);
        mesSeleccionado = getArguments().getInt(ARG_SECTION_NUMBER);
        modificarTextoMesActual(rootView);

        System.out.println("### onCreateView");


        return rootView;
    }

    /**
     * Modifica el mes que se muestra en pantalla.
     */
    private void modificarTextoMesActual( View rootView) {
        final TextView textViewToChange = (TextView) rootView.findViewById(R.id.texto_mes);
        int recursoNombre = getResources().getIdentifier("mes_" + mesSeleccionado, "string", MainActivity.class.getPackage().getName());
        textViewToChange.setText(recursoNombre);
    }
}
