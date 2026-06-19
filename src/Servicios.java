import java.io.*;
import java.util.*;

public class Servicios {
    private final Map<String, Paquete> paquetesPorCodigo; 
    private final List<Paquete> paquetesConAlimentos;
    private final List<Paquete> paquetesSinAlimentos;
    private final List<Paquete>[] paquetesPorUrgencia;
    private final List<Camion> listaCamiones;

    // variables auxiliares para el back
    private Solucion mejorSolucionBack;
    private int estadosGenerados;

    /*
     COMPLEJIDAD TEMPORAL CONSRUCTOR: O(N + M)
     donde N es la cantidad de camiones y M es la cantidad de paquetes
     JUSTIFICACION:
     El constructor inicializa las estructuras y luego invoca a dos metodos 
     privados (procesarCamiones y procesarPaquetes). Cada uno de estos metodos 
     lee su respectivo archivo CSV linea por linea de manera secuencial. 
     Dentro del bucle de lectura, todas las operaciones que se realizan toman tiempo 
     constante O(1). Por lo tanto, el costo total depende linealmente del tamaño 
     de las entradas: O(N) para los camiones y O(M) para los paquetes.
    */
    @SuppressWarnings("unchecked")
    public Servicios(String pathCamiones, String pathPaquetes) {
        this.paquetesPorCodigo = new HashMap<>();
        this.paquetesConAlimentos = new ArrayList<>();
        this.paquetesSinAlimentos = new ArrayList<>();
        this.listaCamiones = new ArrayList<>();
        
        this.paquetesPorUrgencia = new ArrayList[101];
        for (int i = 0; i <= 100; i++) {
            this.paquetesPorUrgencia[i] = new ArrayList<>();
        }

        this.procesarCamiones(pathCamiones);
        this.procesarPaquetes(pathPaquetes);
    }

