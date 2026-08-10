package controlador;

import archivo.archivo;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.Cliente;
import modelo.Habitacion;
import modelo.Pago;
import modelo.Reserva;
import vista.PagoForm;

public class PagoController {

    private PagoForm form;
    private ArrayList<Pago> listaPagos;
    private ArrayList<Reserva> listaReservas;
    private DefaultTableModel dtm;
    private archivo arch;

    // Mismo formato que utilizas en ReservaController
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public PagoController(PagoForm form, DefaultTableModel dtm) {
        this.form = form;
        this.dtm = dtm;
        this.arch = new archivo();

        ArrayList<Cliente> clientes = arch.leerClientes();
        ArrayList<Habitacion> habitaciones = arch.leerHabitaciones(null);

        this.listaReservas = arch.leerReservas(clientes, habitaciones);
        this.listaPagos = arch.leerPagos(this.listaReservas);

        actualizarTabla();
    }
    
    public void inicializar() {
        ArrayList<Reserva> sinPago = new ArrayList<>();

        for (Reserva r : listaReservas) {
            if (r.getPago() == null) {
                sinPago.add(r);
            }
        }

        form.cargarReservasCombo(sinPago);
    }

    // ── AGREGAR ──────────────────────────────────────────────────────

    public void agregarPago() {

        Reserva reserva = form.getReservaSeleccionada();
        String metodo = form.getMetodoSeleccionado();

        if (reserva == null) {
            JOptionPane.showMessageDialog(
                    null,
                    "Selecciona una reserva.",
                    "ERROR",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (reserva.getPago() != null) {
            JOptionPane.showMessageDialog(
                    null,
                    "Esta reserva ya tiene un pago registrado.",
                    "ERROR",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // ── Calcular cantidad de noches ──────────────────────────────
        long noches;

        try {
            LocalDate fechaIngreso =
                    LocalDate.parse(reserva.getFechaIngreso(), FMT);

            LocalDate fechaSalida =
                    LocalDate.parse(reserva.getFechaSalida(), FMT);

            noches = ChronoUnit.DAYS.between(
                    fechaIngreso,
                    fechaSalida
            );

        } catch (DateTimeParseException e) {

            JOptionPane.showMessageDialog(
                    null,
                    "Las fechas de la reserva no son válidas.",
                    "ERROR",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (noches <= 0) {
            JOptionPane.showMessageDialog(
                    null,
                    "La reserva debe tener al menos una noche.",
                    "ERROR",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // ── Obtener precio por noche ─────────────────────────────────
        if (reserva.getHabitacion() == null) {
            JOptionPane.showMessageDialog(
                    null,
                    "La reserva no tiene una habitación asociada.",
                    "ERROR",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        double precioPorNoche = reserva.getHabitacion().getPrecio();

        // ── Calcular total ───────────────────────────────────────────
        double monto = precioPorNoche * noches;

        // ── Crear pago ───────────────────────────────────────────────
        String idPago = "PAG-" + (listaPagos.size() + 1);

        Pago pago = new Pago(idPago, monto, metodo);

        pago.setReserva(reserva);
        reserva.setPago(pago);

        listaPagos.add(pago);

        arch.guardarPagos(listaPagos);
        arch.guardarReservas(listaReservas);

        actualizarTabla();
        form.limpiarCampos();
        inicializar();

        // ── Mostrar resumen ──────────────────────────────────────────
        JOptionPane.showMessageDialog(
                null,
                "Pago registrado correctamente.\n\n"
                + "ID: " + idPago + "\n"
                + "Noches: " + noches + "\n"
                + "Precio por noche: $" + String.format("%.2f", precioPorNoche) + "\n"
                + "Total a pagar: $" + String.format("%.2f", monto),
                "PAGO REGISTRADO",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // ── BUSCAR ───────────────────────────────────────────────────────

    public void buscarPago() {

        String id = JOptionPane.showInputDialog(
                null,
                "Ingrese el ID del pago a buscar:"
        );

        if (id == null || id.trim().isEmpty()) {
            return;
        }

        for (int i = 0; i < listaPagos.size(); i++) {

            Pago p = listaPagos.get(i);

            if (p.getIdPago().equals(id.trim())) {

                form.getTblLista().setRowSelectionInterval(i, i);

                JOptionPane.showMessageDialog(
                        null,
                        "Pago encontrado:\n" + p.mostrar(),
                        "PAGO",
                        JOptionPane.INFORMATION_MESSAGE
                );

                return;
            }
        }

        JOptionPane.showMessageDialog(
                null,
                "No se encontró el pago con ID: " + id,
                "ERROR",
                JOptionPane.ERROR_MESSAGE
        );
    }

    // ── ELIMINAR ─────────────────────────────────────────────────────

    public void eliminarPago() {

        int fila = form.getTblLista().getSelectedRow();

        if (fila < 0) {
            JOptionPane.showMessageDialog(
                    null,
                    "Selecciona un pago de la tabla para eliminar.",
                    "ERROR",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        int conf = JOptionPane.showConfirmDialog(
                null,
                "¿Eliminar el pago seleccionado?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );

        if (conf != JOptionPane.YES_OPTION) {
            return;
        }

        Pago pago = listaPagos.get(fila);

        if (pago.getReserva() != null) {
            pago.getReserva().setPago(null);
        }

        listaPagos.remove(fila);

        arch.guardarPagos(listaPagos);
        arch.guardarReservas(listaReservas);

        actualizarTabla();
        inicializar();

        JOptionPane.showMessageDialog(
                null,
                "Pago eliminado correctamente.",
                "OK",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // ── MÉTODOS PRIVADOS ─────────────────────────────────────────────

    private void actualizarTabla() {

        dtm.setRowCount(0);

        for (Pago p : listaPagos) {

            String idRes =
                    (p.getReserva() != null)
                    ? p.getReserva().getIdReserva()
                    : "N/A";

            String cli =
                    (p.getReserva() != null)
                    ? p.getReserva().getCliente().getNombre()
                    : "N/A";

            dtm.addRow(new Object[]{
                p.getIdPago(),
                idRes,
                cli,
                "$" + String.format("%.2f", p.getMonto()),
                p.getMetodoPago()
            });
        }
    }
}