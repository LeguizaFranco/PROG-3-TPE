public class Paquete implements Comparable<Paquete> {
    private final int id;
    private final String codigo;
    private final int peso;
    private final boolean contieneAlimentos;
    private final int nivelUrgencia;

    public Paquete(int id, String codigo, int peso, boolean contieneAlimentos, int nivelUrgencia) {
        this.id = id;
        this.codigo = codigo;
        this.peso = peso;
        this.contieneAlimentos = contieneAlimentos;
        this.nivelUrgencia = nivelUrgencia;
    }

    public int getId(){ 
        return id; 
    }

    public String getCodigo(){ 
        return codigo; 
    }

    public int getPeso(){ 
        return peso; 
    }

    public boolean isContieneAlimentos(){ 
        return contieneAlimentos; 
    }

    public int getNivelUrgencia(){ 
        return nivelUrgencia;
    }

    @Override
    public int compareTo(Paquete otro) {
        return Integer.compare(otro.getPeso(), this.getPeso());
    }
}