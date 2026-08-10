# Sistema de Reservas de Hotel

Aplicación de escritorio desarrollada en **Java** con **Swing** para la gestión integral de un hotel: clientes, habitaciones, reservas y pagos.

## Funcionalidades

- **Gestión de clientes** — registrar, buscar, modificar y eliminar clientes.
- **Gestión de habitaciones** — control de habitaciones con estado dinámico (*Disponible*, *Reservada*, *Ocupada*) según sus reservas.
- **Gestión de reservas** — creación de reservas validando fechas y disponibilidad de la habitación, sin solapamientos.
- **Gestión de pagos** — registro del pago asociado a una reserva, con el monto sugerido automáticamente según el precio de la habitación.
- **Persistencia en archivos** — la información se guarda y se carga desde archivos de texto plano (`.txt`).

## Tecnologías

- **Java** (JDK 25)
- **Swing** — interfaz gráfica de escritorio
- **NetBeans IDE** — desarrollo y diseño visual de formularios (GUI Builder)
- **Apache Ant** — build del proyecto
- Persistencia mediante archivos de texto (`clientes.txt`, `habitaciones.txt`, `reservas.txt`, `pagos.txt`, `hotel.txt`)

## Arquitectura

El proyecto sigue una organización por capas al estilo **MVC**:

```
src/
├── modelo/        # Clases de dominio: Cliente, Habitacion, Hotel, Reserva, Pago
├── controlador/    # Lógica de negocio: ClienteController, HabController, ReservaController, PagoController, HotelController
├── vista/          # Interfaces gráficas (Swing): VentanaPrincipal, ClienteForm, HabForm, ReservaForm, PagoForm
└── archivo/        # Capa de persistencia en archivos de texto
```

## Instalación y ejecución

### Requisitos previos

- [JDK 25](https://www.oracle.com/java/technologies/downloads/) o superior instalado.
- [NetBeans IDE](https://netbeans.apache.org/download/index.html) (recomendado para abrir el proyecto directamente).

### Clonar el repositorio

```bash
git clone https://github.com/wsage74/Sistema-de-Reservas-de-Hotel-PROGRAMACION-2.git
cd Sistema-de-Reservas-de-Hotel-PROGRAMACION-2
```

### Ejecutar desde NetBeans

1. Abre NetBeans.
2. Ve a `File > Open Project...` y selecciona la carpeta del repositorio clonado.
3. Haz clic derecho sobre el proyecto y selecciona **Run**.

### Ejecutar desde la terminal (con Ant)

```bash
ant run
```

## Autores

- [@wsage](https://www.github.com/wsage74)

---

Proyecto desarrollado para la asignatura *Programación de Computadores II*.
