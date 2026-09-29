package ni.edu.uam.demo.service;

import ni.edu.uam.demo.model.CuentaBancaria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BancoServiceTest {

    private BancoService bancoService;

    @BeforeEach
    void setUp() {
        bancoService = new BancoService();
    }

    @Test
    @DisplayName("Debe crear y registrar cuentas correctamente")
    void testCrearCuenta() {
        CuentaBancaria cuenta = bancoService.crearCuenta("001", "Lucía Torres", 1000.0);
        assertNotNull(cuenta);
        assertEquals("001", cuenta.getNumeroCuenta());
        assertEquals("Lucía Torres", cuenta.getTitular());
        assertEquals(1000.0, cuenta.getSaldo(), 0.001);
        assertEquals(1, bancoService.obtenerTodasLasCuentas().size());
        assertTrue(bancoService.existeCuenta("001"));
    }

    @Test
    @DisplayName("Debe rechazar la creación de cuentas con números duplicados")
    void testCrearCuentaDuplicada() {
        bancoService.crearCuenta("001", "Lucía Torres", 1000.0);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> bancoService.crearCuenta("001", "Pedro Gómez", 500.0));
        assertTrue(ex.getMessage().contains("Ya existe una cuenta"));
    }

    @Test
    @DisplayName("Debe depositar a través del servicio y actualizar el saldo")
    void testDepositarServicio() {
        bancoService.crearCuenta("002", "Mario Mendoza", 300.0);
        double nuevoSaldo = bancoService.depositar("002", 200.0);

        assertEquals(500.0, nuevoSaldo, 0.001);
        assertEquals(500.0, bancoService.consultarSaldo("002"), 0.001);
    }

    @Test
    @DisplayName("Debe retirar a través del servicio y actualizar el saldo")
    void testRetirarServicio() {
        bancoService.crearCuenta("003", "Valeria Vega", 800.0);
        double nuevoSaldo = bancoService.retirar("003", 300.0);

        assertEquals(500.0, nuevoSaldo, 0.001);
        assertEquals(500.0, bancoService.consultarSaldo("003"), 0.001);
    }

    @Test
    @DisplayName("Debe lanzar excepción al operar sobre una cuenta inexistente")
    void testOperacionCuentaInexistente() {
        assertThrows(IllegalArgumentException.class, () -> bancoService.depositar("999", 100.0));
        assertThrows(IllegalArgumentException.class, () -> bancoService.retirar("999", 100.0));
        assertThrows(IllegalArgumentException.class, () -> bancoService.consultarSaldo("999"));
    }
}
