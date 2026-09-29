package ni.edu.uam.demo.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import ni.edu.uam.demo.model.CuentaBancaria;
import ni.edu.uam.demo.model.Transaccion;
import ni.edu.uam.demo.service.BancoService;

public class BancoController {

    private final BancoService bancoService;
    private final ObservableList<CuentaBancaria> listaCuentas;
    private final ObservableList<Transaccion> listaTransacciones;

    // Formulario de nueva cuenta
    @FXML private TextField txtNuevoNumero;
    @FXML private TextField txtNuevoTitular;
    @FXML private TextField txtNuevoSaldoInicial;
    @FXML private Button btnCrearCuenta;

    // Formulario de operaciones
    @FXML private TextField txtCuentaOperacion;
    @FXML private TextField txtMontoOperacion;
    @FXML private Button btnDepositar;
    @FXML private Button btnRetirar;
    @FXML private Button btnConsultarSaldo;

    // Resumen de cuenta seleccionada
    @FXML private Label lblDetalleNumero;
    @FXML private Label lblDetalleTitular;
    @FXML private Label lblDetalleSaldo;

    // Tabla de Cuentas
    @FXML private TableView<CuentaBancaria> tblCuentas;
    @FXML private TableColumn<CuentaBancaria, String> colNumero;
    @FXML private TableColumn<CuentaBancaria, String> colTitular;
    @FXML private TableColumn<CuentaBancaria, Double> colSaldo;

    // Tabla de Historial
    @FXML private TableView<Transaccion> tblHistorial;
    @FXML private TableColumn<Transaccion, String> colHistFecha;
    @FXML private TableColumn<Transaccion, String> colHistTipo;
    @FXML private TableColumn<Transaccion, Double> colHistMonto;
    @FXML private TableColumn<Transaccion, Double> colHistSaldoResultante;
    @FXML private TableColumn<Transaccion, String> colHistDescripcion;

