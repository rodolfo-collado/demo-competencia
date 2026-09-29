package ni.edu.uam.demo.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representa una cuenta bancaria de un cliente con número de cuenta, titular y saldo.
 */
public class CuentaBancaria {
    private String numeroCuenta;
    private String titular;
    private double saldo;
    private final List<Transaccion> historialTransacciones;

    public CuentaBancaria(String numeroCuenta, String titular, double saldoInicial) {
        if (numeroCuenta == null || numeroCuenta.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de cuenta no puede estar vacío.");
        }
        if (titular == null || titular.trim().isEmpty()) {
            throw new IllegalArgumentException("El titular no puede estar vacío.");
        }
        if (saldoInicial < 0) {
            throw new IllegalArgumentException("El saldo inicial no puede ser negativo.");
        }

        this.numeroCuenta = numeroCuenta.trim();
        this.titular = titular.trim();
        this.saldo = saldoInicial;
        this.historialTransacciones = new ArrayList<>();

        this.historialTransacciones.add(new Transaccion(
                TipoTransaccion.APERTURA,
                saldoInicial,
                saldoInicial,
                "Apertura de cuenta con saldo inicial"
        ));
    }

    public synchronized void depositar(double monto) {
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto a depositar debe ser mayor que cero.");
        }
        this.saldo += monto;
        this.historialTransacciones.add(new Transaccion(
                TipoTransaccion.DEPOSITO,
                monto,
                this.saldo,
                "Depósito realizado exitosamente"
        ));
    }

    public synchronized void retirar(double monto) {
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto a retirar debe ser mayor que cero.");
        }
        if (monto > this.saldo) {
            throw new IllegalArgumentException(String.format("Fondos insuficientes. Saldo actual: $%.2f, Monto solicitado: $%.2f", this.saldo, monto));
        }
        this.saldo -= monto;
        this.historialTransacciones.add(new Transaccion(
                TipoTransaccion.RETIRO,
                monto,
                this.saldo,
                "Retiro realizado exitosamente"
        ));
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        if (numeroCuenta == null || numeroCuenta.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de cuenta no puede estar vacío.");
        }
        this.numeroCuenta = numeroCuenta.trim();
    }

    public String getTitular() {
        return titular;
    }

    public void setTitular(String titular) {
        if (titular == null || titular.trim().isEmpty()) {
            throw new IllegalArgumentException("El titular no puede estar vacío.");
        }
        this.titular = titular.trim();
    }

    public double getSaldo() {
        return saldo;
    }

    public List<Transaccion> getHistorialTransacciones() {
        return Collections.unmodifiableList(historialTransacciones);
    }

    @Override
    public String toString() {
        return String.format("Cuenta #%s - %s ($%.2f)", numeroCuenta, titular, saldo);
    }
}
