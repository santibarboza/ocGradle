package model.Analizadores;

import java.util.Hashtable;

public class ReglasImpl implements Reglas{
	protected static ReglasImpl instancia=null;
	protected static Hashtable<String,Integer>opcodes;
	protected static Hashtable<Integer,String>opcodesInv;
	protected static Hashtable<Character,String> casosTriviales;
	
	private ReglasImpl(){
		cargarSentencias();
		cargarTriviales();
	}
	public static ReglasImpl getInstance(){
		if(instancia==null)
			instancia=new ReglasImpl();
		return instancia;
	}
	protected void cargarSentencias(){
		opcodes= new Hashtable<String,Integer>();
		opcodesInv= new Hashtable<Integer,String>();
		try{
			opcodes.put("add",0);
			opcodes.put("sub",1);
			opcodes.put("and",2);
			opcodes.put("xor",3);
			opcodes.put("lsh",4);
			opcodes.put("rsh",5);
			opcodes.put("load",6);
			opcodes.put("store",7);
			opcodes.put("lda",8);
			opcodes.put("jz",9);
			opcodes.put("jg",10);
			opcodes.put("call",11);
			opcodes.put("jmp",12);
			opcodes.put("inc",13);
			opcodes.put("dec",14);
			opcodes.put("hlt",15);
			opcodesInv.put(0,"add");
			opcodesInv.put(1,"sub");
			opcodesInv.put(2,"and");
			opcodesInv.put(3,"xor");
			opcodesInv.put(4,"lsh");
			opcodesInv.put(5,"rsh");
			opcodesInv.put(6,"load");
			opcodesInv.put(7,"store");
			opcodesInv.put(8,"lda");
			opcodesInv.put(9,"jz");
			opcodesInv.put(10,"jg");
			opcodesInv.put(11,"call");
			opcodesInv.put(12,"jmp");
			opcodesInv.put(13,"inc");
			opcodesInv.put(14,"dec");
			opcodesInv.put(15,"hlt");
		}catch(NullPointerException e){
		}
	}
	private void cargarTriviales(){
		casosTriviales= new Hashtable<Character,String>();
		try{
			casosTriviales.put('(',"T_ParenIni");
			casosTriviales.put(')',"T_ParenFin");
			casosTriviales.put(':',"T_Puntos");
			casosTriviales.put(',',"T_Coma");
		}catch(NullPointerException e){
		}
	}
	public boolean esTrivial(char tokenId){
		return (casosTriviales.get(tokenId)!=null);
	}
	public String getIDTrivial(char tokenId){
		return casosTriviales.get(tokenId);
	}
	public int getIDSentencia(String lexema){
		return opcodes.get(lexema);
	}
	public boolean esSentencia(String lexema){
		return opcodes.get(lexema)!=null;
	}
	public String getLexemaSentencia(int id){
		
		if(id>=0 && id<=5)
			return "T_SentenciaOperacion";
		else if(id==6 ||id==7)
				return "T_SentenciaMemoria";
			else if(id>=8 && id<=11)
					return "T_SentenciaAddress";
				else if(id>=12 && id<=14)
						return "T_SentenciaT3";
					else if(id==15)
							return "T_Halt";
						else
							return "ErrorSentencia";
	}
	@Override
	public String getOpcode(int idOpcode) {
		return opcodesInv.get(idOpcode);
	}
}
