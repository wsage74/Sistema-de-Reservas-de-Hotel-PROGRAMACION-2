package modelo;

public class Pago {
    private String idPago;
    private double monto;
    private String metodoPago;
    private Reserva reserva;

    public Pago() {
    }

    public Pago(String idPago, double monto, String metodoPago) {
        this.idPago     = idPago;
        this.monto      = monto;
        this.metodoPago = metodoPago;
    }

    public String getIdPago()                  { return idPago; }
    public void setIdPago(String idPago)       { this.idPago = idPago; }

    public double getMonto()                   { return monto; }
    public void setMonto(double monto)         { this.monto = monto; }

    public String getMetodoPago()                    { return metodoPago; }
    public void setMetodoPago(String metodoPago)     { this.metodoPago = metodoPago; }

    public Reserva getReserva()                { return reserva; }
    public void setReserva(Reserva reserva)    { this.reserva = reserva; }

    public String mostrar() {
        return "ID: " + idPago + "  |  $" + String.format("%.2f", monto)
                + "  |  " + metodoPago;
    }
}