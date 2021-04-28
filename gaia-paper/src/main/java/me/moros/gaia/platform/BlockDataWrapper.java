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

package me.moros.gaia.platform;

import org.bukkit.block.data.BlockData;
import org.checkerframework.checker.nullness.qual.NonNull;

public class BlockDataWrapper implements GaiaBlockData {
  private final BlockData blockData;

  public BlockDataWrapper(@NonNull BlockData blockData) {
    this.blockData = blockData;
  }

  public @NonNull BlockData get() {
    return blockData;
  }

  @Override
  public @NonNull String getAsString() {
    return blockData.getAsString();
  }
}
