package es.jota.detemporada;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListaAlimentos extends BaseAdapter {


    private Activity context;
    private ArrayList<Alimento> alimentos;


    public ListaAlimentos(Activity context, ArrayList<Alimento> alimentos) {
        this.context = context;
        this.alimentos = alimentos;

    }

    public static class ViewHolder {
        TextView textViewId;
        TextView textViewNombre;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        LayoutInflater inflater = context.getLayoutInflater();
        ViewHolder vh;
        if(convertView == null) {
            vh = new ViewHolder();
            row = inflater.inflate(R.layout.row_item, null, true);
            vh.textViewId = (TextView) row.findViewById(R.id.textViewId);
            vh.textViewNombre = (TextView) row.findViewById(R.id.textViewNombre);
            // store the holder with the view.
            row.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        vh.textViewNombre.setText(alimentos.get(position).getNombre());
        vh.textViewId.setText("" + alimentos.get(position).getId());

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

