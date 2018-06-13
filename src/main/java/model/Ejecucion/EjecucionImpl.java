package model.Ejecucion;

import Utils.Hexadecimal;
import model.OCModel;
import model.RepresentacionMemoria.Memoria;
import Excepciones.ErrorEjecucion;

public class EjecucionImpl implements Ejecucion{
	
	private OCModel ocModel;
	private Memoria memoria;
	private int pc,opcode,pcPAP;
	private int registroDIndex, registroSIndex, registroTIndex;
	private int bufferRegistroD,bufferRegistroS,bufferRegistroT;
	private int addr, offset,desplazamiento;
	private boolean condicion;
	private boolean noTerminoPrograma;

	public EjecucionImpl(Memoria memoria){
		this.memoria=memoria;
	}
	@Override
	public void ejecutarCodigoCompleto() throws ErrorEjecucion {
		int instruccion=0;
		boolean noTermina=true;
		
		iniciarPC();	
		while(noTermina) {
			instruccion=fetch();
			decode(instruccion);
			noTermina=execute();
			memory();
			writeBack();
		}
	}
	@Override
	public void iniciarEjecucionPasoaPaso() throws ErrorEjecucion {
		pcPAP=memoria.getDireccionInicio();
		noTerminoPrograma=true;
		ejecutarSiguienteInstruccion();
	}
	@Override
	public void ejecutarSiguienteInstruccion() throws ErrorEjecucion {
		int instruccion=0;
		pc= pcPAP;
		instruccion=fetch();
		decode(instruccion);
		noTerminoPrograma=execute();
		memory();
		writeBack();
		pcPAP=pc;
	}
	
	private void iniciarPC() {
		pc= memoria.getDireccionInicio();
	}
	private int fetch(){
		int instruccion =(memoria.leerMemoria(pc) << 8)+ memoria.leerMemoria(pc+1);
		pc = (pc + 2) & 255;
		ocModel.updatePCView(pc+"");
		ocModel.updateInstrucionView(memoria,pc);
		ocModel.updateLogs("Fetch:\n\t Nuevo PC="+pc+"\n\t Intruccion: "+Hexadecimal.hex(instruccion));
		return instruccion;	
	}
	private void decode(int instruccion) throws ErrorEjecucion{	
		leerIndices(instruccion);
		leerRegistros();
		calcularOffset();
		protegerRegistroF();
		String log="Decode:\n\t Opcode:"+opcode;
		log+="\n\t RegistroD: R"+Hexadecimal.hex(registroDIndex);
		log+="\n\t RegistroS: R"+Hexadecimal.hex(registroSIndex)+"="+Hexadecimal.hex2Dig(bufferRegistroS);
		log+="\n\t RegistroT: R"+Hexadecimal.hex(registroTIndex)+"="+Hexadecimal.hex2Dig(bufferRegistroT);
		log+="\n\t Direccion: 0x"+Hexadecimal.hex2Dig(addr);
		ocModel.updateLogs(log);
	}
	private void leerIndices(int instruccion) {
		opcode=(instruccion>>12)&15; 
		registroDIndex=(instruccion >> 8) & 15;
		registroSIndex=(instruccion >> 4) & 15;
		registroTIndex=(instruccion >> 0) & 15;
		addr=(instruccion >> 0) & 255;
	}
	private void leerRegistros() {
		bufferRegistroS=memoria.leerRegistro(registroSIndex);
		bufferRegistroT=memoria.leerRegistro(registroTIndex);
		bufferRegistroD=memoria.leerRegistro(registroDIndex);
	}
	private void calcularOffset() {
		offset=registroTIndex;
		if(offset>=8)
			offset=offset-16;
	}
	private void protegerRegistroF() throws ErrorEjecucion {
		if(registroDIndex==15 && opcode!=7 && opcode!=9 && opcode!=10&& opcode!=12)
			throw new ErrorEjecucion("El registro F es de solo lectura. error cuando pc= "+pc);
	}
	private boolean execute() throws ErrorEjecucion {
		if(opcode == 0xF)
			return false;		
		switch(opcode){
			case 0x0:
				realizarAdd();
				break;		
			case 0x1:
				realizarSub();
				break;		
			case 0x2:
				realizarAnd();
				break;		
			case 0x3:
				realizarOr();
				break;		
			case 0x4:
				realizarLeftShift();
				break;		
			case 0x5:
				realizarRightShift();
				break;		
			case 0x6:
				realizarLoad();
				break;		
			case 0x7:
				realizarStore();
				break;
			case 0x8:
				realizarLda();
				break;		
			case 0x9:
				realizarJumpZero();
				break;		
			case 0xA:
				realizarJumpGreater();
				break;		
			case 0xB:
				realizarCall();
				break;		
			case 0xC:
				realizarJump();
				break;		
			case 0xD:
				realizarInc();
				break;		
			case 0xE:
				realizarDec();
				break;	
			default:
				throw new ErrorEjecucion("Opcode Invalido cuando pc= "+Hexadecimal.hex2(pc));
		}
		controlarValorRegistroD();
		return true;
	}
	

