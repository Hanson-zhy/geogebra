package geogebra.plugin.jython;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.commands.AlgebraProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.main.Application;

public interface PythonScriptInterface {
	public void init(Application app);
	public void handleEvent(String eventType, GeoElement eventTarget);
	public void notifySelected(GeoElement geo, boolean addToSelection);
	public void toggleWindow();
	public boolean isWindowVisible();
	public void execute(String script);
}
