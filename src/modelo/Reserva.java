package modelo;

public class Reserva {
    private String idReserva;
    private Cliente cliente;
    private Habitacion habitacion;
    private String fechaIngreso;
    private String fechaSalida;
    private String pago; //PRUEBA, SE MODIFICA LUEGO DE CREAR MODULO PAGO

    public Reserva() {
    }

    public Reserva(String idReserva, Cliente cliente, Habitacion habitacion, String fechaIngreso, String fechaSalida, String pago) {
        this.idReserva = idReserva;
        this.cliente = cliente;
        this.habitacion = habitacion;
        this.fechaIngreso = fechaIngreso;
        this.fechaSalida = fechaSalida;
        this.pago = pago;
    }
    
    public String getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(String idReserva) {
        this.idReserva = idReserva;
    }

    public String getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(String fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public String getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(String fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente pliente) {
        this.cliente = pliente;
    }

    public Habitacion getHabitacion() {
        return habitacion;
    }

    public void setHabitacion(Habitacion habitacion) {
        this.habitacion = habitacion;
    }

    public String getPago() {
        return pago;
    }

    public void setPago(String pago) {
        this.pago = pago;
    }
}
