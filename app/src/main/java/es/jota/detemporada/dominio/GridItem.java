package es.jota.detemporada.dominio;

/**
 * @author Filippo Ash
 * @version 1.0.0
 * @date 11/7/2015
 */
public class GridItem extends Item {

    private int mPosition;
    private String mSubTitle;

    public GridItem(Alimento alimento, int position) {
        super(alimento.getNombre(), alimento);
        mPosition = position;
    }

    @Override
    public int getItemType() {
        return GRID_ITEM_TYPE;
    }

    public int getPosition() {
        return mPosition;
    }
}