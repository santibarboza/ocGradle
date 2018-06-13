package model.Analizadores;

import Excepciones.ErrorOCUNS;
import model.Archivos.Archivo;
import model.Tokens.Token;

public interface AnalizadorLexico {
	public Token getToken()throws ErrorOCUNS;
	public void iniciarConArchivo(Archivo archivo) throws ErrorOCUNS;
	public Reglas getReglas();
}
