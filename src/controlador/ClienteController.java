package controlador;

import modelo.Cliente;

public class ClienteController {
    public void crearCliente(int idCliente, String nombre, String telefono) {
        
        Cliente c = new Cliente();
        
        c.setIdCliente(idCliente);
        c.setNombre(nombre);
        c.setTelefono(telefono);
    }
    
    public String mostrarClientes() {
        Cliente c = new Cliente();
        String resultado = "id: " + c.getIdCliente() + "\n" +
                "nombre: " + c.getNombre() + "\n" +
                "telefono: " + c.getTelefono();
        return resultado;
    }
}
