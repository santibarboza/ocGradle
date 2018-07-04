package presenter;

import java.util.Map;

import Excepciones.ErrorEjecucion;
import Excepciones.ErrorOCUNS;
import view.OCView;
import model.OCModel;
import model.Archivos.Archivo;
import model.Archivos.ArchivoAbstractFactory;

public class OCPresenterImpl implements OCPresenter {

		private OCModel ocModel;
		private OCView ocView;
		private Archivo archivoActual;
		private boolean enableLog;
		
		OCPresenterImpl(OCModel ocModel) {
			this.ocModel = ocModel;
			this.enableLog=true;
		}
		public void setOCView(OCView ocView) {
			this.ocView = ocView;
		}
		public void onEventAbrirArchivo() {
			if(ocView.pedirAbrirArchivo()){
				updateArchivoActual();
				String fileName=archivoActual.getName();
				ocView.habilitarOpcionesdeCompilacion();
				ocView.updateTextoTutorial("Archivo Cargado con Exito! Puede Compilarlo o Abrir uno nuevo");
				ocView.updateNombreArchivo(fileName);
				updateContenidoArchivoActual(fileName);
			}
		}
		@Override
		public void onEventGuardarArchivo() {
			if(ocView.guardarArchivo()){
				updateArchivoActual();
				String fileName=archivoActual.getName();
				ocView.habilitarOpcionesdeCompilacion();
				ocView.updateTextoTutorial("Archivo Guardado con Exito! Puede Compilarlo o Abrir uno nuevo");
				ocView.updateNombreArchivo(fileName);
			}
		}
		private void updateArchivoActual(){
			try{
				ArchivoAbstractFactory maker= ocModel.getCreadorArchivo();
				archivoActual=maker.crearArchivo(ocView.recuperarArchivo());
			}catch(ErrorOCUNS error){
				ocView.mostrarMensaje(error.getMessage());
			}
		}
		private void updateContenidoArchivoActual(String fileName) {
			try{
				ocView.updateContenidoArchivoActual(ocModel.getCreadorArchivo().cat(fileName));
			}catch(ErrorOCUNS error){
				ocView.mostrarMensaje(error.getMessage());
			}
		}
		@Override
		public void onEventCompilar(String contenido,String direccionInicio) {
			updateArchivoActual(archivoActual.getName(),contenido);
			realizarCompilacion(direccionInicio);
		}
		private void realizarCompilacion(String direccionInicio) {
			if(ocModel.compilaElArchivo(archivoActual, direccionInicio)){
				ocView.habilitarOpcionesdeEjecucion();
				ocView.updateCodigoCompilado(ocModel.obtenerCodigoCompilado());
				ocView.mostrarMensaje("El Archivo se compilo correctamente");
			}
			
		}
		private void updateArchivoActual(String filename, String contenido) {
			try{
				ArchivoAbstractFactory maker= ocModel.getCreadorArchivo();
				archivoActual=maker.crearArchivo(filename,contenido);
			}catch(ErrorOCUNS error){
				ocView.mostrarMensaje(error.getMessage());
			}			
		}
		@Override
		public void onEventEjecutar(boolean esEjecucionTotal) {
			try {
				if(esEjecucionTotal)
					ocModel.ejecutarCodigoCompleto();
				else{
					ocModel.habilitarEjecucionPasoaPaso();
					ocView.habilitarOpcionesdeEjecucionPasoaPaso();
				}
			} catch (ErrorEjecucion e) {
				ocView.mostrarMensaje(e.getMessage());
			}
		}
		@Override
		public void onEventVerMemoria() {
			ocView.mostrarMemoria();
		}
		@Override
		public void onEventSiguientePaso() {
			try {
				if(ocModel.hayCodigoParaEjecutar())
					ocModel.ejecutarSiguienteIntruccion();
				else
					ocView.deshabilitarOpcionesdeEjecucionPasoaPaso();
			} catch (ErrorEjecucion e) {
				ocView.mostrarMensaje(e.getMessage());
			}
		}
		@Override
		public void onEventVerAyuda() {
			ocView.mostrarAyuda();
		}
		@Override
		public void onEventModificoArchivo() {
			if(archivoActual!=null)
				ocView.updateNombreArchivo(archivoActual.getName()+"*");
			ocView.habilitarGuardarPanel();
			
		}
		//Funciones Model->View
		@Override
		public void mostrarMensaje(String mensaje) {
			ocView.mostrarMensaje(mensaje);
			updateLogs("Mensaje: "+mensaje);
		}
		@Override
		public String pedirDialogo(String pedido) {
			return ocView.pedirDialogo(pedido);
		}
		@Override
		public void updateRegistros(Map<Integer,String> registros) {
			ocView.updateRegistros(registros);
		}
		@Override
		public void updateMemoria(Map<Integer,String> memoria) {
			ocView.updateMemoria(memoria);
		}
		@Override
		public void updatePCView(String pc) {
			ocView.updatePCView(pc);
		}
		@Override
		public void updateInstrucionView(String instruccion) {
			ocView.updateInstrucionView(instruccion);			
		}
		@Override
		public void updateLogs(String log) {
			if(enableLog)
				ocView.updateLogs(log);
		}
		@Override
		public void setEnableLog(boolean condicion){
			this.enableLog=condicion;
		}
		
}
