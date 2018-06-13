package view;

import java.io.File;
import java.util.Map;

public interface OCView {
	public void updateTextoTutorial(String texto);
	public void updateNombreArchivo(String fileName);
	public void updateContenidoArchivoActual(String contenido);
	public void updateCodigoCompilado(String codigo);
	public void updateRegistros(Map<Integer,String> registros);
	public void updateMemoria(Map<Integer,String> memoria);
	public void updatePCView(String pc);
	public void updateInstrucionView(String instruccion);
	public void updateLogs(String log);
	public void habilitarOpcionesdeEjecucion();
	public void habilitarOpcionesdeEjecucionPasoaPaso();
	public void deshabilitarOpcionesdeEjecucionPasoaPaso();
	public void habilitarGuardarPanel();
	public void deshabilitarGuardarPanel();
	public void habilitarOpcionesdeCompilacion();
	public void mostrarMemoria();
	public void mostrarAyuda();
	public void mostrarMensaje(String mensaje);
	public String pedirDialogo(String pedido);
	public boolean pedirAbrirArchivo();
	public boolean guardarArchivo();
	public File recuperarArchivo();
}
