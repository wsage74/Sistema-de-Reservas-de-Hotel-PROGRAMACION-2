package controlador;

import archivo.archivo;
import javax.swing.JOptionPane;
import modelo.Hotel;
import vista.VentanaPrincipal;

public class HotelController {
    
    private VentanaPrincipal form;
    private Hotel hotel;
    private archivo arch;
    
    public HotelController(VentanaPrincipal form) {
        this.form = form;
        this.arch = new archivo();

        this.hotel = arch.leerHotel();
        if (this.hotel == null) {
        this.hotel = new Hotel("", "", "");
}
    }
    
    public Hotel getHotel() {
        return hotel;
    }
    
    private boolean camposVacios() {
        return form.getTxtIdHotel().isEmpty() ||
        form.getTxtNombreHotel().isEmpty() ||
        form.getTxtDireccionHotel().isEmpty();
    }
    
    public void agregarHotel() {
        try {
            if (camposVacios()) {
                JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios", "ERROR",
                JOptionPane.ERROR_MESSAGE);
                return;
            }

            String id = form.getTxtIdHotel();
            String nom = form.getTxtNombreHotel();
            String dir = form.getTxtDireccionHotel();

            this.hotel = new Hotel(id, nom, dir);
            form.setHotel(this.hotel);
            arch.guardarHotel(this.hotel);
            JOptionPane.showMessageDialog(null, "Hotel registrado correctamente", "ÉXITO",
            JOptionPane.INFORMATION_MESSAGE);
        
            form.bloquearCampos();
            arch.guardarHotel(hotel); // guarda en archivo

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "El ID debe ser un valor numérico", "ERROR",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
}
