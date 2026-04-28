package controlador;

import javax.swing.JOptionPane;
import modelo.Cliente;
import modelo.Habitacion;
import modelo.Reserva;

public class ReservaController {
    public void agregarReserva(int idReserva, Cliente cliente, Habitacion habitacion, String entrada, String salida) {
        
        Reserva r = new Reserva();
        
        r.setIdReserva(idReserva);
        r.setCliente(cliente);
        r.setHabitacion(habitacion);
        r.setFechaIngreso(entrada);
        r.setFechaSalida(salida);
        
        cliente.getReservas().add(r);
        habitacion.getReservas().add(r);
        
        JOptionPane.showConfirmDialog(null, "RESERVA REGISTRADA");
    }
}

//ESTABA PROBANDO NOMAS