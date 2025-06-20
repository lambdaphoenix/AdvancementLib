package io.github._6mal7.advancementLib;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * <b>DummyMain</b> is a placeholder main class required only for Modrinth uploads.
 * <p>
 * <b>This class is NOT part of the AdvancementLib API and should never be used directly.</b>
 * <br>
 * If you see this message in your server logs, you have mistakenly installed the library JAR as a plugin.
 * Please remove it and only use AdvancementLib as a dependency in your own plugin project.
 *
 * @author 6mal7
 * @see <a href="https://github.com/6mal7/AdvancementLib">AdvancementLib on GitHub</a>
 */
public class DummyMain extends JavaPlugin {

  /**
   * Warns the server owner if the library JAR is installed as a plugin.
   */
  @Override
  public void onEnable() {
    this.getLogger().severe("AdvancementLib is a library, not a plugin! Do NOT install this JAR directly on your server. It is intended to be used as a dependency in other plugins only.");
  }
}
