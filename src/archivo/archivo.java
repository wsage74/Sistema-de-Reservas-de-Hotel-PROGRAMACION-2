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
import modelo.Pago;
import modelo.Reserva;

public class archivo {

    private static final String PATH_HOTEL        = "hotel.txt";
    private static final String PATH_CLIENTES     = "clientes.txt";
    private static final String PATH_HABITACIONES = "habitaciones.txt";
    private static final String PATH_RESERVAS     = "reservas.txt";
    private static final String PATH_PAGOS        = "pagos.txt";

    // ─────────────────────────── HOTEL ───────────────────────────

    public void guardarHotel(Hotel hotel) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(PATH_HOTEL));
            bw.write(hotel.getIdHotel() + "\t" + hotel.getNombre() + "\t" + hotel.getDireccion());
            bw.newLine();
            bw.close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public Hotel leerHotel() {
        File file = new File(PATH_HOTEL);
        if (!file.exists()) return null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String linea = br.readLine();
            br.close();
            if (linea == null) return null;
            String[] d = linea.split("\t");
            return new Hotel(d[0], d[1], d[2]);
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    // ─────────────────────────── CLIENTES ───────────────────────────

    public void guardarClientes(ArrayList<Cliente> lista) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(PATH_CLIENTES));
            for (Cliente c : lista) {
                bw.write(c.getIdCliente() + "\t" + c.getNombre() + "\t" + c.getTelefono());
                bw.newLine();
            }
            bw.close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public ArrayList<Cliente> leerClientes() {
        ArrayList<Cliente> lista = new ArrayList<>();
        File file = new File(PATH_CLIENTES);
        if (!file.exists()) return lista;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                String[] d = linea.split("\t");
                lista.add(new Cliente(d[0], d[1], d[2]));
            }
            br.close();
        } catch (Exception e) { e.printStackTrace(); }
        return lista;
    }

    // ─────────────────────────── HABITACIONES ───────────────────────────

    public void guardarHabitaciones(ArrayList<Habitacion> lista) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(PATH_HABITACIONES));
            for (Habitacion h : lista) {
                bw.write(h.getNumHabitacion() + "\t" + h.getTipo() + "\t" + h.getPrecio());
                bw.newLine();
            }
            bw.close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public ArrayList<Habitacion> leerHabitaciones(Hotel hotel) {
        ArrayList<Habitacion> lista = new ArrayList<>();
        File file = new File(PATH_HABITACIONES);
        if (!file.exists()) return lista;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                String[] d = linea.split("\t");
                lista.add(new Habitacion(d[0], d[1], Double.parseDouble(d[2]), hotel));
            }
            br.close();
        } catch (Exception e) { e.printStackTrace(); }
        return lista;
    }

    // ─────────────────────────── RESERVAS ───────────────────────────

    public void guardarReservas(ArrayList<Reserva> lista) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(PATH_RESERVAS));
            for (Reserva r : lista) {
                String idPago = (r.getPago() != null) ? r.getPago().getIdPago() : "N/A";
                bw.write(r.getIdReserva()                     + "\t" +
                         r.getCliente().getIdCliente()        + "\t" +
                         r.getHabitacion().getNumHabitacion() + "\t" +
                         r.getFechaIngreso()                  + "\t" +
                         r.getFechaSalida()                   + "\t" +
                         idPago);
                bw.newLine();
            }
            bw.close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public ArrayList<Reserva> leerReservas(ArrayList<Cliente> clientes,
                                            ArrayList<Habitacion> habitaciones) {
        ArrayList<Reserva> lista = new ArrayList<>();
        File file = new File(PATH_RESERVAS);
        if (!file.exists()) return lista;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                String[] d = linea.split("\t");
                if (d.length < 5) continue;

                Cliente clienteEncontrado = null;
                for (Cliente c : clientes)
                    if (c.getIdCliente().equals(d[1])) { clienteEncontrado = c; break; }

                Habitacion habEncontrada = null;
                for (Habitacion h : habitaciones)
                    if (h.getNumHabitacion().equals(d[2])) { habEncontrada = h; break; }

                if (clienteEncontrado == null || habEncontrada == null) continue;

                Reserva reserva = new Reserva();
                reserva.setIdReserva(d[0]);
                reserva.setCliente(clienteEncontrado);
                reserva.setHabitacion(habEncontrada);
                reserva.setFechaIngreso(d[3]);
                reserva.setFechaSalida(d[4]);
                lista.add(reserva);
            }
            br.close();
        } catch (Exception e) { e.printStackTrace(); }
        return lista;
    }

    // ─────────────────────────── PAGOS ───────────────────────────

    public void guardarPagos(ArrayList<Pago> lista) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(PATH_PAGOS));
            for (Pago p : lista) {
                String idReserva = (p.getReserva() != null) ? p.getReserva().getIdReserva() : "N/A";
                bw.write(p.getIdPago()     + "\t" +
                         idReserva         + "\t" +
                         p.getMonto()      + "\t" +
                         p.getMetodoPago());
                bw.newLine();
            }
            bw.close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public ArrayList<Pago> leerPagos(ArrayList<Reserva> reservas) {
        ArrayList<Pago> lista = new ArrayList<>();
        File file = new File(PATH_PAGOS);
        if (!file.exists()) return lista;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                String[] d = linea.split("\t");
                if (d.length < 4) continue;
                Pago pago = new Pago(d[0], Double.parseDouble(d[2]), d[3]);
                // vincular con la reserva
                for (Reserva r : reservas) {
                    if (r.getIdReserva().equals(d[1])) {
                        pago.setReserva(r);
                        r.setPago(pago);
                        break;
                    }
                }
                lista.add(pago);
            }
            br.close();
        } catch (Exception e) { e.printStackTrace(); }
        return lista;
    }
}