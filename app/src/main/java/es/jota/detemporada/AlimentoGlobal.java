package es.jota.detemporada;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AlimentoGlobal implements Serializable {

    private String nombre;

    private Long calorias;
    private Double carbohidratos, proteinas, grasas;

    public AlimentoGlobal() {}

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    Long getCalorias() { return calorias; }
    public void setCalorias(Long calorias) { this.calorias = calorias; }

    Double getCarbohidratos() { return carbohidratos; }
    public void setCarbohidratos(Double carbohidratos) { this.carbohidratos = carbohidratos; }

    Double getProteinas() { return proteinas; }
    public void setProteinas(Double proteinas) { this.proteinas = proteinas; }

    Double getGrasas() { return grasas; }
    public void setGrasas(Double grasas) { this.grasas = grasas; }
}
