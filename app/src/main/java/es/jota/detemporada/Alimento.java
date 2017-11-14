package es.jota.detemporada;

import java.util.Comparator;

public class Alimento {
    int id;
    String nombre;

    int mes01, mes02, mes03, mes04, mes05, mes06, mes07, mes08, mes09, mes10, mes11, mes12;

    public Alimento() {}

    public Alimento(int i, String nombre) {
        super();
        this.id = i;
        this.nombre = nombre;
    }

    public static Comparator<Alimento> getComparator(int mesComparacion) {
        return new AlimentoComparator(mesComparacion);
    }

    private static class AlimentoComparator implements Comparator<Alimento> {
        private int mesComparacion;

        private AlimentoComparator(int mesComparacion) {
            this.mesComparacion = mesComparacion;
        }

        public int compare(Alimento a1, Alimento a2) {
            int comparacion = 0;

            switch (mesComparacion) {
                case 1:
                    comparacion = a2.getMes01() - a1.getMes01();
                    break;
                case 2:
                    comparacion = a2.getMes02() - a1.getMes02();
                    break;
                case 3:
                    comparacion = a2.getMes03() - a1.getMes03();
                    break;
                case 4:
                    comparacion = a2.getMes04() - a1.getMes04();
                    break;
                case 5:
                    comparacion = a2.getMes05() - a1.getMes05();
                    break;
                case 6:
                    comparacion = a2.getMes06() - a1.getMes06();
                    break;
                case 7:
                    comparacion = a2.getMes07() - a1.getMes07();
                    break;
                case 8:
                    comparacion = a2.getMes08() - a1.getMes08();
                    break;
                case 9:
                    comparacion = a2.getMes09() - a1.getMes09();
                    break;
                case 10:
                    comparacion = a2.getMes10() - a1.getMes10();
                    break;
                case 11:
                    comparacion = a2.getMes11() - a1.getMes11();
                    break;
                case 12:
                    comparacion = a2.getMes12() - a1.getMes12();
                    break;
            }

            if(comparacion == 0) {
                comparacion = a1.getNombre().compareTo(a2.getNombre());
            }

            return comparacion;
        }
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getMes01() {
        return mes01;
    }

    public void setMes01(int mes01) {
        this.mes01 = mes01;
    }

    public int getMes02() {
        return mes02;
    }

    public void setMes02(int mes02) {
        this.mes02 = mes02;
    }

    public int getMes03() {
        return mes03;
    }

    public void setMes03(int mes03) {
        this.mes03 = mes03;
    }

    public int getMes04() {
        return mes04;
    }

    public void setMes04(int mes04) {
        this.mes04 = mes04;
    }

    public int getMes05() {
        return mes05;
    }

    public void setMes05(int mes05) {
        this.mes05 = mes05;
    }

    public int getMes06() {
        return mes06;
    }

    public void setMes06(int mes06) {
        this.mes06 = mes06;
    }

    public int getMes07() {
        return mes07;
    }

    public void setMes07(int mes07) {
        this.mes07 = mes07;
    }

    public int getMes08() {
        return mes08;
    }

    public void setMes08(int mes08) {
        this.mes08 = mes08;
    }

    public int getMes09() {
        return mes09;
    }

    public void setMes09(int mes09) {
        this.mes09 = mes09;
    }

    public int getMes10() {
        return mes10;
    }

    public void setMes10(int mes10) {
        this.mes10 = mes10;
    }

    public int getMes11() {
        return mes11;
    }

    public void setMes11(int mes11) {
        this.mes11 = mes11;
    }

    public int getMes12() {
        return mes12;
    }

    public void setMes12(int mes12) {
        this.mes12 = mes12;
    }

}