    private void procesarCamiones(String pathCamiones) {
        try (BufferedReader br = new BufferedReader(new FileReader(pathCamiones))) {
            String linea = br.readLine(); 
            if (linea == null) return;

            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;

                String[] campos = linea.split(";");
                
                int id = Integer.parseInt(campos[0].trim());
                String patente = campos[1].trim();
                boolean estaRefrigerado = campos[2].trim().equals("1"); 
                int capacidadKg = Integer.parseInt(campos[3].trim());

                Camion nuevoCamion = new Camion(id, patente, estaRefrigerado, capacidadKg);
                this.listaCamiones.add(nuevoCamion);
            }
        } catch (IOException e) {
            System.err.println("Error al leer camiones: " + e.getMessage());
        }
    }

    private void procesarPaquetes(String pathPaquetes) {
        try (BufferedReader br = new BufferedReader(new FileReader(pathPaquetes))) {
            String linea = br.readLine(); 
            if (linea == null) return;

            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;

                String[] campos = linea.split(";");
                
                int id = Integer.parseInt(campos[0].trim());
                String codigo = campos[1].trim();
                int peso = Integer.parseInt(campos[2].trim());
                boolean contieneAlimentos = campos[3].trim().equals("1"); 
                int urgencia = Integer.parseInt(campos[4].trim());

                Paquete nuevoPaquete = new Paquete(id, codigo, peso, contieneAlimentos, urgencia);

                this.paquetesPorCodigo.put(codigo, nuevoPaquete);
                
                if (contieneAlimentos) {
                    this.paquetesConAlimentos.add(nuevoPaquete);
                } else {
                    this.paquetesSinAlimentos.add(nuevoPaquete);
                }
                
                if (urgencia >= 1 && urgencia <= 100) {
                    this.paquetesPorUrgencia[urgencia].add(nuevoPaquete);
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer paquetes: " + e.getMessage());
        }
    }

    /*
    COMPLEJIDAD TEMPORAL: O(1) 
    JUSTIFICACION:
    Utilizamos un HashMap (paquetesPorCodigo) donde la clave es el codigo del paquete.
    La operacion de busqueda .get en una tabla de hash toma tiempo constante O(1),
    ya que calcula el indice mediante la funcion hash de la clave de manera directa,
    sin necesidad de recorrer la estructura.
    */
    public Paquete servicio1(String codigoPaquete) {
        return this.paquetesPorCodigo.get(codigoPaquete);
    }

    /*
    COMPLEJIDAD TEMPORAL: O(1)
    JUSTIFICACION:
    En lugar de filtrar la lista completa en cada consulta, devolvemos directamente 
    una referencia a la lista ya calculada en el constructor (paquetesConAlimentos o
    paquetesSinAlimentos) segun el valor del boolean. 
    Retornar una referencia toma tiempo constante.
    */
    public List<Paquete> servicio2(boolean contieneAlimentos) {
        if (contieneAlimentos) {
            return this.paquetesConAlimentos;
        } else {
            return this.paquetesSinAlimentos;
        }
    }

    /*
    COMPLEJIDAD TEMPORAL: O(N + M)
    N es la cantidad de casilleros del rango y M es el total de paquetes que devolvemos.
    JUSTIFICACION:
    En vez de recorrer todos los paquetes que existen para ver cuales entran en el rango, 
    voy directo a los casilleros del arreglo que me interesan. 
    Como en el constructor ya guarde los paquetes agrupados por su nivel exacto de urgencia, 
    el for solo da vueltas entre el minimo y el maximo que me pidieron. En cada vuelta, 
    agarro los paquetes de ese casillero y los meto en la lista de resultado todos juntos con 
    el .addAll(). 
    El algoritmo no pierde el tiempo mirando paquetes que estan fuera del rango solicitado.
    */
    public List<Paquete> servicio3(int urgenciaMinima, int urgenciaMaxima) {
        List<Paquete> resultado = new ArrayList<>();
        
        int min = Math.max(1, urgenciaMinima);
        int max = Math.min(100, urgenciaMaxima);
        
        for (int i = min; i <= max; i++) {
            resultado.addAll(this.paquetesPorUrgencia[i]);
        }
        
        return resultado;
    }

    /*
    ESTRATEGIA: ordenar todos los paquetes de mayor a menor peso. 
    Recorre la lista y asigna cada paquete al primer camion disponible que tenga espacio suficiente 
    y cumpla con las restricciones (los alimentos solo van en camiones refrigerados). 
    Si no entra en ninguno, queda afuera.
    */
    public Solucion algoritmoGreedy() {
        Solucion resultado = new Solucion();
        
        Map<Camion, Integer> cargaActualCamion = new HashMap<>();
        for (Camion c : this.listaCamiones) {
            resultado.asignacion.put(c, new ArrayList<>());
            cargaActualCamion.put(c, 0);
        }

        List<Paquete> candidatos = new ArrayList<>(this.paquetesPorCodigo.values());
        Collections.sort(candidatos);

        int pesoTotalPaquetes = 0;
        int pesoAsignado = 0;
        int candidatosConsiderados = 0;

        for (Paquete p : candidatos) {
            pesoTotalPaquetes += p.getPeso();
            candidatosConsiderados++; 
            
            for (Camion c : this.listaCamiones) {
                int cargaNueva = cargaActualCamion.get(c) + p.getPeso();
            
                boolean cumpleRefrigeracion = !p.isContieneAlimentos() || c.isEstaRefrigerado();
                boolean entraEnCapacidad = cargaNueva <= c.getCapacidadKg();

                if (cumpleRefrigeracion && entraEnCapacidad) {
                    resultado.asignacion.get(c).add(p);
                    cargaActualCamion.put(c, cargaNueva);
                    pesoAsignado += p.getPeso();
                    break; 
                }
            }
        }

        resultado.pesoNoAsignado = pesoTotalPaquetes - pesoAsignado;
        resultado.metricaCosto = candidatosConsiderados;
        
        return resultado;
    }

    /* 
    ESTRATEGIA: explora todas las combinaciones posibles de manera recursiva. 
    Para cada paquete, evalua ingresarlo en cada uno de los camiones validos o dejarlo sin asignar. 
    PODA: si el peso que ya se esta quedando afuera iguala o supera al de la mejor solucion encontrada hasta el momento, 
    corta esa rama del arbol de decision para ahorrar tiempo. 
    */
    public Solucion algoritmoBacktracking() {
        this.mejorSolucionBack = new Solucion();
        this.mejorSolucionBack.pesoNoAsignado = Integer.MAX_VALUE; 
        this.estadosGenerados = 0;

        List<Paquete> listaPaquetes = new ArrayList<>(this.paquetesPorCodigo.values());
        
        Map<Camion, List<Paquete>> asignacionActual = new HashMap<>();
        Map<Camion, Integer> cargaActualCamion = new HashMap<>();
        int pesoTotalPaquetes = 0;

        for(Camion c : this.listaCamiones){
            asignacionActual.put(c, new ArrayList<>());
            cargaActualCamion.put(c, 0);
        }
        
        for(Paquete p : listaPaquetes){
            pesoTotalPaquetes += p.getPeso();
        }

        backtrackingRecursivo(0, listaPaquetes, asignacionActual, cargaActualCamion, pesoTotalPaquetes, 0);

        this.mejorSolucionBack.metricaCosto = this.estadosGenerados;
        return this.mejorSolucionBack;
    }

    private void backtrackingRecursivo(int indicePaquete, List<Paquete> paquetes, Map<Camion, List<Paquete>> asignacionActual, Map<Camion, Integer> cargaActual, int pesoTotalPaquetes, int pesoActualNoAsignado) {
        this.estadosGenerados++; // metrica: cada entrada a la funcion es un estado

        //PODA: si lo que ya vengo perdiendo es peor o igual que la mejor solucion guardada, no sigo
        if(pesoActualNoAsignado >= this.mejorSolucionBack.pesoNoAsignado){
            return;
        }

        //CASO BASE: si ya evalue todos los paquetes, comparo y guardo si es mejor
        if (indicePaquete == paquetes.size()) {
            if (pesoActualNoAsignado < this.mejorSolucionBack.pesoNoAsignado) {
                this.mejorSolucionBack.pesoNoAsignado = pesoActualNoAsignado;
                this.mejorSolucionBack.asignacion = new HashMap<>();
                for (Camion c : asignacionActual.keySet()) {
                    this.mejorSolucionBack.asignacion.put(c, new ArrayList<>(asignacionActual.get(c)));
                }
            }
            return;
        }

        Paquete p = paquetes.get(indicePaquete);

        //intentar meter el paquete en cada camion posible
        for (Camion c : this.listaCamiones) {
            int cargaNueva = cargaActual.get(c) + p.getPeso();
            boolean cumpleRefrigeracion = !p.isContieneAlimentos() || c.isEstaRefrigerado();
            boolean entraEnCapacidad = cargaNueva <= c.getCapacidadKg();

            if (cumpleRefrigeracion && entraEnCapacidad) {
                asignacionActual.get(c).add(p);
                cargaActual.put(c, cargaNueva);
                backtrackingRecursivo(indicePaquete + 1, paquetes, asignacionActual, cargaActual, pesoTotalPaquetes, pesoActualNoAsignado);
                asignacionActual.get(c).remove(asignacionActual.get(c).size() - 1);
                cargaActual.put(c, cargaActual.get(c) - p.getPeso());
            }
        }

        //dejar el paquete sin asignar en ningun camion
        backtrackingRecursivo(indicePaquete + 1, paquetes, asignacionActual, cargaActual, pesoTotalPaquetes, pesoActualNoAsignado + p.getPeso());
    }
}