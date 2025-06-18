package io.github._6mal7.advancementLib;

/**
 * Specifies how criteria for a custom advancement should be granted.
 *
 * <p>Used by {@code AdvancementAPI} to determine whether to grant all criteria at once when the
 * target value is reached, or to grant criteria one by one as the player progresses.
 *
 * @author 6mal7
 * @version 0.2.0
 * @since 0.2.0
 */
public enum GrantMode {
  /**
   * All remaining criteria for the advancement are granted at once when the progress reaches or
   * exceeds the target value.
   *
   * <p>Use this mode for advancements that should be completed in a single step after reaching the
   * required progress.
   */
  ALL_AT_ONCE,
  /**
   * Only the next uncompleted criterion is granted each time the progress reaches the target value.
   * Progress is then reset for the next step.
   *
   * <p>Use this mode for advancements that should be completed over multiple steps, with a progress
   * bar shown in the advancements tab.
   */
  STEP_BY_STEP
}
