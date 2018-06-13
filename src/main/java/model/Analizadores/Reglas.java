package model.Analizadores;

public interface Reglas {
	public boolean esTrivial(char tokenId);
	public String getIDTrivial(char tokenId);
	public int getIDSentencia(String lexema);
	public boolean esSentencia(String lexema);
	public String getLexemaSentencia(int id);
	public String getOpcode(int idOpcode);

}
