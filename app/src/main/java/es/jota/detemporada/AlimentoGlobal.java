package es.jota.detemporada;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AlimentoGlobal implements Serializable {
    String nombre;

    Long calorias;
    Double carbohidratos, proteinas, grasas;

    public AlimentoGlobal() {}

    public AlimentoGlobal(String nombre) {
        super();
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getCalorias() { return calorias; }
    public void setCalorias(Long calorias) { this.calorias = calorias; }

    public Double getCarbohidratos() { return carbohidratos; }
    public void setCarbohidratos(Double carbohidratos) { this.carbohidratos = carbohidratos; }

    public Double getProteinas() { return proteinas; }
    public void setProteinas(Double proteinas) { this.proteinas = proteinas; }

    public Double getGrasas() { return grasas; }
    public void setGrasas(Double grasas) { this.grasas = grasas; }
}
