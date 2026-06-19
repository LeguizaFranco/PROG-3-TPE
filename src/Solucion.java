import java.util.*;

public class Solucion {
    public Map<Camion, List<Paquete>> asignacion;
    public int pesoNoAsignado;
    public int metricaCosto; // estados generados (back) o candidatos considerados (greedy)

    public Solucion() {
        this.asignacion = new HashMap<>();
        this.pesoNoAsignado = 0;
        this.metricaCosto = 0;
    }
}