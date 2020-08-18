/*
 *   Copyright 2020 Moros <https://github.com/PrimordialMoros>
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

import org.bukkit.block.data.BlockData;

public final class GaiaData {

	private final BlockData[][][] data;
	private final GaiaVector size;

	public GaiaData(final GaiaVector size) {
		this.data = new BlockData[size.getX()][size.getY()][size.getZ()];
		this.size = size;
	}

	public BlockData getDataAt(GaiaVector v) {
		return getDataAt(v.getX(), v.getY(), v.getZ());
	}

	public BlockData getDataAt(int x, int y, int z) {
		return data[x][y][z];
	}

	public void setDataAt(GaiaVector v, BlockData blockData) {
		setDataAt(v.getX(), v.getY(), v.getZ(), blockData);
	}

	public void setDataAt(int x, int y, int z, BlockData blockData) {
		data[x][y][z] = blockData;
	}

	public GaiaVector getVector() {
		return size;
	}
}
