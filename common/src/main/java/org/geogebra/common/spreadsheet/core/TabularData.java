package org.geogebra.common.spreadsheet.core;

import org.geogebra.common.gui.view.spreadsheet.HasTabularValues;

/**
 * Interacting with the structure and contents of tabular data.
 */
public interface TabularData<T> extends HasTabularValues<T> {

	// structure
	void reset(int rows, int columns);

	int numberOfRows();

	int numberOfColumns();

	void appendRows(int rows);

	void insertRowAt(int row);

	void deleteRowAt(int row);

	void appendColumns(int columns);

	void insertColumnAt(int column);

	void deleteColumnAt(int column);

	// content
	void setContent(int row, int column, Object content);

	String getColumnName(int column);

	void addChangeListener(TabularDataChangeListener listener);
}