/*
 *   Copyright 2020 Moros <https://github.com/PrimordialMoros>
 *   Copyright (C) sk89q <http://www.sk89q.com>
 *   Copyright (C) WorldEdit team and contributors
 *
 * 	  This file is part of Gaia.
 *
 *    Gaia is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    Gaia is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with Gaia.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.primordialmoros.gaia.util;

/**
 * An immutable 3-dimensional vector.
 */
public final class GaiaVector {

	public static final GaiaVector ZERO = new GaiaVector(0, 0, 0);
	public static final GaiaVector ONE = new GaiaVector(1, 1, 1);

	private static final int XZ_BOUNDS = 30_000_000;
	private static final int Y_MAX = 256;

	public static GaiaVector at(int x, int y, int z) {
		switch (y) {
			case 0:
				if (x == 0 && z == 0) return ZERO;
				break;
			case 1:
				if (x == 1 && z == 1) return ONE;
				break;
			default:
				break;
		}
		return new GaiaVector(x, y, z);
	}

	public static GaiaVector atXZClamped(int x, int y, int z, int minX, int maxX, int minZ, int maxZ) {
		if (minX > maxX || minZ > maxZ) throw new IllegalArgumentException("Minimum cannot be greater than maximum");
		return at(Math.max(minX, Math.min(maxX, x)), y, Math.max(minZ, Math.min(maxZ, z)));
	}

	private final int x;
	private final int y;
	private final int z;

	private GaiaVector(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public int getLength() {
		return x * y * z;
	}

	public GaiaVector add(GaiaVector other) {
		return add(other.x, other.y, other.z);
	}

	public GaiaVector add(int x, int y, int z) {
		return GaiaVector.at(this.x + x, this.y + y, this.z + z);
	}

	public GaiaVector subtract(GaiaVector other) {
		return subtract(other.x, other.y, other.z);
	}

	public GaiaVector subtract(int x, int y, int z) {
		return GaiaVector.at(this.x - x, this.y - y, this.z - z);
	}

	public GaiaVector divide(int n) {
		return GaiaVector.at(x / n, y / n, z / n);
	}

	public boolean containedWithin(GaiaVector min, GaiaVector max) {
		return x >= min.x && x <= max.x && y >= min.y && y <= max.y && z >= min.z && z <= max.z;
	}

	public GaiaVector getMinimum(GaiaVector v2) {
		return new GaiaVector(
			Math.min(x, v2.x),
			Math.min(y, v2.y),
			Math.min(z, v2.z)
		);
	}

	public GaiaVector getMaximum(GaiaVector v2) {
		return new GaiaVector(
			Math.max(x, v2.x),
			Math.max(y, v2.y),
			Math.max(z, v2.z)
		);
	}

	public static boolean isValidVector(GaiaVector v) {
		return isValidXZ(v.getX()) && isValidXZ(v.getZ()) && isValidY(v.getY());
	}

	private static boolean isValidXZ(int val) {
		return -XZ_BOUNDS < val && val < XZ_BOUNDS;
	}

	private static boolean isValidY(int val) {
		return 0 <= val && val < Y_MAX;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof GaiaVector)) return false;
		GaiaVector other = (GaiaVector) o;
		return other.x == this.x && other.y == this.y && other.z == this.z;
	}

	@Override
	public String toString() {
		return x + " " + y + " " + z;
	}
}
