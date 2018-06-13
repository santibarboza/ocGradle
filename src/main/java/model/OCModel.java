package model;

import Excepciones.ErrorEjecucion;
import presenter.OCPresenter;
import model.Archivos.Archivo;
import model.Archivos.ArchivoAbstractFactory;
import model.RepresentacionMemoria.Memoria;

public interface OCModel {
	public ArchivoAbstractFactory getCreadorArchivo();
	public boolean compilaElArchivo(Archivo archivo, String DireccionInicio);
	public void ejecutarCodigoCompleto() throws ErrorEjecucion;
	public void habilitarEjecucionPasoaPaso() throws ErrorEjecucion;
	public void ejecutarSiguienteIntruccion() throws ErrorEjecucion;
	public boolean hayCodigoParaEjecutar();
	public String obtenerCodigoCompilado();
	public void setOCPresenter(OCPresenter ocPresenter) ;
	public void mostrarMensaje(String mensaje);
	public String pedirDialogo(String pedido);
	public void updateRegistros();
	public void updateMemoria();
	public void updatePCView(String pc);
	public void updateLogs(String log);
	public void updateInstrucionView(Memoria memoria,int pc);
}
