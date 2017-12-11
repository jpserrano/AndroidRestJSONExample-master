package es.jota.detemporada.dominio;


public abstract class Item {

    public static final int HEADER_ITEM_TYPE = 0;
    public static final int GRID_ITEM_TYPE = 1;

    private String mItemTitle;
    private Alimento alimento;
    private Long calidadMes;

    public Item(String mItemTitle, Alimento alimento, Long calidadMes) {
        this.mItemTitle = mItemTitle;
        this.alimento = alimento;
        this.calidadMes = calidadMes;
    }

    public String getItemTitle() {
        return mItemTitle;
    }
    public Alimento getAlimento() { return alimento; }
    public Long getCalidadMes() { return calidadMes; }

    public abstract int getItemType();
}
