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

import com.github.primordialmoros.gaia.Arena;
import com.github.primordialmoros.gaia.Gaia;
import com.github.primordialmoros.gaia.io.GaiaIO;
import com.github.primordialmoros.gaia.util.functional.GaiaRunnableInfo;
import com.github.primordialmoros.gaia.util.metadata.ArenaMetadata;
import com.github.primordialmoros.gaia.util.metadata.ChunkMetadata;
import com.github.primordialmoros.gaia.util.metadata.GaiaMetadata;
import com.github.primordialmoros.gaia.util.metadata.Metadatable;
import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * A chunk aligned GaiaRegion.
 */
public class GaiaChunk implements Metadatable {

	public static final Comparator<GaiaChunk> ZX_ORDER = Comparator.comparingInt(GaiaChunk::getZ).thenComparingInt(GaiaChunk::getX);

	private final UUID id;

	private final Arena parent;
	private final GaiaRegion chunk;
	private final int chunkX, chunkZ;

	private ChunkMetadata meta;

	private boolean reverting;

	public GaiaChunk(final UUID id, final Arena parent, final GaiaRegion region) {
		this.id = id;
		this.parent = parent;
		chunkX = region.getMinimumPoint().getX() / 16;
		chunkZ = region.getMinimumPoint().getZ() / 16;
		chunk = region;
		reverting = false;
	}

	public UUID getId() {
		return id;
	}

	public Arena getParent() {
		return parent;
	}

	public int getX() {
		return chunkX;
	}

	public int getZ() {
		return chunkZ;
	}

	public GaiaRegion getRegion() {
		return chunk;
	}

	/**
	 * Attempts to load the chunk and analyze blocks based on passed info.
	 * It will analyze up to a maximum amount of blocks per tick as defined in passed info.
	 * If there are more blocks left to analyze it will continue in the next tick.
	 *
	 * @param info the object containing the info
	 */
	private void analyze(final GaiaRunnableInfo info, final GaiaData data) {
		PaperLib.getChunkAtAsync(info.world, chunkX, chunkZ).thenAccept(result -> {
			GaiaVector relative, real;
			BlockData d;
			int counter = 0;
			while (++counter <= info.maxTransactions && info.it.hasNext()) {
				relative = info.it.next();
				real = chunk.getMinimumPoint().add(relative);
				d = info.world.getBlockAt(real.getX(), real.getY(), real.getZ()).getBlockData();
				data.setDataAt(relative, d);
			}
			if (info.it.hasNext()) {
				Bukkit.getScheduler().runTaskLater(Gaia.getPlugin(), () -> analyze(info, data), 1);
			} else {
				Bukkit.getScheduler().runTaskAsynchronously(Gaia.getPlugin(), () -> {
					String hash = GaiaIO.getInstance().saveData(this, data);
					if (hash.equals((meta.hash))) {
						ArenaMetadata m = (ArenaMetadata) parent.getMetadata();
						m.chunks.add(meta);
					}
				});
			}
		});
	}

	/**
	 * Attempts to load the chunk and revert blocks based on passed info.
	 * It will revert up to a maximum amount of blocks per tick as defined in passed info.
	 * If there are more blocks left to revert it will continue in the next tick.
	 *
	 * @param info the object containing the info
	 */
	private void revert(final GaiaRunnableInfo info, final GaiaData data) {
		if (!reverting) return;
		PaperLib.getChunkAtAsync(info.world, chunkX, chunkZ).thenAccept(result -> {
			GaiaVector relative, real;
			int counter = 0;
			while (++counter <= info.maxTransactions && info.it.hasNext()) {
				relative = info.it.next();
				real = chunk.getMinimumPoint().add(relative);
				info.world.getBlockAt(real.getX(), real.getY(), real.getZ()).setBlockData(data.getDataAt(relative));
			}
			if (info.it.hasNext()) {
				Bukkit.getScheduler().runTaskLater(Gaia.getPlugin(), () -> revert(info, data), 1);
			} else {
				cancelReverting();
			}
		});
	}

	public static void analyzeChunk(final GaiaChunk chunk, final World world) {
		if (chunk.isAnalyzed()) return;
		Bukkit.getScheduler().runTaskAsynchronously(Gaia.getPlugin(), () -> {
			final Iterator<GaiaVector> it = chunk.iterator();
			final GaiaData gd = new GaiaData(chunk.getRegion().getVector());
			chunk.analyze(new GaiaRunnableInfo(it, world, 4096), gd);
		});
	}

	public static void revertChunk(final GaiaChunk chunk, final World world) {
		if (chunk.isReverting()) return;
		chunk.reverting = true;
		Bukkit.getScheduler().runTaskAsynchronously(Gaia.getPlugin(), () -> {
			final Iterator<GaiaVector> it = chunk.iterator();
			final GaiaData gd = GaiaIO.getInstance().loadData(chunk);
			if (gd != null) chunk.revert(new GaiaRunnableInfo(it, world, 4096), gd);
		});
	}

	public boolean isReverting() {
		return reverting;
	}

	public void cancelReverting() {
		reverting = false;
	}

	public boolean isAnalyzed() {
		return meta != null && meta.hash != null && !meta.hash.isEmpty();
	}

	public Iterator<GaiaVector> iterator() {
		return new Iterator<GaiaVector>() {
			private final GaiaVector max = chunk.getVector();
			private int nextX = 0;
			private int nextY = 0;
			private int nextZ = 0;

			@Override
			public boolean hasNext() {
				return (nextX != Integer.MIN_VALUE);
			}

			@Override
			public GaiaVector next() {
				if (!hasNext()) throw new NoSuchElementException();
				GaiaVector answer = GaiaVector.at(nextX, nextY, nextZ);
				if (++nextX >= max.getX()) {
					nextX = 0;
					if (++nextZ >= max.getZ()) {
						nextZ = 0;
						if (++nextY >= max.getY()) {
							nextX = Integer.MIN_VALUE;
						}
					}
				}
				return answer;
			}
		};
	}

	@Override
	public GaiaMetadata getMetadata() {
		return meta;
	}

	@Override
	public void setMetadata(GaiaMetadata meta) {
		this.meta = (ChunkMetadata) meta;
	}
}