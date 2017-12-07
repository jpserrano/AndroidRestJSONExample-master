package es.jota.detemporada.adapter;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import es.jota.detemporada.R;
import es.jota.detemporada.ScrollingActivity;
import es.jota.detemporada.dominio.Alimento;
import es.jota.detemporada.dominio.GridItem;
import es.jota.detemporada.dominio.Item;
import es.jota.detemporada.view.Holder;

import java.util.List;


public class GridListAdapter extends RecyclerView.Adapter<Holder> {

    private static final String TAG = GridListAdapter.class.getName();
    private static final String EXTRA_ALIMENTO_SELECCIONADO = "es.jota.detemporada.ALIMENTOSELECCIONADO";

    private final int mDefaultSpanCount;
    private List<Item> mItemList;

    public GridListAdapter(List<Item> itemList, GridLayoutManager gridLayoutManager, int defaultSpanCount) {
        mItemList = itemList;
        mDefaultSpanCount = defaultSpanCount;
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return isHeaderType(position) ? mDefaultSpanCount : 1;
            }
        });
    }

    private boolean isHeaderType(int position) {
        return mItemList.get(position).getItemType() == Item.HEADER_ITEM_TYPE;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view;

        if(viewType == 0) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.header_type_layout, viewGroup, false);
        } else {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.grid_type_layout, viewGroup, false);
        }

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        if(isHeaderType(position)) {
            bindHeaderItem(holder, position);
        } else {
            bindGridItem(holder, position);
        }
    }

    /**
     * This method is used to bind grid item value
     *
     * @param holder
     * @param position
     */
    private void bindGridItem(Holder holder, int position) {

        final View container = holder.itemView;

        final GridItem item = (GridItem) mItemList.get(position);
        final Alimento alimento = item.getAlimento();
        final String nombreAlimento = alimento.getNombre();

        // Si en el strings no está definido el nombre del alimento mostramos el nombre desde el objeto en BD
        TextView title = (TextView) container.findViewById(R.id.gridTitle);
        int recursoNombre = container.getResources().getIdentifier(nombreAlimento, "string", container.getContext().getPackageName());
        if (recursoNombre == 0) {
            title.setText(nombreAlimento);
            Log.w(TAG, "getView: el alimento '" + nombreAlimento + "' no está definido en el fichero string");
        } else {
            title.setText(recursoNombre);
        }

        // Si no existe la imagen del alimento mostramos una imagen genérica para que la interfaz no se descuadre
        ImageView imageView = (ImageView) container.findViewById(R.id.imagen_alimento_pequenya);
        int recursoImagen = container.getResources().getIdentifier("img_" + nombreAlimento, "drawable", container.getContext().getPackageName());
        if(recursoImagen == 0) {
            imageView.setImageResource(R.drawable.img_no_foto);
            Log.w(TAG, "getView: el alimento '" + nombreAlimento + "' no tiene la imagen asociada");
        } else {
            imageView.setImageResource(recursoImagen);
        }

        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ScrollingActivity.class);
                intent.putExtra(EXTRA_ALIMENTO_SELECCIONADO, alimento);
                view.getContext().startActivity(intent);
            }
        });
    }

    /**
     * This method is used to bind the header with the corresponding item position information
     *
     * @param holder
     * @param position
     */
    private void bindHeaderItem(Holder holder, int position) {
        TextView title = (TextView) holder.itemView.findViewById(R.id.headerTitle);
        title.setText(mItemList.get(position).getItemTitle());
    }

    @Override
    public int getItemViewType(int position) {
        return mItemList.get(position).getItemType() == Item.HEADER_ITEM_TYPE ? 0 : 1;
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    /**
     * This method is used to add an item into the recyclerview list
     *
     * @param item
     */
    public void addItem(Item item) {
        mItemList.add(item);
        notifyDataSetChanged();
    }

    /**
     * This method is used to remove items from the list
     *
     * @param item {@link Item}
     */
    public void removeItem(Item item) {
        mItemList.remove(item);
        notifyDataSetChanged();
    }
}
