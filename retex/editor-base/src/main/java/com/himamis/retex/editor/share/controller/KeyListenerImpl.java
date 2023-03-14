package com.himamis.retex.editor.share.controller;

import com.himamis.retex.editor.share.event.KeyEvent;
import com.himamis.retex.editor.share.util.JavaKeyCodes;

/**
 * Key listener
 */
public class KeyListenerImpl {

	private InputController inputController;

	/**
	 * @param inputController
	 *            input controller
	 */
	public KeyListenerImpl(InputController inputController) {
		this.inputController = inputController;
	}

	/**
	 * @param keyEvent
	 *            event
	 * @param editorState
	 *            editor state
	 * @return whether event was handled
	 */
	public boolean onKeyPressed(KeyEvent keyEvent, EditorState editorState) {
		// Ctrl, not AltGr
		boolean ctrlPressed = ((keyEvent.getKeyModifiers()
				& KeyEvent.CTRL_MASK) > 0)
				&& ((keyEvent.getKeyModifiers() & KeyEvent.ALT_MASK) == 0);
		
		boolean shiftPressed = (keyEvent.getKeyModifiers() & KeyEvent.SHIFT_MASK) > 0;

		switch (keyEvent.getKeyCode()) {
		case JavaKeyCodes.VK_A:
			if (ctrlPressed) {
				editorState.selectAll();
				return true;
			}
			return false;
		case JavaKeyCodes.VK_V:
			if (ctrlPressed) {
				inputController.paste();
				return inputController.getMathField().useCustomPaste();
			}
			return false;
		case JavaKeyCodes.VK_C:
			if (ctrlPressed) {
				inputController.copy();
				return true;
			}
			return false;
		case JavaKeyCodes.VK_X:
			if (ctrlPressed) {
				inputController.copy();
				InputController.deleteSelection(editorState);
				return true;
			}
			return false;
		case JavaKeyCodes.VK_ESCAPE:
			// if math field doesn't have its own escape handler, blur it
			inputController.getMathField().blur();
			return true;
		case JavaKeyCodes.VK_HOME:
			if (shiftPressed) {
				editorState.selectToStart();
			} else {
				CursorController.firstField(editorState);
			}
			return true;
		case JavaKeyCodes.VK_END:
			if (shiftPressed) {
				editorState.selectToEnd();
			} else {
				CursorController.lastField(editorState);
			}
			return true;
		case JavaKeyCodes.VK_LEFT:
			boolean ret = CursorController.prevCharacter(editorState);
			if (shiftPressed) {
				editorState.extendSelection(true);
			} else {
				editorState.resetSelection();
			}
			return ret;
		case JavaKeyCodes.VK_RIGHT:
			ret = CursorController.nextCharacter(editorState);
			if (shiftPressed) {
				editorState.extendSelection(false);
			} else {
				editorState.resetSelection();
			}
			return ret;
		case JavaKeyCodes.VK_UP:
			return CursorController.upField(editorState);
		case JavaKeyCodes.VK_DOWN:
			return CursorController.downField(editorState);
		case JavaKeyCodes.VK_DELETE:
			if (!InputController.deleteSelection(editorState)) {
				inputController.delCharacter(editorState);
			}
			return true;
		case JavaKeyCodes.VK_BACK_SPACE:
			if (!InputController.deleteSelection(editorState)) {
				inputController.bkspCharacter(editorState);
			}
			return true;
		case JavaKeyCodes.VK_SHIFT:
			return false;
		case JavaKeyCodes.VK_OPEN_BRACKET:
			return false;
		case JavaKeyCodes.VK_TAB:
			return onTab(shiftPressed);
		default:
			// InputController.deleteSelection(editorState);
			return false;
		}
	}

	/**
	 * @param shiftDown
	 *            whether shift is pressed
	 * @return tab handling
	 */
	public boolean onTab(boolean shiftDown) {
		return inputController.handleTab(shiftDown);
	}

	/**
	 * @param ch
	 *            key
	 * @param editorState
	 *            current state
	 * @return whether event was handled
	 */
	public boolean onKeyTyped(char ch, EditorState editorState) {
		return inputController.handleChar(editorState, ch);
	}
}
