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

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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

    private void bindGridItem(Holder holder, int position) {

        final View container = holder.itemView;

        final GridItem item = (GridItem) mItemList.get(position);
        final Alimento alimento = item.getAlimento();
        final String nombreAlimento = alimento.getNombre();

        // Si en el strings no está definido el nombre del alimento mostramos el nombre desde el objeto en BD
        TextView title = (TextView) container.findViewById(R.id.gridTitle);
        int recursoNombre = container.getResources().getIdentifier("alim_" + nombreAlimento, "string", container.getContext().getPackageName());
        if (recursoNombre == 0) {
            title.setText(nombreAlimento);
            Log.w(TAG, "getView: el alimento '" + nombreAlimento + "' no está definido en el fichero string");
        } else {
            title.setText(recursoNombre);
        }

        // Añadimos el círculo al lado del nombre del alimento
        // con el color que le corresponde por la calidad que tiene en el mes seleccionado
        int circuloCalidadAlimento = R.drawable.ic_circle_10;
        if(item.getCalidadMes() == 0) {
            circuloCalidadAlimento = R.drawable.ic_circle_0;
        } else if(item.getCalidadMes() == 5) {
            circuloCalidadAlimento = R.drawable.ic_circle_5;
        }

        title.setCompoundDrawablesRelativeWithIntrinsicBounds(circuloCalidadAlimento, 0, 0, 0);
        title.setCompoundDrawablePadding(8);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReferenceFromUrl("gs://de-temporada.appspot.com/img/alimentos/" + nombreAlimento + ".jpg");

        ImageView imageView = (ImageView) container.findViewById(R.id.imagen_alimento_pequenya);
        Glide.with(container.getContext()).using(new FirebaseImageLoader()).load(storageReference).into(imageView);

        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ScrollingActivity.class);
                intent.putExtra(EXTRA_ALIMENTO_SELECCIONADO, alimento);
                view.getContext().startActivity(intent);
            }
        });
    }

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

    public void addItem(Item item) {
        mItemList.add(item);
        notifyDataSetChanged();
    }
}
