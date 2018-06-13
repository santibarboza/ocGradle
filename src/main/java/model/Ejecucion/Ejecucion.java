package model.Ejecucion;

import model.OCModel;
import model.RepresentacionMemoria.Memoria;
import Excepciones.ErrorEjecucion;

public interface Ejecucion {
	public void ejecutarCodigoCompleto() throws ErrorEjecucion;
	public void iniciarEjecucionPasoaPaso() throws ErrorEjecucion;
	public void ejecutarSiguienteInstruccion() throws ErrorEjecucion;
	public boolean hayCodigoParaEjecutar();
	public void setModel(OCModel ocModel);
	public Memoria getMemoria();
}
