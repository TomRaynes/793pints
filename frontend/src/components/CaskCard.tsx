import { useEffect, useMemo, useState } from "react";
import type {Cask} from "../types/models";
import {getCooldown} from "../pages/CellarPage.tsx";

const formatRemaining = (remainingMs: number): string => {
    const totalSeconds = Math.max(0, Math.floor(remainingMs / 1000));
    const hours = Math.floor(totalSeconds / 3600);
    const minutes = Math.floor((totalSeconds % 3600) / 60);
    const seconds = totalSeconds % 60;
    return `${String(hours).padStart(2, "0")}:${String(minutes).padStart(2, "0")}:${String(seconds).padStart(2, "0")}`;
};

export default function CaskCard({ cask }: { cask: Cask }) {
    const [nowMs, setNowMs] = useState(() => Date.now());
    const cooldownHours = getCooldown(cask);
    const lastChangeMs = cask.stateChangeTimestamp.getTime();
    const hasValidTimestamp = Number.isFinite(lastChangeMs);

    useEffect(() => {
        if (cooldownHours == null || !hasValidTimestamp) return;
        const id = setInterval(() => setNowMs(Date.now()), 1000);
        return () => clearInterval(id);
    }, [cooldownHours, hasValidTimestamp, lastChangeMs]);

    const remainingText = useMemo(() => {
        if (cooldownHours == null || !hasValidTimestamp) {
            return null;
        }
        const cooldownMs = cooldownHours * 60 * 60 * 1000;
        const remainingMs = cooldownMs - (nowMs - lastChangeMs);
        return formatRemaining(remainingMs);
    }, [cooldownHours, hasValidTimestamp, lastChangeMs, nowMs]);

    return (
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
            <div className="cask-card">
                <strong>{cask.caskName}</strong>
                <div>Status: {cask.state}</div>
            </div>
            <div>
                {remainingText ? `${remainingText} remaining` : null}
            </div>
        </div>
    );
}
