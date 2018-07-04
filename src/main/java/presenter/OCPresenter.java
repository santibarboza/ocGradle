package presenter;

import java.util.Map;

import view.OCView;

public interface OCPresenter {
	public void setOCView(OCView ocView);
	public void setEnableLog(boolean condicion);
	public void onEventAbrirArchivo();
	public void onEventGuardarArchivo();
	public void onEventCompilar(String contenido,String direccionInicio);
	public void onEventEjecutar(boolean esEjecucionTotal);
	public void onEventVerMemoria();
	public void onEventSiguientePaso();
	public void onEventVerAyuda();
	public void onEventModificoArchivo();
	public void mostrarMensaje(String mensaje);
	public String pedirDialogo(String pedido);
	public void updateRegistros(Map<Integer,String> registros);
	public void updateMemoria(Map<Integer,String> memoria);
	public void updatePCView(String pc);
	public void updateInstrucionView(String instruccion);
	public void updateLogs(String log);
}
