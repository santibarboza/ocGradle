package model.RepresentacionMemoria;

import Excepciones.ErrorSintactico;

public interface TabladeEtiquetas {
	public void cargarEtiquetaPendiente(int direccion,String etiqueta);
	public void cargarDirecciondeEtiqueta(String etiqueta,int direccion);
	public void remplazarEtiquetas(Memoria memoria) throws ErrorSintactico;
	public String obtenerEtiqueta(int direccion);
	public void iniciar();
}
