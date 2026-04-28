package modelo;

import java.util.ArrayList;

public class Habitacion {
    private int numHabitacion;
    private String tipo;
    private double precio;

    private Hotel hotel;
    private ArrayList<Reserva> reservas;

    public Habitacion() {
        
    }

    public Habitacion(int numHabitacion, String tipo, double precio, Hotel hotel) {
        this.numHabitacion = numHabitacion;
        this.tipo = tipo;
        this.precio = precio;
        this.hotel = hotel;
        reservas = new ArrayList<>();
    }
  
    public int getNumHabitacion() {
        return numHabitacion;
    }

    public void setNumHabitacion(int numHabitacion) {
        this.numHabitacion = numHabitacion;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    public ArrayList<Reserva> getReservas() {
        return reservas;
    }

    public void setReservas(ArrayList<Reserva> reservas) {
        this.reservas = reservas;
    }

    
}
