package controlador;

import archivo.archivo;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import modelo.Cliente;
import modelo.Habitacion;
import modelo.Hotel;
import modelo.Reserva;
import vista.HabForm;

public class HabController {

    private HabForm form;
    private ArrayList<Habitacion> listaHabitaciones;
    private ArrayList<Reserva> listaReservas;
    private Hotel hotel;
    private archivo arch;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public HabController(HabForm form, Hotel hotel) {
        this.form  = form;
        this.hotel = hotel;
        this.arch  = new archivo();
        this.listaHabitaciones = arch.leerHabitaciones(hotel);

        ArrayList<modelo.Cliente> clientes = arch.leerClientes();
        this.listaReservas = arch.leerReservas(clientes, listaHabitaciones);

        for (Habitacion h : listaHabitaciones) {
            crearTabHabitacion(h);
        }
    }

    public void agregarHabitacion() {
        if (camposVacios()) {
            JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios",
                    "ERROR", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String numHab = form.getTxtNumHabitacion();

        if (existeId(numHab)) {
            JOptionPane.showMessageDialog(null,
                    "La HABITACION ya existe, no se permiten duplicados",
                    "ERROR", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String tipo   = form.getTipoHabitacion();
        double precio = Double.parseDouble(form.getTxtPrecioHabitacion());

        Habitacion habitacion = new Habitacion(numHab, tipo, precio, hotel);
        listaHabitaciones.add(habitacion);
        arch.guardarHabitaciones(listaHabitaciones);
        actualizarTabbedPane();
        form.limpiarCampos();
    }

    private void crearTabHabitacion(Habitacion h) {
        String estado     = calcularEstado(h);
        String fechaOcupa = calcularFechaOcupacion(h);
        String fechaLibre = calcularFechaLiberacion(h);

        JPanel panel = new JPanel();

        if (fechaOcupa != null && fechaLibre != null) {
            // Reservada u Ocupada: 6 filas — cada dato en su propio label
            panel.setLayout(new GridLayout(6, 1, 0, 8));
            panel.add(new JLabel("Número: "  + h.getNumHabitacion()));
            panel.add(new JLabel("Tipo: "    + h.getTipo()));
            panel.add(new JLabel("Precio: $" + h.getPrecio()));
            panel.add(new JLabel("Estado: "  + estado));
            panel.add(new JLabel("La habitación se ocupa: "  + fechaOcupa));
            panel.add(new JLabel("La habitación se libera: " + fechaLibre));
        } else {
            // Disponible: 4 filas
            panel.setLayout(new GridLayout(4, 1, 0, 8));
            panel.add(new JLabel("Número: "  + h.getNumHabitacion()));
            panel.add(new JLabel("Tipo: "    + h.getTipo()));
            panel.add(new JLabel("Precio: $" + h.getPrecio()));
            panel.add(new JLabel("Estado: "  + estado));
        }

        form.agregarTab("Hab " + h.getNumHabitacion(), panel);
    }

    //Devuelve la fecha de ingreso de la próxima reserva activa o futura, o null si disponible
    private String calcularFechaOcupacion(Habitacion h) {
        LocalDate hoy = LocalDate.now();

        for (Reserva r : listaReservas) {
            if (!r.getHabitacion().getNumHabitacion().equals(h.getNumHabitacion())) continue;
            try {
                LocalDate inicio = LocalDate.parse(r.getFechaIngreso(), FMT);
                LocalDate salida = LocalDate.parse(r.getFechaSalida(),  FMT);

                // Reserva activa (huésped ya llegó) o futura
                if (hoy.isBefore(salida) && (hoy.isEqual(inicio) || hoy.isAfter(inicio) || hoy.isBefore(inicio))) {
                    return inicio.format(FMT);
                }
            } catch (DateTimeParseException e) {}
        }
        return null;
    }

    //Devuelve la fecha de salida de la reserva activa o futura más próxima, o null si disponible
    private String calcularFechaLiberacion(Habitacion h) {
        LocalDate hoy = LocalDate.now();

        for (Reserva r : listaReservas) {
            if (!r.getHabitacion().getNumHabitacion().equals(h.getNumHabitacion())) continue;
            try {
                LocalDate inicio = LocalDate.parse(r.getFechaIngreso(), FMT);
                LocalDate salida = LocalDate.parse(r.getFechaSalida(),  FMT);

                if (hoy.isBefore(salida) && (hoy.isEqual(inicio) || hoy.isAfter(inicio) || hoy.isBefore(inicio))) {
                    return salida.format(FMT);
                }
            } catch (DateTimeParseException e) {}
        }
        return null;
    }

    private String calcularEstado(Habitacion h) {
        LocalDate hoy = LocalDate.now();

        for (Reserva r : listaReservas) {
            if (!r.getHabitacion().getNumHabitacion().equals(h.getNumHabitacion())) continue;
            try {
                LocalDate inicio = LocalDate.parse(r.getFechaIngreso(), FMT);
                LocalDate salida = LocalDate.parse(r.getFechaSalida(),  FMT);

                // El huésped ya está hospedado
                if ((hoy.isEqual(inicio) || hoy.isAfter(inicio)) && hoy.isBefore(salida)) {
                    return "Ocupada";
                }

                // Reserva futura
                if (hoy.isBefore(inicio)) {
                    return "Reservada";
                }

            } catch (DateTimeParseException e) {}
        }

        return "Disponible";
    }

    //─── Buscar / Modificar / Eliminar ────────────────────────────────────────

    public void buscarHabitacion() {
        String id = JOptionPane.showInputDialog(null,
                "Ingrese el NÚMERO de habitación a buscar");

        if (id == null) return;

        if (id.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Debe ingresar el número de la habitación");
            return;
        }

        for (Habitacion h : listaHabitaciones) {
            if (h.getNumHabitacion().equals(id)) {
                form.setTxtNumHabitacion(h.getNumHabitacion());
                form.setTipoHabitacion(h.getTipo());
                form.setTxtPrecioHabitacion(String.valueOf(h.getPrecio()));
                form.bloquearCampos();
                return;
            }
        }

        JOptionPane.showMessageDialog(null, "No se encontró la habitación",
                "ERROR", JOptionPane.ERROR_MESSAGE);
    }

    public void modificar() {
        if (camposVacios()) {
            JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String id = form.getTxtNumHabitacion();
        Habitacion habitacionModificar = null;

        for (Habitacion h : listaHabitaciones) {
            if (id.equals(h.getNumHabitacion())) {
                habitacionModificar = h;
                break;
            }
        }

        if (habitacionModificar == null) {
            JOptionPane.showMessageDialog(null,
                    "No se encontró la habitación con ese número",
                    "ERROR", JOptionPane.ERROR_MESSAGE);
            return;
        }

        habitacionModificar.setTipo(form.getTipoHabitacion());
        habitacionModificar.setPrecio(Double.parseDouble(form.getTxtPrecioHabitacion()));
        arch.guardarHabitaciones(listaHabitaciones);

        actualizarTabbedPane();
        form.desbloquearCampos();
        form.limpiarCampos();

        JOptionPane.showMessageDialog(null, "Habitación modificada correctamente",
                "OK", JOptionPane.INFORMATION_MESSAGE);
    }

    public void eliminarHabitacion() {
        String id = form.getTxtNumHabitacion();

        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Ingrese el número de la habitación a eliminar",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (int i = 0; i < listaHabitaciones.size(); i++) {
            if (listaHabitaciones.get(i).getNumHabitacion().equals(id)) {
                int confirmacion = JOptionPane.showConfirmDialog(null,
                        "¿Está seguro de eliminar esta HABITACION: " + id + "?",
                        "Confirmación", JOptionPane.YES_NO_OPTION);

                if (confirmacion == JOptionPane.YES_OPTION) {
                    listaHabitaciones.remove(i);
                    arch.guardarHabitaciones(listaHabitaciones);
                    actualizarTabbedPane();
                    form.limpiarCampos();
                    form.desbloquearCampos();
                    JOptionPane.showMessageDialog(null, "Habitación eliminada correctamente",
                            "OK", JOptionPane.INFORMATION_MESSAGE);
                }
                return;
            }
        }

        JOptionPane.showMessageDialog(null, "No se encontró la habitación con ese número",
                "ERROR", JOptionPane.ERROR_MESSAGE);
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private void actualizarTabbedPane() {
        form.limpiarTabs();

        listaHabitaciones = arch.leerHabitaciones(hotel);
        ArrayList<Cliente> clientes = arch.leerClientes();
        listaReservas = arch.leerReservas(clientes, listaHabitaciones);

        for (Habitacion h : listaHabitaciones) {
            crearTabHabitacion(h);
        }

        form.revalidate();
        form.repaint();
    }

    private boolean camposVacios() {
        return form.getTxtNumHabitacion().isEmpty()
            || form.getTipoHabitacion().isEmpty()
            || form.getTxtPrecioHabitacion().isEmpty();
    }

    private boolean existeId(String id) {
        for (Habitacion h : listaHabitaciones) {
            if (h.getNumHabitacion().equals(id)) return true;
        }
        return false;
    }
}