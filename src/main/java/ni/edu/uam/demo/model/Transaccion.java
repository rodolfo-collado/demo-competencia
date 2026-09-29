package ni.edu.uam.demo.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Representa el registro de una transacción efectuada sobre una cuenta bancaria.
 */
public class Transaccion {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final LocalDateTime fechaHora;
    private final TipoTransaccion tipo;
    private final double monto;
    private final double saldoResultante;
    private final String descripcion;

    public Transaccion(TipoTransaccion tipo, double monto, double saldoResultante, String descripcion) {
        this.fechaHora = LocalDateTime.now();
        this.tipo = tipo;
        this.monto = monto;
        this.saldoResultante = saldoResultante;
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public String getFechaHoraFormateada() {
        return fechaHora.format(FORMATTER);
    }

    public TipoTransaccion getTipo() {
        return tipo;
    }

    public double getMonto() {
        return monto;
    }

    public double getSaldoResultante() {
        return saldoResultante;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | Monto: $%.2f | Saldo: $%.2f | %s",
                getFechaHoraFormateada(), tipo, monto, saldoResultante, descripcion);
    }
}
