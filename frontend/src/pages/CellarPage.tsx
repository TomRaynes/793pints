import { useEffect, useState } from "react";
import { getAllCasks, createCask } from "../api/cask";
import { getCellarConfig, updateCellarConfig } from "../api/cellar";
import type { UpdateCellarConfigRequest } from "../api/cellar";
import type { Cask, CaskState, EntityLabel } from "../types/models";
import StatusGroup from "../components/StatusGroup";
import { useLocation, useNavigate } from "react-router-dom";
import { useHandleUnauthorised } from "../Utils.tsx";
import PageLayout from "../components/PageLayout";

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

export const normalizeState = (state: string | undefined) => {
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
    organisationName: string;
    cellar: EntityLabel;
};

export default function CellarPage() {
    const [casks, setCasks] = useState<Cask[]>([]);
    const [isNewCaskOpen, setIsNewCaskOpen] = useState(false);
    const [newCaskName, setNewCaskName] = useState("");
    const [isCreating, setIsCreating] = useState(false);
    const [isSettingsOpen, setIsSettingsOpen] = useState(false);
    const [settingsLoading, setSettingsLoading] = useState(false);
    const [isSavingSettings, setIsSavingSettings] = useState(false);
    const [settingsSaved, setSettingsSaved] = useState(false);
    const [editRackDefault, setEditRackDefault] = useState("");
    const [editVentDefault, setEditVentDefault] = useState("");
    const [editTapDefault, setEditTapDefault] = useState("");
    const [editPullDefault, setEditPullDefault] = useState("");
    const [applyRackAll, setApplyRackAll] = useState(false);
    const [applyVentAll, setApplyVentAll] = useState(false);
    const [applyTapAll, setApplyTapAll] = useState(false);
    const [applyPullAll, setApplyPullAll] = useState(false);
    const location = useLocation();
    const navigate = useNavigate();
    const state = location.state as CellarLocationState;

    const organisationId = state?.organisationId ?? null;
    const organisationName = state?.organisationName ?? null;
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

    const updateCask = (updated: Cask) => {
        setCasks((prev) => prev.map((cask) => (cask.caskId === updated.caskId ? updated : cask)));
    };

    const removeCaskFromList = (caskId: string) => {
        setCasks((prev) => prev.filter((cask) => cask.caskId !== caskId));
    };

    const openSettings = async () => {
        if (!cellarId) return;
        setIsSettingsOpen(true);
        setSettingsLoading(true);
        setSettingsSaved(false);
        setApplyRackAll(false);
        setApplyVentAll(false);
        setApplyTapAll(false);
        setApplyPullAll(false);
        try {
            const config = await getCellarConfig(cellarId);
            setEditRackDefault(String(config.rackCooldownDefault ?? 0));
            setEditVentDefault(String(config.ventCooldownDefault ?? 0));
            setEditTapDefault(String(config.tapCooldownDefault ?? 0));
            setEditPullDefault(String(config.pullingPeriodDefault ?? 0));
        } catch (err: any) {
            handleUnauthorised(err);
        } finally {
            setSettingsLoading(false);
        }
    };

    const closeSettings = () => {
        if (isSavingSettings) return;
        setIsSettingsOpen(false);
    };

    const saveSettings = async () => {
        if (!cellarId || isSavingSettings) return;
        try {
            setIsSavingSettings(true);
            setSettingsSaved(false);
            const request: UpdateCellarConfigRequest = {
                rackCooldownDefault: { value: Number(editRackDefault) || 0, applyToAll: applyRackAll },
                ventCooldownDefault: { value: Number(editVentDefault) || 0, applyToAll: applyVentAll },
                tapCooldownDefault: { value: Number(editTapDefault) || 0, applyToAll: applyTapAll },
                pullingPeriodDefault: { value: Number(editPullDefault) || 0, applyToAll: applyPullAll },
            };
            await updateCellarConfig(cellarId, request);
            setSettingsSaved(true);
            setTimeout(() => setSettingsSaved(false), 2000);
        } catch (err: any) {
            handleUnauthorised(err);
        } finally {
            setIsSavingSettings(false);
        }
    };

    const totalCasks = casks.length;

    return (
        <PageLayout backTo="/organisation" backLabel={organisationName ?? "Back"} backState={{ id: organisationId, name: organisationName }}>
            <div className="breadcrumb">
                <button className="breadcrumb-link" onClick={() => navigate("/organisations")}>Organisations</button>
                <span className="breadcrumb-sep">/</span>
                <button className="breadcrumb-link" onClick={() => navigate("/organisation", { state: { id: organisationId, name: organisationName } })}>{organisationName}</button>
                <span className="breadcrumb-sep">/</span>
                <span className="breadcrumb-current">{cellarName}</span>
            </div>

            <h1 className="page-title page-title-clickable" onClick={openSettings} title="Cellar settings">{cellarName}</h1>
            <p className="page-subtitle">{totalCasks} cask{totalCasks !== 1 ? "s" : ""} in this cellar</p>

            {casks.length === 0 ? (
                <div className="empty-state">
                    <div className="empty-state-icon">🛢️</div>
                    <p className="empty-state-text">No casks in this cellar yet. Add one to get started.</p>
                </div>
            ) : (
                statuses.map((status) => (
                    <StatusGroup
                        key={status}
                        status={status}
                        casks={casks.filter((c) => c.state === status)}
                        organisationId={organisationId}
                        cellarId={cellarId}
                        onUpdateCask={updateCask}
                        onRemoveCask={removeCaskFromList}
                        onError={handleUnauthorised}
                    />
                ))
            )}

            <div className="action-bar">
                <button className="btn btn-primary" onClick={newCask}>
                    + New Cask
                </button>
            </div>

            {isNewCaskOpen && (
                <div className="modal-overlay" onClick={closeNewCask}>
                    <div className="modal" onClick={(e) => e.stopPropagation()}>
                        <h3 className="modal-title">New Cask</h3>
                        <label className="modal-field">
                            <span>Cask Name</span>
                            <input
                                type="text"
                                value={newCaskName}
                                onChange={(e) => setNewCaskName(e.target.value)}
                                placeholder="Enter cask name"
                                autoFocus
                            />
                        </label>
                        <div className="modal-actions">
                            <button className="btn btn-secondary" onClick={closeNewCask} disabled={isCreating}>
                                Cancel
                            </button>
                            <button className="btn btn-primary" onClick={submitNewCask} disabled={isCreating || !newCaskName.trim()}>
                                {isCreating ? "Creating…" : "Create"}
                            </button>
                        </div>
                    </div>
                </div>
            )}
            {isSettingsOpen && (
                <div className="modal-overlay" onClick={closeSettings}>
                    <div className="modal" onClick={(e) => e.stopPropagation()}>
                        <h3 className="modal-title">Cellar Settings</h3>
                        {settingsLoading ? (
                            <p className="text-muted">Loading…</p>
                        ) : (
                            <>
                                <div className="config-field">
                                    <label className="modal-field">
                                        <span>Racking Cooldown Default (hours)</span>
                                        <input type="number" min="0" step="1" value={editRackDefault} onChange={(e) => setEditRackDefault(e.target.value)} disabled={isSavingSettings} />
                                    </label>
                                    <label className="config-checkbox">
                                        <input type="checkbox" checked={applyRackAll} onChange={(e) => setApplyRackAll(e.target.checked)} disabled={isSavingSettings} />
                                        <span>Apply to all cellars in {organisationName}</span>
                                    </label>
                                </div>

                                <div className="config-field">
                                    <label className="modal-field">
                                        <span>Venting Cooldown Default (hours)</span>
                                        <input type="number" min="0" step="1" value={editVentDefault} onChange={(e) => setEditVentDefault(e.target.value)} disabled={isSavingSettings} />
                                    </label>
                                    <label className="config-checkbox">
                                        <input type="checkbox" checked={applyVentAll} onChange={(e) => setApplyVentAll(e.target.checked)} disabled={isSavingSettings} />
                                        <span>Apply to all cellars in {organisationName}</span>
                                    </label>
                                </div>

                                <div className="config-field">
                                    <label className="modal-field">
                                        <span>Tapping Cooldown Default (hours)</span>
                                        <input type="number" min="0" step="1" value={editTapDefault} onChange={(e) => setEditTapDefault(e.target.value)} disabled={isSavingSettings} />
                                    </label>
                                    <label className="config-checkbox">
                                        <input type="checkbox" checked={applyTapAll} onChange={(e) => setApplyTapAll(e.target.checked)} disabled={isSavingSettings} />
                                        <span>Apply to all cellars in {organisationName}</span>
                                    </label>
                                </div>

                                <div className="config-field">
                                    <label className="modal-field">
                                        <span>Pulling Period Default (hours)</span>
                                        <input type="number" min="0" step="1" value={editPullDefault} onChange={(e) => setEditPullDefault(e.target.value)} disabled={isSavingSettings} />
                                    </label>
                                    <label className="config-checkbox">
                                        <input type="checkbox" checked={applyPullAll} onChange={(e) => setApplyPullAll(e.target.checked)} disabled={isSavingSettings} />
                                        <span>Apply to all cellars in {organisationName}</span>
                                    </label>
                                </div>

                                <div className="modal-actions">
                                    {settingsSaved && (
                                        <span className="profile-save-success">✓ Saved</span>
                                    )}
                                    <button className="btn btn-secondary" onClick={closeSettings} disabled={isSavingSettings}>
                                        Cancel
                                    </button>
                                    <button className="btn btn-primary" onClick={saveSettings} disabled={isSavingSettings}>
                                        {isSavingSettings ? "Saving…" : "Save"}
                                    </button>
                                </div>
                            </>
                        )}
                    </div>
                </div>
            )}
        </PageLayout>
    );
}