    public BancoController() {
        this.bancoService = new BancoService();
        this.listaCuentas = FXCollections.observableArrayList();
        this.listaTransacciones = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        // Configurar columnas de la tabla de cuentas
        colNumero.setCellValueFactory(new PropertyValueFactory<>("numeroCuenta"));
        colTitular.setCellValueFactory(new PropertyValueFactory<>("titular"));
        colSaldo.setCellValueFactory(new PropertyValueFactory<>("saldo"));
        tblCuentas.setItems(listaCuentas);

        // Configurar columnas de la tabla de historial
        colHistFecha.setCellValueFactory(new PropertyValueFactory<>("fechaHoraFormateada"));
        colHistTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colHistMonto.setCellValueFactory(new PropertyValueFactory<>("monto"));
        colHistSaldoResultante.setCellValueFactory(new PropertyValueFactory<>("saldoResultante"));
        colHistDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        tblHistorial.setItems(listaTransacciones);

        // Listener al seleccionar una cuenta de la tabla
        tblCuentas.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                seleccionarCuenta(newSelection);
            }
        });

        // Cuentas de demostración iniciales
        cargarDatosIniciales();
    }

    private void cargarDatosIniciales() {
        try {
            bancoService.crearCuenta("CTA-1001", "María Fernanda Gómez", 1500.00);
            bancoService.crearCuenta("CTA-1002", "Carlos Alberto Ruiz", 2800.50);
            bancoService.crearCuenta("CTA-1003", "Elena Beatriz Morales", 450.00);
            actualizarTablaCuentas();
            if (!listaCuentas.isEmpty()) {
                tblCuentas.getSelectionModel().selectFirst();
            }
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudieron cargar las cuentas iniciales: " + e.getMessage());
        }
    }

    @FXML
    public void onCrearCuenta() {
        String numero = txtNuevoNumero.getText();
        String titular = txtNuevoTitular.getText();
        String saldoTexto = txtNuevoSaldoInicial.getText();

        if (numero == null || numero.trim().isEmpty() ||
            titular == null || titular.trim().isEmpty() ||
            saldoTexto == null || saldoTexto.trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos incompletos", "Por favor ingrese el número de cuenta, titular y saldo inicial.");
            return;
        }

        try {
            double saldoInicial = Double.parseDouble(saldoTexto.trim());
            CuentaBancaria nuevaCuenta = bancoService.crearCuenta(numero, titular, saldoInicial);
            actualizarTablaCuentas();
            tblCuentas.getSelectionModel().select(nuevaCuenta);

            txtNuevoNumero.clear();
            txtNuevoTitular.clear();
            txtNuevoSaldoInicial.clear();

            mostrarAlerta(Alert.AlertType.INFORMATION, "Cuenta Creada",
                    String.format("La cuenta #%s para el titular '%s' fue creada exitosamente con un saldo inicial de $%.2f.",
                            nuevaCuenta.getNumeroCuenta(), nuevaCuenta.getTitular(), nuevaCuenta.getSaldo()));
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Valor Inválido", "El saldo inicial debe ser un número válido.");
        } catch (IllegalArgumentException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al crear cuenta", e.getMessage());
        }
    }

    @FXML
    public void onDepositar() {
        String numero = txtCuentaOperacion.getText();
        String montoTexto = txtMontoOperacion.getText();

        if (!validarInputsOperacion(numero, montoTexto)) {
            return;
        }

        try {
            double monto = Double.parseDouble(montoTexto.trim());
            double nuevoSaldo = bancoService.depositar(numero, monto);
            refrescarDatosCuenta(numero);
            txtMontoOperacion.clear();

            mostrarAlerta(Alert.AlertType.INFORMATION, "Depósito Exitoso",
                    String.format("Se depositaron $%.2f en la cuenta #%s.\nNuevo saldo: $%.2f", monto, numero, nuevoSaldo));
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Valor Inválido", "El monto debe ser un número válido.");
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error en Depósito", e.getMessage());
        }
    }

    @FXML
    public void onRetirar() {
        String numero = txtCuentaOperacion.getText();
        String montoTexto = txtMontoOperacion.getText();

        if (!validarInputsOperacion(numero, montoTexto)) {
            return;
        }

        try {
            double monto = Double.parseDouble(montoTexto.trim());
            double nuevoSaldo = bancoService.retirar(numero, monto);
            refrescarDatosCuenta(numero);
            txtMontoOperacion.clear();

            mostrarAlerta(Alert.AlertType.INFORMATION, "Retiro Exitoso",
                    String.format("Se retiraron $%.2f de la cuenta #%s.\nNuevo saldo: $%.2f", monto, numero, nuevoSaldo));
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Valor Inválido", "El monto debe ser un número válido.");
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error en Retiro", e.getMessage());
        }
    }

    @FXML
    public void onConsultarSaldo() {
        String numero = txtCuentaOperacion.getText();
        if (numero == null || numero.trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Número de cuenta requerido", "Por favor ingrese o seleccione un número de cuenta para consultar.");
            return;
        }

        try {
            CuentaBancaria cuenta = bancoService.obtenerCuentaRequerida(numero);
            refrescarDatosCuenta(cuenta.getNumeroCuenta());

            mostrarAlerta(Alert.AlertType.INFORMATION, "Consulta de Saldo",
                    String.format("Cuenta #%s\nTitular: %s\nSaldo Disponible: $%.2f",
                            cuenta.getNumeroCuenta(), cuenta.getTitular(), cuenta.getSaldo()));
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error en Consulta", e.getMessage());
        }
    }

    private void seleccionarCuenta(CuentaBancaria cuenta) {
        txtCuentaOperacion.setText(cuenta.getNumeroCuenta());
        lblDetalleNumero.setText(cuenta.getNumeroCuenta());
        lblDetalleTitular.setText(cuenta.getTitular());
        lblDetalleSaldo.setText(String.format("$%.2f", cuenta.getSaldo()));
        listaTransacciones.setAll(cuenta.getHistorialTransacciones());
    }

    private void refrescarDatosCuenta(String numeroCuenta) {
        actualizarTablaCuentas();
        bancoService.buscarCuenta(numeroCuenta).ifPresent(cuenta -> {
            tblCuentas.getSelectionModel().select(cuenta);
            seleccionarCuenta(cuenta);
        });
    }

    private void actualizarTablaCuentas() {
        listaCuentas.setAll(bancoService.obtenerTodasLasCuentas());
        tblCuentas.refresh();
    }

    private boolean validarInputsOperacion(String numero, String montoTexto) {
        if (numero == null || numero.trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Número de cuenta requerido", "Por favor seleccione o ingrese un número de cuenta.");
            return false;
        }
        if (montoTexto == null || montoTexto.trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Monto requerido", "Por favor ingrese el monto a operar.");
            return false;
        }
        return true;
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
