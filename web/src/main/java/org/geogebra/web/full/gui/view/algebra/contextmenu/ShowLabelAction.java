package org.geogebra.web.full.gui.view.algebra.contextmenu;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.scientific.LabelController;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.view.algebra.MenuAction;
import org.geogebra.web.full.main.AppWFull;

public class ShowLabelAction extends MenuAction {
	public ShowLabelAction() {
		super("ShowLabel", MaterialDesignResources.INSTANCE.label());
	}

	@Override
	public void execute(GeoElement geo, AppWFull app) {
		new LabelController().showLabel(geo);
		app.storeUndoInfo();
	}
}