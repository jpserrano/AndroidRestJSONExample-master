package es.jota.detemporada.dominio;

/**
 * @author Filippo Ash
 * @version 1.0.0
 * @date 11/7/2015
 */
public abstract class Item {

    public static final int HEADER_ITEM_TYPE = 0;
    public static final int GRID_ITEM_TYPE = 1;

    private String mItemTitle;
    private Alimento alimento;

    public Item(String mItemTitle, Alimento alimento) {
        this.mItemTitle = mItemTitle;
        this.alimento = alimento;
    }

    public String getItemTitle() {
        return mItemTitle;
    }
    public Alimento getAlimento() { return alimento; }

    public abstract int getItemType();
}
