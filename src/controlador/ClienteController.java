package controlador;

import archivo.archivo;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.Cliente;
import vista.ClienteForm;

public class ClienteController {
    
    private ClienteForm form;
    private ArrayList<Cliente> listaClientes;
    private DefaultTableModel dtm;
    private archivo arch;

    public ClienteController(ClienteForm form, DefaultTableModel dtm) {
        this.form = form;
        this.dtm = dtm;
        this.arch = new archivo();
        this.listaClientes = arch.leerClientes(); // carga desde archivo al iniciar
        actualizarTabla();
    }

    public void agregarCliente() {
        if (camposVacios()) {
            JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios", "ERROR",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String id = form.getTxtId();

        if (existeId(id)) {
            JOptionPane.showMessageDialog(null, "El ID ya existe, no se permiten duplicados", "ERROR",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String nom = form.getTxtNombre();
        String cel = form.getTxtCel();

        Cliente cliente = new Cliente(id, nom, cel);
        listaClientes.add(cliente);
        arch.guardarClientes(listaClientes); // guarda en archivo

        actualizarTabla();
        form.limpiarCampos();
    }

    public void buscarCliente() {
        String id = JOptionPane.showInputDialog(null, "Ingrese el ID a Buscar");

        if (id == null) return;

        if (id.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Debe ingresar un ID");
            return;
        }

        for (Cliente c : listaClientes) {
            if (c.getIdCliente().equals(id)) {

                for (int i = 0; i < dtm.getRowCount(); i++) {
                    if (dtm.getValueAt(i, 0).equals(id)) {

                        form.getTblLista().setRowSelectionInterval(i, i);

                        form.setTxtId(c.getIdCliente());
                        form.setTxtNombre(c.getNombre());
                        form.setTxtCel(c.getTelefono());
                        form.bloquearCampos();
                        return;
                    }
                }
            }
        }

        JOptionPane.showMessageDialog(null, "No se encontró el cliente con ese ID",
                "ERROR", JOptionPane.ERROR_MESSAGE);
    }

    public void modificar() {
        if (camposVacios()) {
            JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String id = form.getTxtId();

        Cliente clienteModificar = null;
        for (Cliente c : listaClientes) {
            if (id.equals(c.getIdCliente())) {
                clienteModificar = c;
                break;
            }
        }

        if (clienteModificar == null) {
            JOptionPane.showMessageDialog(null, "No se encontró el cliente con ese ID",
                    "ERROR", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String nom = form.getTxtNombre();
        String cel = form.getTxtCel();

        clienteModificar.setNombre(nom);
        clienteModificar.setTelefono(cel);
        arch.guardarClientes(listaClientes); // guarda en archivo

        actualizarTabla();
        form.desbloquearCampos();
        form.limpiarCampos();

        JOptionPane.showMessageDialog(null, "Cliente modificado correctamente",
                "OK", JOptionPane.INFORMATION_MESSAGE);
    }

    public void eliminarCliente() {
        String id = form.getTxtId();

        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Ingrese el ID del Cliente a eliminar",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (int i = 0; i < listaClientes.size(); i++) {
            if (listaClientes.get(i).getIdCliente().equals(id)) {

                int confirmacion = JOptionPane.showConfirmDialog(null,
                        "¿Está seguro de eliminar al Cliente: " + id + "?",
                        "Confirmación", JOptionPane.YES_NO_OPTION);

                if (confirmacion == JOptionPane.YES_OPTION) {
                    listaClientes.remove(i);
                    arch.guardarClientes(listaClientes); // guarda en archivo
                    actualizarTabla();
                    form.limpiarCampos();
                    form.desbloquearCampos();

                    JOptionPane.showMessageDialog(null, "Cliente eliminado correctamente",
                            "OK", JOptionPane.INFORMATION_MESSAGE);
                }
                return;
            }
        }

        JOptionPane.showMessageDialog(null, "No se encontró el Cliente con ese ID",
                "ERROR", JOptionPane.ERROR_MESSAGE);
    }

    private boolean camposVacios() {
        return form.getTxtId().isEmpty() ||
               form.getTxtNombre().isEmpty() ||
               form.getTxtCel().isEmpty();
    }

    private boolean existeId(String id) {
        for (Cliente c : listaClientes) {
            if (c.getIdCliente().equals(id)) return true;
        }
        return false;
    }

    private void actualizarTabla() {
        dtm.setRowCount(0);
        for (Cliente c : listaClientes) {
            Object[] fila = { c.getIdCliente(), c.getNombre(), c.getTelefono() };
            dtm.addRow(fila);
        }
    }
}