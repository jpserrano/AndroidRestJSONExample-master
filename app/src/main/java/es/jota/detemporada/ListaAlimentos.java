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

import java.util.ArrayList;
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

    public static class ViewHolder {
        ImageView imageView;
        TextView textViewNombre;
        RatingBar ratingBar;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        LayoutInflater inflater = context.getLayoutInflater();
        ViewHolder vh;
        if(convertView == null) {
            vh = new ViewHolder();
            row = inflater.inflate(R.layout.row_item, parent, false);
            vh.imageView = (ImageView) row.findViewById(R.id.imageView);
            vh.textViewNombre = (TextView) row.findViewById(R.id.textViewNombre);
            vh.ratingBar = (RatingBar) row.findViewById(R.id.ratingBar);
            row.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        String nombreAlimento = alimentos.get(position).getNombre();

        // Si en el strings no está definido el nombre del alimento mostramos el nombre desde el objeto en BD
        int recursoNombre = context.getResources().getIdentifier(nombreAlimento, "string", context.getPackageName());
        if(recursoNombre == 0) {
            vh.textViewNombre.setText(nombreAlimento);
            Log.w(TAG, "getView: el alimento '" + nombreAlimento + "' no está definido en el fichero string");
        } else {
            vh.textViewNombre.setText(recursoNombre);
        }

        vh.ratingBar.setRating((alimentos.get(position).getCalidades().get(mesSeleccionado - 1)) / 2);

        // Si no existe la imagen del alimento mostramos una imagen genérica para que la interfaz no se descuadre
        int recursoImagen = context.getResources().getIdentifier("img_" + nombreAlimento, "drawable", context.getPackageName());
        if(recursoImagen == 0) {
            vh.imageView.setImageResource(R.drawable.img_no_foto);
            Log.w(TAG, "getView: el alimento '" + nombreAlimento + "' no no tiene la imagen asociada");
        } else {
            vh.imageView.setImageResource(recursoImagen);
        }

        return  row;
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
