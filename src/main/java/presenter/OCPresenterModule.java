package presenter;

import model.OCModelModule;
import view.OCView;
import view.OCViewModule;

public class OCPresenterModule {
	private static OCPresenterModule instance;

	  private OCPresenterModule() { }

	  public static OCPresenterModule getInstance() {
	    if (instance == null) {
	      instance = new OCPresenterModule();
	    }
	    return instance;
	  }
	  void startApplication() {
	    OCPresenter presenter = getOCPresenter();
	    OCView view = openOCWindowAndGetView(presenter);
	    presenter.setOCView(view);
	    setPresenterToModel(presenter);
	  }
	  private OCPresenter getOCPresenter() {
	    return new OCPresenterImpl(OCModelModule.getInstance().getOCModel());
	  }
	  private OCView openOCWindowAndGetView(OCPresenter presenter) {
	    return OCViewModule.getInstance().openOCWindow(presenter);
	  }
	  private void setPresenterToModel(OCPresenter presenter) {
		  OCModelModule.getInstance().getOCModel().setOCPresenter(presenter);
	  }
	  void startApplication(OCViewModule viewModule) {
	    OCPresenter presenter = getOCPresenter();
	    OCView view = viewModule.openOCWindow(presenter);
	    presenter.setOCView(view);
	    setPresenterToModel(presenter);
	  }
}
