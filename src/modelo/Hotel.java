package modelo;

import java.util.ArrayList;

public class Hotel {
    private String idHotel;
    private String nombre;
    private String direccion;
    
    private ArrayList<Habitacion> habitaciones;

    public Hotel() {
        
    }
    
    public Hotel(String idHotel, String nombre, String direccion) {
        this.idHotel = idHotel;
        this.nombre = nombre;
        this.direccion = direccion;
        habitaciones = new ArrayList<>();
    }
        
    public String getIdHotel() {
        return idHotel;
    }

    public void setIdHotel(String idHotel) {
        this.idHotel = idHotel;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public ArrayList getHabitaciones() {
        return habitaciones;
    }

    public void setHabitaciones(ArrayList habitaciones) {
        this.habitaciones = habitaciones;
    }
    
    public boolean esValido() {
        return idHotel != null && !idHotel.isEmpty()
            && nombre != null && !nombre.isEmpty()
            && direccion != null && !direccion.isEmpty();
    }
    
}