	private void realizarAdd() throws ErrorEjecucion {
		bufferRegistroD=Hexadecimal.comp(bufferRegistroS)+Hexadecimal.comp(bufferRegistroT);
		if(bufferRegistroD>127 || bufferRegistroD< -128)
			throw new ErrorEjecucion("Overflow cuando PC="+Hexadecimal.hex2Dig(pc));
		bufferRegistroD=Hexadecimal.comp(bufferRegistroD);
		ocModel.updateLogs("Execute: ADD \t RD<-"+Hexadecimal.hex2(bufferRegistroD));	
	}
	private void realizarSub() throws ErrorEjecucion {
		bufferRegistroD=Hexadecimal.comp(bufferRegistroS)-Hexadecimal.comp(bufferRegistroT);
		if(bufferRegistroD>127 || bufferRegistroD< -128)
			throw new ErrorEjecucion("Overflow cuando PC="+Hexadecimal.hex2Dig(pc));
		bufferRegistroD=Hexadecimal.comp(bufferRegistroD);
		ocModel.updateLogs("Execute: SUB\n\t RD<-"+Hexadecimal.hex2(bufferRegistroD));
	}
	private void realizarAnd() {
		bufferRegistroD=(Hexadecimal.comp(bufferRegistroS) & Hexadecimal.comp(bufferRegistroT));
		ocModel.updateLogs("Execute: AND\n\t RD<-"+Hexadecimal.hex2(bufferRegistroD));
	}
	private void realizarOr() {
		bufferRegistroD=(Hexadecimal.comp(bufferRegistroS) ^ Hexadecimal.comp(bufferRegistroT));	
		ocModel.updateLogs("Execute: OR\n\t RD<-"+Hexadecimal.hex2(bufferRegistroD));
	}
	private void realizarLeftShift() {
		bufferRegistroD=(bufferRegistroS<<bufferRegistroT) & 256;
		ocModel.updateLogs("Execute: LSHIFT\n\t RD<-"+Hexadecimal.hex2(bufferRegistroD));
	}
	private void realizarRightShift() {
		bufferRegistroD=(bufferRegistroS>>bufferRegistroT) & 256;
		ocModel.updateLogs("Execute: RSHIFT\n\t RD<-"+Hexadecimal.hex2(bufferRegistroD));
	}
	private void realizarLoad() throws ErrorEjecucion {
		desplazamiento=(bufferRegistroS+Hexadecimal.comp(offset));
		ocModel.updateLogs("Execute: Load\n\t direccionEfectiva<-"+Hexadecimal.hex2(desplazamiento));
		
	}
	private void realizarStore() {
		desplazamiento=bufferRegistroD+Hexadecimal.comp(offset);
		ocModel.updateLogs("Execute: Store\n\t direccionEfectiva<-"+Hexadecimal.hex2(desplazamiento));
	}
	private void realizarLda() {
		bufferRegistroD=addr;
		ocModel.updateLogs("Execute: LDA\n\t RD<-"+Hexadecimal.hex2(bufferRegistroD));
	}
	private void realizarJumpZero() {
		condicion= (bufferRegistroD==0);
		ocModel.updateLogs("Execute: JZ\n\t condicion="+condicion);
	}
	private void realizarJumpGreater() {
		condicion=(Hexadecimal.comp(bufferRegistroD)>0);
		ocModel.updateLogs("Execute: JG\n\t condicion="+condicion);
	}
	private void realizarCall() {
		bufferRegistroD=pc;
		pc=addr;
		ocModel.updatePCView(pc+"");
		ocModel.updateLogs("Execute: CALL\n\t NuevoPC="+Hexadecimal.hex2(pc));
	}
	private void realizarJump() {
		pc=bufferRegistroD;
		ocModel.updatePCView(pc+"");
		ocModel.updateLogs("Execute: JUMP\n\t NuevoPC="+Hexadecimal.hex2(pc));
	}
	private void realizarInc() {
		bufferRegistroD=(Hexadecimal.comp(bufferRegistroD)+1) & 255;
		ocModel.updateLogs("Execute: INC\n\t RD<-"+Hexadecimal.hex2(bufferRegistroD));
	}
	private void realizarDec(){
		bufferRegistroD=(Hexadecimal.comp(bufferRegistroD)-1) & 255;
		ocModel.updateLogs("Execute: DEC\n\t RD<-"+Hexadecimal.hex2(bufferRegistroD));
	}
	private void controlarValorRegistroD() throws ErrorEjecucion {
		if(bufferRegistroD>255 || bufferRegistroD<-128)
			throw new ErrorEjecucion("Overflow cuando PC="+Hexadecimal.hex2(pc));
	}
	private void memory() throws ErrorEjecucion{
		if(esBranch())
			MemoryBranch();
		else if(esLoad())
			MemoryLoad();
		else if(esStore())
			MemoryStore();
		else
			ocModel.updateLogs("Memory: NOP");
	}
	private boolean esBranch(){
		return(opcode==9)||(opcode==10);
	}
	private void MemoryBranch(){
		if(condicion)
			pc=pc+Hexadecimal.comp(addr);
		ocModel.updatePCView(pc+"");
		ocModel.updateLogs("Memory: Branch\n\t PC="+Hexadecimal.hex2(pc));
	}
	private boolean esLoad(){
		return (opcode==6);
	}
	private void MemoryLoad() throws ErrorEjecucion{
		if(esInteraccionConUsuario()){
			read();
			ocModel.updateLogs("Memory: LOAD - Read()\n\t RD="+Hexadecimal.hex2(bufferRegistroD));
		}
		else{
			bufferRegistroD=memoria.leerMemoria(desplazamiento);
			ocModel.updateLogs("Memory: LOAD\n\t RD="+Hexadecimal.hex2(bufferRegistroD));
		}
	}
	private boolean esStore(){
		return (opcode==7);
	}
	private void MemoryStore(){
		if(esInteraccionConUsuario()){
			ocModel.mostrarMensaje(" Salida =  "+Hexadecimal.hex2(bufferRegistroS)+" = ("+Hexadecimal.comp(bufferRegistroS)+")d");
			ocModel.updateLogs("Memory: Store - print()\n\t RD="+Hexadecimal.hex2(bufferRegistroD));
		}else{
			memoria.escribirMemoria(desplazamiento, bufferRegistroS);
			ocModel.updateLogs("Memory: Store\n\t RD="+Hexadecimal.hex2(bufferRegistroD));
		}
	}
	private boolean esInteraccionConUsuario(){
		return desplazamiento==255;
	}
	private void read() throws ErrorEjecucion{
		try{
			String ax=ocModel.pedirDialogo("Ingrese un numero de 00 a FF:");
			bufferRegistroD=Integer.parseInt(ax, 16);
			}catch(NumberFormatException e){
				throw new ErrorEjecucion("El numero ingresado no es valido");		
			}
			if(bufferRegistroD<0 || bufferRegistroD>255)
				throw new ErrorEjecucion("Se ingreso un numero fuera de rango");
	}

	private void writeBack() {
		bufferRegistroD=bufferRegistroD&255;
		memoria.escribirRegistro(registroDIndex, bufferRegistroD);
		ocModel.updateMemoria();
		ocModel.updateRegistros();
		ocModel.updateLogs("WriteBack: Se actualizo RD\n");
	}
	@Override
	public void setModel(OCModel ocModel) {
		this.ocModel=ocModel;		
	}
	@Override
	public boolean hayCodigoParaEjecutar() {
		return noTerminoPrograma;
	}
	@Override
	public Memoria getMemoria() {
		return memoria;
	}

	
}
