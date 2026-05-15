package com.pints793.mobile.domain.logic

import com.pints793.mobile.domain.Cask
import com.pints793.mobile.domain.CaskState
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/** Pure functions porting CellarPage.tsx#getNextState / refreshCaskState / getCooldown. */
object CaskStateMachine {

    fun nextState(current: CaskState): CaskState = when (current) {
        CaskState.Delivered    -> CaskState.Racked
        CaskState.Racked       -> CaskState.Settled
        CaskState.Settled      -> CaskState.Vented
        CaskState.Vented       -> CaskState.NeedsTap
        CaskState.NeedsTap     -> CaskState.Tapped
        CaskState.Tapped       -> CaskState.ReadyToServe
        CaskState.ReadyToServe -> CaskState.Pulling
        CaskState.Pulling      -> CaskState.Tired
        CaskState.Tired        -> CaskState.Tired
    }

    fun cooldownHours(cask: Cask): Long? = when (cask.state) {
        CaskState.Racked  -> cask.rackCooldownHours
        CaskState.Vented  -> cask.ventCooldownHours
        CaskState.Tapped  -> cask.tapCooldownHours
        CaskState.Pulling -> cask.pullingPeriodHours
        else -> null
    }

    /** Advance the cask client-side and stamp the change moment. Mirrors web behaviour. */
    fun refresh(cask: Cask): Cask = cask.copy(
        state = nextState(cask.state),
        stateChangeTimestamp = Clock.System.now().toString()
    )

    /** Returns remaining millis until the cooldown elapses, or null if no cooldown applies. */
    fun remainingMillis(cask: Cask, now: Instant = Clock.System.now()): Long? {
        val hours = cooldownHours(cask) ?: return null
        val ts = runCatching { Instant.parse(cask.stateChangeTimestamp) }.getOrNull() ?: return null
        val elapsedMs = now.toEpochMilliseconds() - ts.toEpochMilliseconds()
        return hours * 60L * 60L * 1000L - elapsedMs
    }
}

