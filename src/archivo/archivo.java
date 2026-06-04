package archivo;
 
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import modelo.Cliente;
import modelo.Habitacion;
import modelo.Hotel;
import modelo.Reserva;
 
public class archivo {
 
    private static final String PATH_HOTEL = "hotel.txt";
    private static final String PATH_CLIENTES = "clientes.txt";
    private static final String PATH_HABITACIONES = "habitaciones.txt";
    private static final String PATH_RESERVAS = "reservas.txt";
    
    // ─────────────────────────── HOTEL ───────────────────────────
    
    public void guardarHotel(Hotel hotel) {
        try {
            File file = new File(PATH_HOTEL);
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));

            bw.write(
                hotel.getIdHotel() + "\t" +
                hotel.getNombre()+ "\t" +
                hotel.getDireccion());

            bw.newLine();
            bw.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public Hotel leerHotel() {
        File file = new File(PATH_HOTEL);
        if (!file.exists()) return null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String linea = br.readLine();

            if (linea == null) return null;

            String[] datos = linea.split("\t");

            Hotel hotel = new Hotel(
                datos[0],
                datos[1],
                datos[2]
            );

            br.close();
            return hotel;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    
    // ─────────────────────────── CLIENTES ───────────────────────────
 
    public void guardarClientes(ArrayList<Cliente> lista) {
        try {
            File file = new File(PATH_CLIENTES);
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            for (Cliente c : lista) {
                bw.write(c.getIdCliente() + "\t" +
                         c.getNombre()    + "\t" +
                         c.getTelefono());
                bw.newLine();
            }
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 
    public ArrayList<Cliente> leerClientes() {
        ArrayList<Cliente> lista = new ArrayList<>();
        File file = new File(PATH_CLIENTES);
        if (!file.exists()) return lista;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split("\t");
                String id  = datos[0];
                String nom = datos[1];
                String cel = datos[2];
                lista.add(new Cliente(id, nom, cel));
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }
 
    // ─────────────────────────── HABITACIONES ───────────────────────────
 
    public void guardarHabitaciones(ArrayList<Habitacion> lista) {
        try {
            File file = new File(PATH_HABITACIONES);
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            for (Habitacion h : lista) {
                bw.write(h.getNumHabitacion() + "\t" +
                         h.getTipo()          + "\t" +
                         h.getPrecio());
                bw.newLine();
            }
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 
    public ArrayList<Habitacion> leerHabitaciones(Hotel hotel) {
        ArrayList<Habitacion> lista = new ArrayList<>();
        File file = new File(PATH_HABITACIONES);
        if (!file.exists()) return lista;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split("\t");
                String numHab = datos[0];
                String tipo   = datos[1];
                double precio = Double.parseDouble(datos[2]);
                lista.add(new Habitacion(numHab, tipo, precio, hotel));
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }
    
    // ─────────────────────────── RESERVAS ───────────────────────────

    public void guardarReservas(ArrayList<Reserva> lista) {
        try {
            File file = new File(PATH_RESERVAS);
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            for (Reserva r : lista) {
                bw.write(r.getIdReserva()                    + "\t" +
                         r.getCliente().getIdCliente()       + "\t" +
                         r.getHabitacion().getNumHabitacion()+ "\t" +
                         r.getFechaIngreso()                 + "\t" +
                         r.getFechaSalida());
                bw.newLine();
            }
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Reserva> leerReservas(ArrayList<Cliente> clientes, ArrayList<Habitacion> habitaciones) {
        ArrayList<Reserva> lista = new ArrayList<>();
        File file = new File(PATH_RESERVAS);
        if (!file.exists()) return lista;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split("\t");
                String idReserva   = datos[0];
                String idCliente   = datos[1];
                String numHab      = datos[2];
                String fechaIngreso = datos[3];
                String fechaSalida  = datos[4];

                // Buscar el cliente y la habitación en las listas ya cargadas
                Cliente clienteEncontrado = null;
                for (Cliente c : clientes) {
                    if (c.getIdCliente().equals(idCliente)) {
                        clienteEncontrado = c;
                        break;
                    }
                }

                Habitacion habEncontrada = null;
                for (Habitacion h : habitaciones) {
                    if (h.getNumHabitacion().equals(numHab)) {
                        habEncontrada = h;
                        break;
                    }
                }

                if (clienteEncontrado == null || habEncontrada == null) continue;

                Reserva reserva = new Reserva();
                reserva.setIdReserva(idReserva);
                reserva.setCliente(clienteEncontrado);
                reserva.setHabitacion(habEncontrada);
                reserva.setFechaIngreso(fechaIngreso);
                reserva.setFechaSalida(fechaSalida);

                lista.add(reserva);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }
    
}