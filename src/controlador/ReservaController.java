package controlador;

import archivo.archivo;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.Cliente;
import modelo.Habitacion;
import modelo.Reserva;
import vista.ReservaForm;

public class ReservaController {

    private ReservaForm form;
    private ArrayList<Reserva> listaReservas;
    private DefaultTableModel dtm;
    private archivo arch;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public ReservaController(ReservaForm form, DefaultTableModel dtm) {
        this.form = form;
        this.dtm  = dtm;
        this.arch = new archivo();

        ArrayList<Cliente>    clientes     = arch.leerClientes();
        ArrayList<Habitacion> habitaciones = arch.leerHabitaciones(null);
        this.listaReservas = arch.leerReservas(clientes, habitaciones);
        // vincular pagos guardados a las reservas cargadas
        arch.leerPagos(this.listaReservas);
        actualizarTabla();
    }

    public void inicializar() {
        form.cargarClientesCombo(arch.leerClientes());
    }

    public void inicializarHabitaciones() {
        form.cargarHabitacionesCombo(arch.leerHabitaciones(null));
    }

    // ── AGREGAR ──────────────────────────────────────────────────────

    public void agregarReserva() {
        if (camposVacios()) {
            JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios.",
                "ERROR", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalDate ingreso, salida;
        try {
            ingreso = LocalDate.parse(form.getTxtFechaIngreso(), FMT);
            salida  = LocalDate.parse(form.getTxtFechaSalida(),  FMT);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(null,
                "Formato de fecha inválido.\nUse DD-MM-AAAA  (ejemplo: 15-06-2025).",
                "ERROR", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!ingreso.isBefore(salida)) {
            JOptionPane.showMessageDialog(null,
                "La fecha de ingreso debe ser anterior a la de salida.",
                "ERROR", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Habitacion hab = form.getCmbHabitaciones();
        Reserva conflicto = buscarConflicto(hab, ingreso, salida, null);
        if (conflicto != null) {
            JOptionPane.showMessageDialog(null,
                "La habitación " + hab.getNumHabitacion()
                + " ya está reservada en ese período.\nSe libera el: "
                + conflicto.getFechaSalida(),
                "Habitación no disponible", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = crearId();

        Reserva r = new Reserva(id, form.getCmbClientes(), hab,
                form.getTxtFechaIngreso(), form.getTxtFechaSalida(), null);

        listaReservas.add(r);
        arch.guardarReservas(listaReservas);

        actualizarTabla();
        form.limpiarCampos();
        JOptionPane.showMessageDialog(null,
            "Reserva registrada correctamente.\nID: " + id
            + "\n\nRecuerda registrar el pago en el módulo de Pagos.",
            "OK", JOptionPane.INFORMATION_MESSAGE);
    }

    // ── BUSCAR ───────────────────────────────────────────────────────

    public void buscarReserva() {
        String id = JOptionPane.showInputDialog(null, "Ingrese el ID de la reserva:");
        if (id == null || id.trim().isEmpty()) return;

        for (int i = 0; i < listaReservas.size(); i++) {
            Reserva r = listaReservas.get(i);
            if (r.getIdReserva().equals(id.trim())) {
                form.getTblLista().setRowSelectionInterval(i, i);
                form.setCmbClientes(r.getCliente());
                form.setCmbHabitaciones(r.getHabitacion());
                form.setTxtFechaIngreso(r.getFechaIngreso());
                form.setTxtFechaSalida(r.getFechaSalida());
                return;
            }
        }
        JOptionPane.showMessageDialog(null, "No se encontró la reserva con ID: " + id,
            "ERROR", JOptionPane.ERROR_MESSAGE);
    }

    // ── MODIFICAR ────────────────────────────────────────────────────

    public void modificarReserva() {
        int fila = form.getTblLista().getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(null,
                "Primero busca la reserva que deseas modificar.", "ERROR",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (camposVacios()) {
            JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios.",
                "ERROR", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalDate ingreso, salida;
        try {
            ingreso = LocalDate.parse(form.getTxtFechaIngreso(), FMT);
            salida  = LocalDate.parse(form.getTxtFechaSalida(),  FMT);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(null,
                "Formato de fecha inválido.\nUse DD-MM-AAAA.", "ERROR",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!ingreso.isBefore(salida)) {
            JOptionPane.showMessageDialog(null,
                "La fecha de ingreso debe ser anterior a la de salida.",
                "ERROR", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Reserva reservaActual = listaReservas.get(fila);

        if (reservaActual.getPago() != null) {
            JOptionPane.showMessageDialog(
                    null,
                    "No se puede modificar una reserva que ya tiene un pago registrado.\n"
                    + "Primero elimina el pago si necesitas cambiar las fechas o la habitación.",
                    "Reserva con pago",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        Habitacion hab = form.getCmbHabitaciones();
        Reserva conflicto = buscarConflicto(hab, ingreso, salida, reservaActual.getIdReserva());
        if (conflicto != null) {
            JOptionPane.showMessageDialog(null,
                "La habitación ya está reservada en ese período.\nSe libera el: "
                + conflicto.getFechaSalida(),
                "Habitación no disponible", JOptionPane.WARNING_MESSAGE);
            return;
        }

        reservaActual.setCliente(form.getCmbClientes());
        reservaActual.setHabitacion(hab);
        reservaActual.setFechaIngreso(form.getTxtFechaIngreso());
        reservaActual.setFechaSalida(form.getTxtFechaSalida());

        arch.guardarReservas(listaReservas);
        actualizarTabla();
        form.limpiarCampos();
        JOptionPane.showMessageDialog(null, "Reserva modificada correctamente.",
            "OK", JOptionPane.INFORMATION_MESSAGE);
    }

    // ── ELIMINAR ─────────────────────────────────────────────────────

    public void eliminarReserva() {
        String id = JOptionPane.showInputDialog(null, "Ingrese el ID de la reserva a eliminar:");
        if (id == null || id.trim().isEmpty()) return;

        for (int i = 0; i < listaReservas.size(); i++) {
            if (listaReservas.get(i).getIdReserva().equals(id.trim())) {
                int conf = JOptionPane.showConfirmDialog(null,
                    "¿Eliminar la reserva ID: " + id + "?",
                    "Confirmar", JOptionPane.YES_NO_OPTION);
                if (conf == JOptionPane.YES_OPTION) {
                    listaReservas.remove(i);
                    arch.guardarReservas(listaReservas);
                    actualizarTabla();
                    form.limpiarCampos();
                    JOptionPane.showMessageDialog(null, "Reserva eliminada correctamente.",
                        "OK", JOptionPane.INFORMATION_MESSAGE);
                }
                return;
            }
        }
        JOptionPane.showMessageDialog(null, "No se encontró la reserva con ID: " + id,
            "ERROR", JOptionPane.ERROR_MESSAGE);
    }

    // ── Métodos privados ──────────────────────────────────────────────

    private void actualizarTabla() {
        dtm.setRowCount(0);
        for (Reserva r : listaReservas) {
            dtm.addRow(new Object[]{
                r.getIdReserva(),
                r.getCliente().getNombre(),
                r.getHabitacion().getNumHabitacion(),
                r.getFechaIngreso(),
                r.getFechaSalida(),
                r.getMontoStr() + " - " + r.getMetodoStr()
            });
        }
    }

    private boolean camposVacios() {
        return form.getCmbClientes()     == null
            || form.getCmbHabitaciones() == null
            || form.getTxtFechaIngreso().trim().isEmpty()
            || form.getTxtFechaSalida().trim().isEmpty();
    }

    private Reserva buscarConflicto(Habitacion hab, LocalDate nuevoIni,
                                     LocalDate nuevoSal, String excluirId) {
        for (Reserva r : listaReservas) {
            if (excluirId != null && r.getIdReserva().equals(excluirId)) continue;
            if (!r.getHabitacion().getNumHabitacion().equals(hab.getNumHabitacion())) continue;
            try {
                LocalDate ini = LocalDate.parse(r.getFechaIngreso(), FMT);
                LocalDate sal = LocalDate.parse(r.getFechaSalida(),  FMT);
                if (nuevoIni.isBefore(sal) && nuevoSal.isAfter(ini)) return r;
            } catch (DateTimeParseException ignored) {}
        }
        return null;
    }

    private String crearId() {
        int max = 0;
        for (Reserva r : listaReservas) {
            try {
                int v = Integer.parseInt(r.getIdReserva());
                if (v > max) max = v;
            } catch (NumberFormatException ignored) {}
        }
        return String.valueOf(max + 1);
    }
}