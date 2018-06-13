package model.Analizadores;

import model.Archivos.Archivo;
import model.RepresentacionMemoria.Memoria;
import model.RepresentacionMemoria.TabladeEtiquetas;
import Excepciones.ErrorOCUNS;

public interface AnalizadorSintacticoySemantico {
	public void compilar(Archivo archivo,String direccionInicio)throws ErrorOCUNS;
	public Memoria getMemoria();
	public TabladeEtiquetas getTablaEtiqueta();
	public Reglas getReglas();
}
