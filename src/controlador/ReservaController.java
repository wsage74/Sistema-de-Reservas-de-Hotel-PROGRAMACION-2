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

    // Formato de fecha que usa el sistema: DD-MM-AAAA
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    // ─── Constructor ─────────────────────────────────────────────────────────

    public ReservaController(ReservaForm form, DefaultTableModel dtm) {
        this.form = form;
        this.dtm  = dtm;
        this.arch = new archivo();

        ArrayList<Cliente>    clientes     = arch.leerClientes();
        ArrayList<Habitacion> habitaciones = arch.leerHabitaciones(null);

        this.listaReservas = arch.leerReservas(clientes, habitaciones);
        actualizarTabla();
    }

    // ─── Inicialización de combos ─────────────────────────────────────────────

    public void inicializar() {
        ArrayList<Cliente> lista = arch.leerClientes();
        form.cargarClientesCombo(lista);
    }

    public void inicializarHabitaciones() {
        // Solo carga habitaciones que NO están reservadas en este momento
        // (es decir, cuya fecha de salida ya pasó o que no tienen ninguna reserva).
        // El filtro real de solapamiento ocurre en agregarReserva() con las fechas
        // que el usuario ingresa, así el combo muestra todas las habitaciones
        // registradas y avisamos si hay conflicto al intentar reservar.
        ArrayList<Habitacion> lista = arch.leerHabitaciones(null);
        form.cargarHabitacionesCombo(lista);
    }

    // ─── AGREGAR ─────────────────────────────────────────────────────────────

    public void agregarReserva() {

        if (camposVacios()) {
            JOptionPane.showMessageDialog(null,
                    "Todos los campos son obligatorios.",
                    "ERROR", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String strIngreso = form.getTxtFechaIngreso();
        String strSalida  = form.getTxtFechaSalida();

        // Validar que las fechas tengan el formato correcto
        LocalDate ingreso, salida;
        try {
            ingreso = LocalDate.parse(strIngreso, FMT);
            salida  = LocalDate.parse(strSalida,  FMT);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(null,
                    "Formato de fecha inválido.\nUse DD-MM-AAAA  (ejemplo: 15-06-2025).",
                    "ERROR", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // La fecha de ingreso debe ser antes que la de salida
        if (!ingreso.isBefore(salida)) {
            JOptionPane.showMessageDialog(null,
                    "La fecha de ingreso debe ser anterior a la fecha de salida.",
                    "ERROR", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Habitacion habSeleccionada = form.getCmbHabitaciones();

        // ── Verificar solapamiento con reservas existentes ──────────────────
        Reserva conflicto = buscarConflicto(habSeleccionada, ingreso, salida, null);

        if (conflicto != null) {
            LocalDate liberacion = LocalDate.parse(conflicto.getFechaSalida(), FMT);
            JOptionPane.showMessageDialog(null,
                    "⚠ La habitación " + habSeleccionada.getNumHabitacion()
                    + " (" + habSeleccionada.getTipo() + ") ya está reservada\n"
                    + "en ese período.\n\n"
                    + "Se libera el: " + liberacion.format(FMT)
                    + "  (reserva ID " + conflicto.getIdReserva() + ").",
                    "Habitación no disponible", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // ────────────────────────────────────────────────────────────────────

        String id = crearId();

        Reserva r = new Reserva(
                id,
                form.getCmbClientes(),
                habSeleccionada,
                strIngreso,
                strSalida,
                "PENDIENTE"
        );

        listaReservas.add(r);
        arch.guardarReservas(listaReservas);
        actualizarTabla();
        form.limpiarCampos();

        JOptionPane.showMessageDialog(null,
                "Reserva registrada correctamente.",
                "OK", JOptionPane.INFORMATION_MESSAGE);
    }

    // ─── BUSCAR ──────────────────────────────────────────────────────────────

    public void buscarReserva() {

        String id = JOptionPane.showInputDialog(null,
                "Ingrese el ID de la reserva a buscar:");

        if (id == null) return;
        id = id.trim();

        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Debe ingresar el ID de la reserva.",
                    "ERROR", JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (int i = 0; i < listaReservas.size(); i++) {
            Reserva r = listaReservas.get(i);
            if (r.getIdReserva().equals(id)) {
                form.getTblLista().setRowSelectionInterval(i, i);
                form.setCmbClientes(r.getCliente());
                form.setCmbHabitaciones(r.getHabitacion());
                form.setTxtFechaIngreso(r.getFechaIngreso());
                form.setTxtFechaSalida(r.getFechaSalida());
                return;
            }
        }

        JOptionPane.showMessageDialog(null,
                "No se encontró ninguna reserva con el ID: " + id,
                "ERROR", JOptionPane.ERROR_MESSAGE);
    }

    // ─── MODIFICAR ───────────────────────────────────────────────────────────

    public void modificarReserva() {

        int fila = form.getTblLista().getSelectedRow();

        if (fila < 0) {
            JOptionPane.showMessageDialog(null,
                    "Primero busque la reserva que desea modificar.",
                    "ERROR", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (camposVacios()) {
            JOptionPane.showMessageDialog(null,
                    "Todos los campos son obligatorios.",
                    "ERROR", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String strIngreso = form.getTxtFechaIngreso();
        String strSalida  = form.getTxtFechaSalida();

        LocalDate ingreso, salida;
        try {
            ingreso = LocalDate.parse(strIngreso, FMT);
            salida  = LocalDate.parse(strSalida,  FMT);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(null,
                    "Formato de fecha inválido.\nUse DD-MM-AAAA  (ejemplo: 15-06-2025).",
                    "ERROR", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!ingreso.isBefore(salida)) {
            JOptionPane.showMessageDialog(null,
                    "La fecha de ingreso debe ser anterior a la fecha de salida.",
                    "ERROR", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Reserva reservaActual  = listaReservas.get(fila);
        Habitacion habSeleccionada = form.getCmbHabitaciones();

        // Al modificar, excluimos la propia reserva del chequeo de conflicto
        Reserva conflicto = buscarConflicto(habSeleccionada, ingreso, salida,
                                            reservaActual.getIdReserva());

        if (conflicto != null) {
            LocalDate liberacion = LocalDate.parse(conflicto.getFechaSalida(), FMT);
            JOptionPane.showMessageDialog(null,
                    "⚠ La habitación " + habSeleccionada.getNumHabitacion()
                    + " (" + habSeleccionada.getTipo() + ") ya está reservada\n"
                    + "en ese período.\n\n"
                    + "Se libera el: " + liberacion.format(FMT)
                    + "  (reserva ID " + conflicto.getIdReserva() + ").",
                    "Habitación no disponible", JOptionPane.WARNING_MESSAGE);
            return;
        }

        reservaActual.setCliente(form.getCmbClientes());
        reservaActual.setHabitacion(habSeleccionada);
        reservaActual.setFechaIngreso(strIngreso);
        reservaActual.setFechaSalida(strSalida);

        arch.guardarReservas(listaReservas);
        actualizarTabla();
        form.limpiarCampos();

        JOptionPane.showMessageDialog(null,
                "Reserva modificada correctamente.",
                "OK", JOptionPane.INFORMATION_MESSAGE);
    }

    // ─── ELIMINAR ────────────────────────────────────────────────────────────

    public void eliminarReserva() {

        String id = JOptionPane.showInputDialog(null,
                "Ingrese el ID de la reserva a eliminar:");

        if (id == null) return;
        id = id.trim();

        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Debe ingresar el ID de la reserva.",
                    "ERROR", JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (int i = 0; i < listaReservas.size(); i++) {
            if (listaReservas.get(i).getIdReserva().equals(id)) {
                int conf = JOptionPane.showConfirmDialog(null,
                        "¿Está seguro de eliminar la reserva ID: " + id + "?",
                        "Confirmación", JOptionPane.YES_NO_OPTION);
                if (conf == JOptionPane.YES_OPTION) {
                    listaReservas.remove(i);
                    arch.guardarReservas(listaReservas);
                    actualizarTabla();
                    form.limpiarCampos();
                    JOptionPane.showMessageDialog(null,
                            "Reserva eliminada correctamente.",
                            "OK", JOptionPane.INFORMATION_MESSAGE);
                }
                return;
            }
        }

        JOptionPane.showMessageDialog(null,
                "No se encontró la reserva con ese ID.",
                "ERROR", JOptionPane.ERROR_MESSAGE);
    }

    private Reserva buscarConflicto(Habitacion hab,
                                    LocalDate nuevoIngreso,
                                    LocalDate nuevoSalida,
                                    String excluirId) {
        for (Reserva r : listaReservas) {

            // Ignorar la reserva que se está editando
            if (excluirId != null && r.getIdReserva().equals(excluirId)) continue;

            // Solo chequear reservas de esa habitación
            if (!r.getHabitacion().getNumHabitacion()
                   .equals(hab.getNumHabitacion())) continue;

            LocalDate ingresoExist, salidaExist;
            try {
                ingresoExist = LocalDate.parse(r.getFechaIngreso(), FMT);
                salidaExist  = LocalDate.parse(r.getFechaSalida(),  FMT);
            } catch (DateTimeParseException e) {
                continue; // fecha corrupta, se salta
            }

            // Solapamiento: los rangos se cruzan si uno empieza antes de que el otro termine
            boolean solapa = nuevoIngreso.isBefore(salidaExist)
                          && nuevoSalida.isAfter(ingresoExist);

            if (solapa) return r;
        }
        return null;
    }

    // ─── Métodos privados de apoyo ────────────────────────────────────────────

    private void actualizarTabla() {
        dtm.setRowCount(0);
        for (Reserva r : listaReservas) {
            dtm.addRow(new Object[]{
                r.getIdReserva(),
                r.getCliente().getNombre(),
                r.getHabitacion().getNumHabitacion(),
                r.getFechaIngreso(),
                r.getFechaSalida(),
                r.getPago()
            });
        }
    }

    private boolean camposVacios() {
        return form.getCmbClientes()     == null
            || form.getCmbHabitaciones() == null
            || form.getTxtFechaIngreso().trim().isEmpty()
            || form.getTxtFechaSalida().trim().isEmpty();
    }

    private String crearId() {
        int max = 0;
        for (Reserva r : listaReservas) {
            try {
                int val = Integer.parseInt(r.getIdReserva());
                if (val > max) max = val;
            } catch (NumberFormatException e) {
                // ID no numérico, ignorar
            }
        }
        return String.valueOf(max + 1);
    }
}