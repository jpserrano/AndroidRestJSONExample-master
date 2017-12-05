package es.jota.detemporada;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

public class ListaAlimentos extends BaseAdapter {

    private static final String TAG = ListaAlimentos.class.getName();

    private Activity context;
    private List<Alimento> alimentos;
    private int mesSeleccionado;

    ListaAlimentos(Activity context, List<Alimento> alimentos, int mesSeleccionado) {
        this.context = context;
        this.alimentos = alimentos;
        this.mesSeleccionado = mesSeleccionado;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if(convertView == null) {
            LayoutInflater layoutInflater = context.getLayoutInflater();
            view = layoutInflater.inflate(R.layout.row_item, parent, false);
        } else {
            view = convertView;
        }

        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        TextView textView = (TextView) view.findViewById(R.id.textViewNombre);
        RatingBar ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);

        String nombreAlimento = alimentos.get(position).getNombre();

        // Si en el strings no está definido el nombre del alimento mostramos el nombre desde el objeto en BD
        int recursoNombre = context.getResources().getIdentifier(nombreAlimento, "string", context.getPackageName());
        if(recursoNombre == 0) {
            textView.setText(nombreAlimento);
            Log.w(TAG, "getView: el alimento '" + nombreAlimento + "' no está definido en el fichero string");
        } else {
            textView.setText(recursoNombre);
        }

        ratingBar.setRating((alimentos.get(position).getCalidades().get(mesSeleccionado - 1)) / 2);

        // Si no existe la imagen del alimento mostramos una imagen genérica para que la interfaz no se descuadre
        int recursoImagen = context.getResources().getIdentifier("img_" + nombreAlimento, "drawable", context.getPackageName());
        if(recursoImagen == 0) {
            imageView.setImageResource(R.drawable.img_no_foto);
            Log.w(TAG, "getView: el alimento '" + nombreAlimento + "' no tiene la imagen asociada");
        } else {
            imageView.setImageResource(recursoImagen);
        }

        return view;
    }

    public long getItemId(int position) {
        return position;
    }

    public Object getItem(int position) {
        return position;
    }

    public int getCount() {

        if(alimentos.size() <= 0) {
            return 1;
        }

        return alimentos.size();
    }
}
