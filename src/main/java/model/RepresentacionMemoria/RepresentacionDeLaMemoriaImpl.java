package model.RepresentacionMemoria;

public class RepresentacionDeLaMemoriaImpl implements RepresentacionDeLaMemoria {
	
	protected Memoria memoria;
	protected TabladeEtiquetas tabladeEtiquetas;
	
	public RepresentacionDeLaMemoriaImpl(Memoria memoria,TabladeEtiquetas tabladeEtiquetas){
		this.memoria=memoria;
		this.tabladeEtiquetas=tabladeEtiquetas;
	}
	@Override
	public Memoria getMemoria() {
		return memoria;
	}

	@Override
	public TabladeEtiquetas getTablaDeEtiquetas() {
		return tabladeEtiquetas;
	}

}
