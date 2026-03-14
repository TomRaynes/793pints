import { useEffect, useState } from "react";
import { getAllCasks, createCask } from "../api/cask";
import type {Cask, CaskState, EntityLabel} from "../types/models";
import StatusGroup from "../components/StatusGroup";
import {useLocation} from "react-router-dom";
import {useHandleUnauthorised} from "../Utils.tsx";

const statuses = [
    "Delivered",
    "Racked",
    "Settled",
    "Vented",
    "Needs Tap",
    "Tapped",
    "Ready to Serve",
    "Pulling",
    "Tired",
];

const stateLabelMap: Record<string, string> = {
    delivered: "Delivered",
    racked: "Racked",
    settled: "Settled",
    vented: "Vented",
    needs_tap: "Needs Tap",
    tapped: "Tapped",
    ready_to_serve: "Ready to Serve",
    pulling: "Pulling",
    tired: "Tired",
};

const normalizeState = (state: string | undefined) => {
    if (!state) return "";
    const key = state.trim().toLowerCase();
    return stateLabelMap[key] ?? state;
};

const normalizeCask = (raw: Record<string, unknown>): Cask => {
    return {
        caskId: String(raw.caskId ?? raw.id ?? ""),
        caskName: String(raw.caskName ?? raw.name ?? ""),
        state: normalizeState(String(raw.state ?? "")) as Cask["state"],
        stateChangeTimestamp: new Date(String(raw.stateChangeTimestamp ?? null)),
        rackCooldownHours: Number(raw.rackCooldownHours) ?? null,
        ventCooldownHours: Number(raw.ventCooldownHours) ?? null,
        tapCooldownHours: Number(raw.tapCooldownHours) ?? null,
        pullingPeriodHours: Number(raw.pullingPeriodHours) ?? null
    };
};

export const getCooldown = (cask: Cask): number | null => {
    switch (cask.state) {
        case "Racked": return cask.rackCooldownHours == null ? null : Number(cask.rackCooldownHours);
        case "Vented": return cask.ventCooldownHours == null ? null : Number(cask.ventCooldownHours);
        case "Tapped": return cask.tapCooldownHours == null ? null : Number(cask.tapCooldownHours);
        case "Pulling": return cask.pullingPeriodHours == null ? null : Number(cask.pullingPeriodHours);
    }
    return null;
}

const getNextState = (cask: Cask): CaskState => {
    switch (cask.state) {
        case "Delivered": return "Racked";
        case "Racked": return "Settled";
        case "Settled": return "Vented";
        case "Vented": return "Needs Tap";
        case "Needs Tap": return "Tapped";
        case "Tapped": return "Ready to Serve";
        case "Ready to Serve": return "Pulling";
        case "Pulling": return "Tired";
        case "Tired": return "Tired";
    }
}

export function refreshCaskState(cask: Cask): Cask {
    return {
        ...cask,
        state: getNextState(cask),
        stateChangeTimestamp: new Date()
    };
}

type CellarLocationState = {
    organisationId: string;
    cellar: EntityLabel;
};

export default function CellarPage() {
    const [casks, setCasks] = useState<Cask[]>([]);
    const [isNewCaskOpen, setIsNewCaskOpen] = useState(false);
    const [newCaskName, setNewCaskName] = useState("");
    const [isCreating, setIsCreating] = useState(false);
    const location = useLocation();
    const state = location.state as CellarLocationState;

    const organisationId = state?.organisationId ?? null;
    const cellarId = state?.cellar.id ?? null;
    const cellarName = state?.cellar.name ?? null;

    const handleUnauthorised = useHandleUnauthorised();

    const load = async () => {
        try {
            const data = await getAllCasks(organisationId, cellarId);
            const rawList = Array.isArray(data) ? data : data?.casks ?? [];
            setCasks(rawList.map((c: Record<string, unknown>) => normalizeCask(c)));
        } catch (err: any) {
            handleUnauthorised(err);
        }
    };

    useEffect(() => {
        load();
    }, []);

    useEffect(() => {
        const id = setInterval(() => {
            setCasks((prev) => prev.map((cask) => {
                const cooldownHours = getCooldown(cask);
                if (cooldownHours == null) return cask;
                const lastChangeMs = cask.stateChangeTimestamp.getTime();
                if (!Number.isFinite(lastChangeMs)) return cask;
                const remainingMs = (cooldownHours * 60 * 60 * 1000) - (Date.now() - lastChangeMs);
                if (remainingMs > 0) return cask;
                return refreshCaskState(cask);
            }));
        }, 1000);
        return () => clearInterval(id);
    }, []);

    const newCask = () => {
        setNewCaskName("");
        setIsNewCaskOpen(true);
    };

    const closeNewCask = () => {
        if (isCreating) return;
        setIsNewCaskOpen(false);
    };

    const submitNewCask = async () => {
        if (!organisationId || !cellarId || isCreating) return;
        const trimmedName = newCaskName.trim();
        if (!trimmedName) return;
        try {
            setIsCreating(true);
            await createCask(organisationId, cellarId, trimmedName, "Delivered");
            await load();
            setIsNewCaskOpen(false);
        } catch (err: any) {
            handleUnauthorised(err);
        } finally {
            setIsCreating(false);
        }
    };

    return (
        <div className="container">
            <div style={{ display: 'flex', justifyContent: 'center' }}>
                <h2>{cellarName}</h2>
            </div>

            {casks.length === 0 ? (
                <div>No casks found for this cellar.</div>
            ) : (
                statuses.map((status) => (
                    <StatusGroup
                        key={status}
                        status={status}
                        casks={casks.filter((c) => c.state === status)}
                    />
                ))
            )}

            <br/>
            <br/>

            <div style={{ display: 'flex', justifyContent: 'center' }}>
                <button key="new org" type="button" onClick={() => newCask()}>
                    New Cask
                </button>
            </div>

            {isNewCaskOpen ? (
                <div className="modal-overlay" onClick={closeNewCask}>
                    <div className="modal" onClick={(e) => e.stopPropagation()}>
                        <h3>Cask Name</h3>
                        <input
                            type="text"
                            value={newCaskName}
                            onChange={(e) => setNewCaskName(e.target.value)}
                            placeholder="Enter cask name"
                            autoFocus
                        />
                        <div className="modal-actions">
                            <button type="button" onClick={closeNewCask} disabled={isCreating}>
                                Cancel
                            </button>
                            <button type="button" onClick={submitNewCask} disabled={isCreating || !newCaskName.trim()}>
                                {isCreating ? "Creating..." : "Create"}
                            </button>
                        </div>
                    </div>
                </div>
            ) : null}
        </div>
    );
}
