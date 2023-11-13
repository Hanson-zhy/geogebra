package org.geogebra.common.spreadsheet.core;

public class ContextMenuItem {
	public enum Identifer {
		CUT("Cut"), COPY("Copy"),
		PASTE("Paste"),
		DELETE("Delete"),
		INSERT_ROW_ABOVE("ContextMenu.insertRowAbove"),
		INSERT_ROW_BELOW("ContextMenu.insertRowBelow"),
		DELETE_ROW("ContextMenu.deleteRow"),
		INSERT_COLUMN_LEFT("ContextMenu.insertColumnLeft"),
		INSERT_COLUMN_RIGHT("ContextMenu.insertColumnRight"),
		DELETE_COLUMN("ContextMenu.deleteColumn");

		public final String localizationKey;

		private Identifer(String localizationKey) {
			this.localizationKey = localizationKey;
		}

	}

	public final Identifer identifer;
	public final Runnable action;

	ContextMenuItem(Identifer identifer, Runnable action) {
		this.identifer = identifer;
		this.action = action;
	}

	public final String getLocalizationKey() {
		return identifer.localizationKey;
	}

	public final void performAction() {
		action.run();
	}
}
