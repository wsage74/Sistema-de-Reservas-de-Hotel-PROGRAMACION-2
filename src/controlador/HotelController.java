package controlador;

import javax.swing.JOptionPane;
import modelo.Habitacion;
import modelo.Hotel;

public class HotelController {

    public void agregarHabitacion(Hotel hotel, Habitacion habitacion) {

        habitacion.setHotel(hotel);
        hotel.getHabitaciones().add(habitacion);
        
        JOptionPane.showConfirmDialog(null, "HABITACION REGISTRADA");
    }
}
