package de.erichseifert.gral.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;

import de.erichseifert.gral.util.GraphicsUtils;

public class EditableLabel extends Label implements KeyListener {
	private boolean edited;
	private int caretPosition;
	private int markPosition;
	private Paint selectionBackground;
	private StringBuilder text;

	// TODO: Should superclass use StringBuilder?
	public EditableLabel() {
		this("");
	}

	public EditableLabel(String text) {
		super(text);
		this.text = new StringBuilder(text);
		selectionBackground = new Color(100, 199, 233, 120);
	}

	public boolean isEdited() {
		return edited;
	}

	public void setEdited(boolean edited) {
		this.edited = edited;
		setCaretPosition(text.length());
		setMarkPosition(text.length());
	}

	protected int getCaretPosition() {
		return caretPosition;
	}

	protected void setCaretPosition(int caretPosition) {
		this.caretPosition = caretPosition;
	}

	protected int getMarkPosition() {
		return markPosition;
	}

	protected void setMarkPosition(int markPosition) {
		this.markPosition = markPosition;
	}

	public Paint getSelectionBackground() {
		return selectionBackground;
	}

	public void setSelectionBackground(Paint selectionBackground) {
		this.selectionBackground = selectionBackground;
	}

	@Override
	public void drawComponents(DrawingContext context) {
		if (isEdited()) {
			drawCaret(context);
		}
		super.drawComponents(context);
	}

	protected void drawCaret(DrawingContext context) {
		Graphics2D g2d = context.getGraphics();
		AffineTransform txOld = g2d.getTransform();
		FontRenderContext fontRenderContext = g2d.getFontRenderContext();

		String outlineText = getText();
		int caretPosition = getCaretPosition();
		if (outlineText.isEmpty()) {
			outlineText = EMPTY_LABEL_OUTLINE_STRING;
			caretPosition = 0;
		}

		TextLayout layout = new TextLayout(outlineText, getFont(), fontRenderContext);
		Shape selectionShape = layout.getLogicalHighlightShape(getMarkPosition(), getCaretPosition());
		Shape[] caretShapes = layout.getCaretShapes(caretPosition);
		Shape caretShape = caretShapes[0];

		// Apply positioning
		g2d.translate(0, layout.getAscent());

		GraphicsUtils.fillPaintedShape(g2d, selectionShape, getSelectionBackground(), null);
		g2d.draw(caretShape);
		g2d.setTransform(txOld);
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (!isEdited()) {
			return;
		}

		int key = e.getKeyCode();
		int caretPosition = getCaretPosition();
		int markPosition = getMarkPosition();
		if (key == KeyEvent.VK_RIGHT) {
			caretPosition = Math.min(caretPosition + 1, text.length());
		} else if (key == KeyEvent.VK_LEFT) {
			caretPosition = Math.max(caretPosition - 1, 0);
		} else if (key == KeyEvent.VK_BACK_SPACE) {
			if (!getText().isEmpty()) {
				/*
				 * Bring start and end in correct order. Otherwise StringBuilder.delete will throw
				 * an ArrayIndexOutOfBoundsException.
				 */
				int startIndex = Math.min(markPosition, caretPosition);
				int endIndex = Math.max(markPosition, caretPosition);

				if (startIndex != endIndex) {
					// Delete multiple characters
					text.delete(startIndex, endIndex);
					caretPosition = startIndex;
					invalidate();
				} else if (startIndex > 0) {
					// Delete single character
					text.deleteCharAt(caretPosition - 1);
					caretPosition--;
					invalidate();
				}
			}
		} else if (key == KeyEvent.VK_HOME) {
			caretPosition = 0;
		} else if (key == KeyEvent.VK_END) {
			caretPosition = text.length();
		} else {
			char keyChar = e.getKeyChar();
			if (getFont().canDisplay(keyChar)) {
				text.insert(caretPosition, keyChar);
				caretPosition++;
				markPosition++;
				invalidate();
			}
		}
		if (!e.isShiftDown()) {
			markPosition = caretPosition;
		}
		setCaretPosition(caretPosition);
		setMarkPosition(markPosition);
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public String getText() {
		return text.toString();
	}

	@Override
	public void setText(String text) {
		this.text = new StringBuilder(text);
	}
}
