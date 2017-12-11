package es.jota.detemporada.dominio;


public class HeaderItem extends Item {

    public HeaderItem(String title) {
        // TODO buscar otra forma, que sea solo el title y pasar el alimento y la calidad por setters al GridItem
        super(title, null, null);
    }

    @Override
    public int getItemType() {
        return HEADER_ITEM_TYPE;
    }
}
