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

import me.moros.gaia.api.GaiaVector;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

public class PlayerWrapper implements GaiaPlayer {
  private final Player player;

  public PlayerWrapper(@NonNull Player player) {
    this.player = player;
  }

  public @NonNull Player get() {
    return this.player;
  }

  @Override
  public boolean isPlayer() {
    return true;
  }

  @Override
  public boolean isOnline() {
    return player.isOnline();
  }

  @Override
  public @NonNull GaiaVector getLocation() {
    return GaiaVector.at(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
  }

  @Override
  public @NonNull WorldWrapper getWorld() {
    return new WorldWrapper(player.getWorld());
  }

  @Override
  public @NonNull String getName() {
    return player.getName();
  }

  @Override
  public boolean hasPermission(@NonNull String permission) {
    return player.hasPermission(permission);
  }

  @Override
  public void sendMessage(@NonNull Component text) {
    player.sendMessage(text);
  }
}
