package es.jota.detemporada.dominio;


public class GridItem extends Item {

    public GridItem(Alimento alimento, Long calidadMes) {
        super(alimento.getNombre(), alimento, calidadMes);
    }

    @Override
    public int getItemType() {
        return GRID_ITEM_TYPE;
    }
}