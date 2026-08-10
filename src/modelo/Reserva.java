package modelo;

public class Reserva {
    private String idReserva;
    private Cliente cliente;
    private Habitacion habitacion;
    private String fechaIngreso;
    private String fechaSalida;
    private Pago pago;

    public Reserva() {
    }

    public Reserva(String idReserva, Cliente cliente, Habitacion habitacion,
                   String fechaIngreso, String fechaSalida, Pago pago) {
        this.idReserva   = idReserva;
        this.cliente     = cliente;
        this.habitacion  = habitacion;
        this.fechaIngreso = fechaIngreso;
        this.fechaSalida  = fechaSalida;
        this.pago        = pago;
    }

    public String getIdReserva()               { return idReserva; }
    public void setIdReserva(String idReserva) { this.idReserva = idReserva; }

    public String getFechaIngreso()                  { return fechaIngreso; }
    public void setFechaIngreso(String fechaIngreso) { this.fechaIngreso = fechaIngreso; }

    public String getFechaSalida()                 { return fechaSalida; }
    public void setFechaSalida(String fechaSalida) { this.fechaSalida = fechaSalida; }

    public Cliente getCliente()              { return cliente; }
    public void setCliente(Cliente cliente)  { this.cliente = cliente; }

    public Habitacion getHabitacion()                   { return habitacion; }
    public void setHabitacion(Habitacion habitacion)    { this.habitacion = habitacion; }

    public Pago getPago()          { return pago; }
    public void setPago(Pago pago) { this.pago = pago; }

    /*
    Devuelve el monto del pago como texto para mostrar en la tabla.
    Si no hay pago, devuelve "Sin pago".
    */
    public String getMontoStr() {
        if (pago == null) return "Sin pago";
        return "$" + String.format("%.2f", pago.getMonto());
    }

    //Devuelve el metodo del pago para mostrar en la tabla.
    
    public String getMetodoStr() {
        if (pago == null) return "Pendiente";
        return pago.getMetodoPago();
    }
}