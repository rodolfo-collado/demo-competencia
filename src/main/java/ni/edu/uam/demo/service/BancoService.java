package ni.edu.uam.demo.service;

import ni.edu.uam.demo.model.CuentaBancaria;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Servicio encargado de gestionar las cuentas bancarias y coordinar las operaciones del banco.
 */
public class BancoService {
    private final Map<String, CuentaBancaria> cuentas;

    public BancoService() {
        this.cuentas = new LinkedHashMap<>();
    }

    /**
     * Registra una nueva cuenta bancaria en el sistema.
     */
    public synchronized CuentaBancaria crearCuenta(String numeroCuenta, String titular, double saldoInicial) {
        if (numeroCuenta == null || numeroCuenta.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de cuenta no puede ser nulo ni vacío.");
        }
        String numeroNormalizado = numeroCuenta.trim();
        if (cuentas.containsKey(numeroNormalizado)) {
            throw new IllegalArgumentException(String.format("Ya existe una cuenta con el número '%s'.", numeroNormalizado));
        }

        CuentaBancaria nuevaCuenta = new CuentaBancaria(numeroNormalizado, titular, saldoInicial);
        cuentas.put(numeroNormalizado, nuevaCuenta);
        return nuevaCuenta;
    }

    /**
     * Busca una cuenta bancaria por su número.
     */
    public synchronized Optional<CuentaBancaria> buscarCuenta(String numeroCuenta) {
        if (numeroCuenta == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(cuentas.get(numeroCuenta.trim()));
    }

    /**
     * Obtiene una cuenta bancaria o lanza excepción si no existe.
     */
    public synchronized CuentaBancaria obtenerCuentaRequerida(String numeroCuenta) {
        return buscarCuenta(numeroCuenta)
                .orElseThrow(() -> new IllegalArgumentException(String.format("No se encontró la cuenta con el número '%s'.", numeroCuenta)));
    }

    /**
     * Realiza un depósito en la cuenta especificada.
     */
    public synchronized double depositar(String numeroCuenta, double monto) {
        CuentaBancaria cuenta = obtenerCuentaRequerida(numeroCuenta);
        cuenta.depositar(monto);
        return cuenta.getSaldo();
    }

    /**
     * Realiza un retiro de la cuenta especificada.
     */
    public synchronized double retirar(String numeroCuenta, double monto) {
        CuentaBancaria cuenta = obtenerCuentaRequerida(numeroCuenta);
        cuenta.retirar(monto);
        return cuenta.getSaldo();
    }

    /**
     * Consulta el saldo actual de la cuenta especificada.
     */
    public synchronized double consultarSaldo(String numeroCuenta) {
        CuentaBancaria cuenta = obtenerCuentaRequerida(numeroCuenta);
        return cuenta.getSaldo();
    }

    /**
     * Retorna una lista inmutable con todas las cuentas registradas.
     */
    public synchronized List<CuentaBancaria> obtenerTodasLasCuentas() {
        return Collections.unmodifiableList(new ArrayList<>(cuentas.values()));
    }

    /**
     * Verifica si una cuenta existe en el sistema.
     */
    public synchronized boolean existeCuenta(String numeroCuenta) {
        if (numeroCuenta == null) {
            return false;
        }
        return cuentas.containsKey(numeroCuenta.trim());
    }
}
