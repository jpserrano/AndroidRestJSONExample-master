package es.jota.detemporada.dominio;


public class GridItem extends Item {

    public GridItem(Alimento alimento) {
        super(alimento.getNombre(), alimento);
    }

    @Override
    public int getItemType() {
        return GRID_ITEM_TYPE;
    }
}