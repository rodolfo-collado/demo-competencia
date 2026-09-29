package ni.edu.uam.demo.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CuentaBancariaTest {

    private CuentaBancaria cuenta;

    @BeforeEach
    void setUp() {
        cuenta = new CuentaBancaria("CTA-001", "Ana Martínez", 500.0);
    }

    @Test
    @DisplayName("Debe crear la cuenta con los datos iniciales correctos y registro de apertura")
    void testCreacionCuentaExitosa() {
        assertEquals("CTA-001", cuenta.getNumeroCuenta());
        assertEquals("Ana Martínez", cuenta.getTitular());
        assertEquals(500.0, cuenta.getSaldo(), 0.001);
        assertEquals(1, cuenta.getHistorialTransacciones().size());
        assertEquals(TipoTransaccion.APERTURA, cuenta.getHistorialTransacciones().get(0).getTipo());
    }

    @Test
    @DisplayName("Debe rechazar la creación con saldo negativo o campos vacíos")
    void testCreacionCuentaParametrosInvalidos() {
        assertThrows(IllegalArgumentException.class, () -> new CuentaBancaria("", "Ana", 100));
        assertThrows(IllegalArgumentException.class, () -> new CuentaBancaria(null, "Ana", 100));
        assertThrows(IllegalArgumentException.class, () -> new CuentaBancaria("CTA-002", "", 100));
        assertThrows(IllegalArgumentException.class, () -> new CuentaBancaria("CTA-002", null, 100));
        assertThrows(IllegalArgumentException.class, () -> new CuentaBancaria("CTA-002", "Ana", -50));
    }

    @Test
    @DisplayName("Debe depositar correctamente e incrementar el saldo")
    void testDepositarExitoso() {
        cuenta.depositar(250.0);
        assertEquals(750.0, cuenta.getSaldo(), 0.001);
        assertEquals(2, cuenta.getHistorialTransacciones().size());
        assertEquals(TipoTransaccion.DEPOSITO, cuenta.getHistorialTransacciones().get(1).getTipo());
        assertEquals(250.0, cuenta.getHistorialTransacciones().get(1).getMonto(), 0.001);
    }

    @Test
    @DisplayName("Debe rechazar depósito con montos menores o iguales a cero")
    void testDepositarMontoInvalido() {
        assertThrows(IllegalArgumentException.class, () -> cuenta.depositar(0));
        assertThrows(IllegalArgumentException.class, () -> cuenta.depositar(-100));
    }

    @Test
    @DisplayName("Debe retirar correctamente y decrementar el saldo")
    void testRetirarExitoso() {
        cuenta.retirar(200.0);
        assertEquals(300.0, cuenta.getSaldo(), 0.001);
        assertEquals(2, cuenta.getHistorialTransacciones().size());
        assertEquals(TipoTransaccion.RETIRO, cuenta.getHistorialTransacciones().get(1).getTipo());
        assertEquals(200.0, cuenta.getHistorialTransacciones().get(1).getMonto(), 0.001);
    }

    @Test
    @DisplayName("Debe rechazar retiro si los fondos son insuficientes")
    void testRetirarFondosInsuficientes() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> cuenta.retirar(600.0));
        assertTrue(exception.getMessage().contains("Fondos insuficientes"));
        assertEquals(500.0, cuenta.getSaldo(), 0.001);
    }

    @Test
    @DisplayName("Debe rechazar retiro con montos menores o iguales a cero")
    void testRetirarMontoInvalido() {
        assertThrows(IllegalArgumentException.class, () -> cuenta.retirar(0));
        assertThrows(IllegalArgumentException.class, () -> cuenta.retirar(-50));
    }
}
