public class Camion{
    private final int id;
    private final String patente;
    private final boolean estaRefrigerado;
    private final int capacidadKg;

    public Camion(int id, String patente, boolean estaRefrigerado, int capacidadKg) {
        this.id = id;
        this.patente = patente;
        this.estaRefrigerado = estaRefrigerado;
        this.capacidadKg = capacidadKg;
    }

    public int getId(){ 
        return id;
    }

    public String getPatente(){ 
        return patente;
    }

    public boolean isEstaRefrigerado(){ 
        return estaRefrigerado; 
    }

    public int getCapacidadKg(){ 
        return capacidadKg; 
    }
}
