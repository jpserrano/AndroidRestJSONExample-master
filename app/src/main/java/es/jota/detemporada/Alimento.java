package es.jota.detemporada;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Alimento implements Serializable {

    private Long id;
    private String nombre;
    private List<Long> calidades;

    public Alimento() {}

    static Comparator<Alimento> getComparator(int mesComparacion) {
        return new AlimentoComparator(mesComparacion);
    }

    private static class AlimentoComparator implements Comparator<Alimento> {
        private int mesComparacion;

        private AlimentoComparator(int mesComparacion) {
            this.mesComparacion = mesComparacion;
        }

        /**
         * Compara dos alimentos para su posterior ordenación.
         * Primero compara las calidades para el mes seleccionado, a mayor calidad primero.
         * Si las dos calidades son iguales ordena por nombre descendente.
         *
         * @param a1 Primer alimento.
         * @param a2 Segundo alimento.
         * @return Resultado de la comparación.
         */
        public int compare(Alimento a1, Alimento a2) {
            int comparacion = Long.valueOf(a2.getCalidades().get(mesComparacion - 1) - a1.getCalidades().get(mesComparacion - 1)).intValue();

            if(comparacion == 0) {
                comparacion = a1.getNombre().compareTo(a2.getNombre());
            }

            return comparacion;
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    String getNombre() { return nombre; }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    List<Long> getCalidades() { return calidades; }
    public void setCalidades(List<Long> calidades) { this.calidades = calidades; }
}
