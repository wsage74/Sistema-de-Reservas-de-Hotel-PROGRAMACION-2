package modelo;

import java.util.ArrayList;

public class Cliente {
    private String idCliente;
    private String nombre;
    private String telefono;
            
    private ArrayList<Reserva> reservas;
    
    public Cliente() {
        
    }

    public Cliente(String idCliente, String nombre, String telefono) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.telefono = telefono;
        reservas = new ArrayList<>();
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public ArrayList<Reserva> getReservas() {
        return reservas;
    }

    public void setReservas(ArrayList reservas) {
        this.reservas = reservas;
    }   
    
    @Override
    public String toString() {
        return idCliente + " - " + nombre;
    }
}
