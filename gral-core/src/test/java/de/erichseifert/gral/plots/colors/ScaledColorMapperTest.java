/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2011 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <michael.seifert[at]gmx.net>
 *
 * This file is part of GRAL.
 *
 * GRAL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GRAL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GRAL.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.erichseifert.gral.plots.colors;

import static org.junit.Assert.assertEquals;

import java.awt.Color;
import java.awt.Paint;

import org.junit.Before;
import org.junit.Test;

import de.erichseifert.gral.util.MathUtils;

public class ScaledColorMapperTest {
	private static final double DELTA = 1e-15;
	private ScaledColorMapper cm;

	private static final class ScaledColorMapperMock extends ScaledColorMapper {
		public Paint get(double value) {
			float v = (float) MathUtils.limit(scale(value), 0.0, 1.0);
			return new Color(v, v, v);
		}
	}

	@Before
	public void setUp() {
		cm = new ScaledColorMapperMock();
	}

	@Test
	public void testOffset() {
		assertEquals(0.0, cm.getOffset(), DELTA);
		cm.setOffset(42.0);
		assertEquals(42.0, cm.getOffset(), DELTA);
	}

	@Test
	public void testScale() {
		assertEquals(1.0, cm.getScale(), DELTA);
		cm.setScale(42.0);
		assertEquals(42.0, cm.getScale(), DELTA);
	}

	private static final void assertColor(double expected, Paint p) {
		Color c = (Color) p;
		int e = (int) MathUtils.limit(expected*255.0 + 0.5, 0, 255);
		assertEquals(e, c.getRed());
		assertEquals(e, c.getGreen());
		assertEquals(e, c.getBlue());
	}

	@Test
	public void testScaleOp() {
		for (double x=0.0; x<=1.0; x+=0.5) {
			assertColor(x, cm.get(x));
		}

		cm.setRange(0.25, 0.75);
		for (double x=0.0; x<=1.0; x+=0.5) {
			assertColor((x - 0.25)/0.5, cm.get(x));
		}
	}
}