package es.jota.detemporada.dominio;

/**
 * @author Filippo Ash
 * @version 1.0.0
 * @date 11/7/2015
 */
public class HeaderItem extends Item {

    public HeaderItem(String title) {
        super(title, null);
    }

    @Override
    public int getItemType() {
        return HEADER_ITEM_TYPE;
    }
}
