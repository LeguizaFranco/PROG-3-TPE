import java.util.List;

public class Main {
    public static void main(String[] args) {
        String pathCamiones = "Camiones.csv";
        String pathPaquetes = "Paquetes.csv";

        Servicios sistema = new Servicios(pathCamiones, pathPaquetes);

        //servicio 1 
        System.out.println("servicio1");
        Paquete p1 = sistema.servicio1("P001");
        if (p1 != null) {
            System.out.println("id: " + p1.getId() + ", peso: " + p1.getPeso() + ", contieneAlimentos: "+ p1.isContieneAlimentos()+ ", urgencia: " + p1.getNivelUrgencia());
        } else {
            System.out.println("paquete no encontrado.");
        }
        
        Paquete pInexistente = sistema.servicio1("P999");
        System.out.println(pInexistente);

        //servicio 2
        System.out.println("servicio2");
        List<Paquete> conAlimentos = sistema.servicio2(true);
        for (Paquete p : conAlimentos) {
            System.out.println("codigo: " + p.getCodigo());
        }

        //servicio 3
        System.out.println("servicio3");
        List<Paquete> rangoUrgencia = sistema.servicio3(10, 90);
        for (Paquete p : rangoUrgencia) {
            System.out.println("codigo: " + p.getCodigo() + ", urgencia: " + p.getNivelUrgencia());
        }

        //back
        Solucion solBack = sistema.algoritmoBacktracking();
        System.out.println("Backtracking");
        System.out.println("Solucion obtenida:");
        for (Camion c : solBack.asignacion.keySet()) {
            System.out.print("Camion " + c.getId() + ": ");
            for (Paquete p : solBack.asignacion.get(c)) {
                System.out.print(p.getCodigo() + " ");
            }
            System.out.println();
        }
        System.out.println("Peso no asignado: " + solBack.pesoNoAsignado + " kg.");
        System.out.println("Metrica para analizar el costo de la solucion (cantidad de estados generados): " + solBack.metricaCosto);

        //greedy
        Solucion solGreedy = sistema.algoritmoGreedy();
        System.out.println("Greedy");
        System.out.println("Solucion obtenida:");
        for (Camion c : solGreedy.asignacion.keySet()) {
            System.out.print("Camion " + c.getId() + ": ");
            for (Paquete p : solGreedy.asignacion.get(c)) {
                System.out.print(p.getCodigo() + " ");
            }
            System.out.println();
        }
        System.out.println("Peso no asignado: " + solGreedy.pesoNoAsignado + " kg.");
        System.out.println("Metrica para analizar el costo de la solucion (cantidad de candidatos considerados): " + solGreedy.metricaCosto);
    }
}