import { useEffect, useMemo, useState } from "react";
import type { Cask, CaskState } from "../types/models";
import { updateCask as updateCaskApi, removeCask as removeCaskApi } from "../api/cask";
import { normalizeState } from "../pages/CellarPage.tsx";

const caskStates: CaskState[] = [
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

const formatRemaining = (remainingMs: number): string => {
    const totalSeconds = Math.max(0, Math.floor(remainingMs / 1000));
    const hours = Math.floor(totalSeconds / 3600);
    const minutes = Math.floor((totalSeconds % 3600) / 60);
    const seconds = totalSeconds % 60;
    return `${String(hours).padStart(2, "0")}:${String(minutes).padStart(2, "0")}:${String(seconds).padStart(2, "0")}`;
};

type Props = {
    cask: Cask;
    organisationId: string | null;
    cellarId: string | null;
    onUpdate: (updated: Cask) => void;
    onRemove: (caskId: string) => void;
    onError: (err: unknown) => void;
};

const buildUpdatedCask = (base: Cask, updates: Partial<Cask>): Cask => {
    const nextState = updates.state ?? base.state;
    const stateChanged = nextState !== base.state;
    return {
        ...base,
        ...updates,
        state: nextState,
        stateChangeTimestamp: stateChanged ? new Date() : base.stateChangeTimestamp
    };
};

const normalizeCaskResponse = (raw: Record<string, unknown>): Cask => {
    return {
        caskId: String(raw.caskId ?? raw.id ?? ""),
        caskName: String(raw.caskName ?? raw.name ?? ""),
        state: normalizeState(String(raw.state ?? "")) as CaskState,
        stateChangeTimestamp: new Date(String(raw.stateChangeTimestamp ?? null)),
        rackCooldownHours: raw.rackCooldownHours == null ? null : Number(raw.rackCooldownHours),
        ventCooldownHours: raw.ventCooldownHours == null ? null : Number(raw.ventCooldownHours),
        tapCooldownHours: raw.tapCooldownHours == null ? null : Number(raw.tapCooldownHours),
        pullingPeriodHours: raw.pullingPeriodHours == null ? null : Number(raw.pullingPeriodHours)
    };
};

const toApiString = (value: string): string => value.trim();
const toApiNumber = (value: string): string => {
    const trimmed = value.trim();
    if (!trimmed) return "";
    const parsed = Number(trimmed);
    return Number.isFinite(parsed) ? String(parsed) : "";
};

const getCooldown = (cask: Cask): number | null => {
    switch (cask.state) {
        case "Racked": return cask.rackCooldownHours == null ? null : Number(cask.rackCooldownHours);
        case "Vented": return cask.ventCooldownHours == null ? null : Number(cask.ventCooldownHours);
        case "Tapped": return cask.tapCooldownHours == null ? null : Number(cask.tapCooldownHours);
        case "Pulling": return cask.pullingPeriodHours == null ? null : Number(cask.pullingPeriodHours);
    }
    return null;
};

export default function CaskCard({ cask, organisationId, cellarId, onUpdate, onRemove, onError }: Props) {
    const [nowMs, setNowMs] = useState(() => Date.now());
    const [isEditOpen, setIsEditOpen] = useState(false);
    const [isSaving, setIsSaving] = useState(false);
    const [isDeleteOpen, setIsDeleteOpen] = useState(false);
    const [isDeleting, setIsDeleting] = useState(false);
    const [editName, setEditName] = useState(cask.caskName ?? "");
    const [editState, setEditState] = useState<CaskState>(cask.state);
    const [editRack, setEditRack] = useState("");
    const [editVent, setEditVent] = useState("");
    const [editTap, setEditTap] = useState("");
    const [editPull, setEditPull] = useState("");

    useEffect(() => {
        if (!isEditOpen) return;
        setEditName(cask.caskName ?? "");
        setEditState(cask.state);
        setEditRack(cask.rackCooldownHours == null ? "" : String(cask.rackCooldownHours));
        setEditVent(cask.ventCooldownHours == null ? "" : String(cask.ventCooldownHours));
        setEditTap(cask.tapCooldownHours == null ? "" : String(cask.tapCooldownHours));
        setEditPull(cask.pullingPeriodHours == null ? "" : String(cask.pullingPeriodHours));
    }, [cask, isEditOpen]);

    const openEdit = () => setIsEditOpen(true);
    const closeEdit = () => setIsEditOpen(false);
    const openDelete = () => setIsDeleteOpen(true);
    const closeDelete = () => {
        if (isDeleting) return;
        setIsDeleteOpen(false);
    };

    const toNullableNumber = (value: string): number | null => {
        const trimmed = value.trim();
        if (!trimmed) return null;
        const parsed = Number(trimmed);
        return Number.isFinite(parsed) ? parsed : null;
    };

    const saveChanges = async () => {
        if (!organisationId || !cellarId || isSaving) return;
        const trimmedName = editName.trim();
        if (!trimmedName) return;
        try {
            setIsSaving(true);
            const res = await updateCaskApi(
                organisationId,
                cellarId,
                cask.caskId,
                toApiString(trimmedName),
                editState,
                toApiNumber(editRack),
                toApiNumber(editVent),
                toApiNumber(editTap),
                toApiNumber(editPull)
            );
            const updatedFromApi = res?.caskId || res?.id ? normalizeCaskResponse(res) : null;
            const fallback = buildUpdatedCask(cask, {
                caskName: trimmedName,
                state: editState,
                rackCooldownHours: toNullableNumber(editRack),
                ventCooldownHours: toNullableNumber(editVent),
                tapCooldownHours: toNullableNumber(editTap),
                pullingPeriodHours: toNullableNumber(editPull),
            });
            onUpdate(updatedFromApi ?? fallback);
            setIsEditOpen(false);
        } catch (err) {
            onError(err);
        } finally {
            setIsSaving(false);
        }
    };

    const confirmDelete = async () => {
        if (!organisationId || !cellarId || isDeleting) return;
        try {
            setIsDeleting(true);
            await removeCaskApi(organisationId, cellarId, cask.caskId);
            onRemove(cask.caskId);
            setIsDeleteOpen(false);
        } catch (err) {
            onError(err);
        } finally {
            setIsDeleting(false);
        }
    };

    const cooldownHours = getCooldown(cask);
    const lastChangeMs = cask.stateChangeTimestamp instanceof Date
        ? cask.stateChangeTimestamp.getTime()
        : Date.parse(String(cask.stateChangeTimestamp));
    const hasValidTimestamp = Number.isFinite(lastChangeMs);

    useEffect(() => {
        if (cooldownHours == null || !hasValidTimestamp) return;
        const id = setInterval(() => setNowMs(Date.now()), 1000);
        return () => clearInterval(id);
    }, [cooldownHours, hasValidTimestamp, lastChangeMs]);

    const remainingText = useMemo(() => {
        if (cooldownHours == null || !hasValidTimestamp) return null;
        const cooldownMs = cooldownHours * 60 * 60 * 1000;
        const remainingMs = cooldownMs - (nowMs - lastChangeMs);
        return formatRemaining(remainingMs);
    }, [cooldownHours, hasValidTimestamp, lastChangeMs, nowMs]);

    return (
        <>
            <div className="cask-row">
                <button type="button" className="cask-card" onClick={openEdit}>
                    <div className="cask-card-info">
                        <div className="cask-card-name">{cask.caskName}</div>
                        <div className="cask-card-state">{cask.state}</div>
                    </div>
                    {remainingText && (
                        <div className="cask-card-timer">
                            <span className="timer-icon">⏱</span>
                            {remainingText}
                        </div>
                    )}
                </button>
                <button type="button" className="cask-delete-btn" onClick={openDelete} title="Delete cask">
                    ✕
                </button>
            </div>

            {isDeleteOpen && (
                <div className="modal-overlay" onClick={closeDelete}>
                    <div className="modal" onClick={(e) => e.stopPropagation()}>
                        <h3 className="modal-title">Delete Cask</h3>
                        <p className="modal-danger-text">
                            Are you sure you want to delete "{cask.caskName}"? This action cannot be undone.
                        </p>
                        <div className="modal-actions">
                            <button className="btn btn-secondary" onClick={closeDelete} disabled={isDeleting}>
                                Cancel
                            </button>
                            <button className="btn btn-danger-fill" onClick={confirmDelete} disabled={isDeleting}>
                                {isDeleting ? "Deleting…" : "Delete"}
                            </button>
                        </div>
                    </div>
                </div>
            )}

            {isEditOpen && (
                <div className="modal-overlay" onClick={closeEdit}>
                    <div className="modal" onClick={(e) => e.stopPropagation()}>
                        <h3 className="modal-title">Edit Cask</h3>
                        <label className="modal-field">
                            <span>Cask Name</span>
                            <input
                                type="text"
                                value={editName}
                                onChange={(e) => setEditName(e.target.value)}
                                placeholder="Enter cask name"
                                autoFocus
                                disabled={isSaving}
                            />
                        </label>
                        <label className="modal-field">
                            <span>State</span>
                            <select value={editState} onChange={(e) => setEditState(e.target.value as CaskState)} disabled={isSaving}>
                                {caskStates.map((state) => (
                                    <option key={state} value={state}>{state}</option>
                                ))}
                            </select>
                        </label>
                        <label className="modal-field">
                            <span>Racking Cooldown (hours)</span>
                            <input
                                type="number"
                                min="0"
                                step="1"
                                value={editRack}
                                onChange={(e) => setEditRack(e.target.value)}
                                disabled={isSaving}
                            />
                        </label>
                        <label className="modal-field">
                            <span>Venting Cooldown (hours)</span>
                            <input
                                type="number"
                                min="0"
                                step="1"
                                value={editVent}
                                onChange={(e) => setEditVent(e.target.value)}
                                disabled={isSaving}
                            />
                        </label>
                        <label className="modal-field">
                            <span>Tapping Cooldown (hours)</span>
                            <input
                                type="number"
                                min="0"
                                step="1"
                                value={editTap}
                                onChange={(e) => setEditTap(e.target.value)}
                                disabled={isSaving}
                            />
                        </label>
                        <label className="modal-field">
                            <span>Pulling Period (hours)</span>
                            <input
                                type="number"
                                min="0"
                                step="1"
                                value={editPull}
                                onChange={(e) => setEditPull(e.target.value)}
                                disabled={isSaving}
                            />
                        </label>
                        <div className="modal-actions">
                            <button className="btn btn-secondary" onClick={closeEdit} disabled={isSaving}>
                                Cancel
                            </button>
                            <button className="btn btn-primary" onClick={saveChanges} disabled={isSaving || !editName.trim()}>
                                {isSaving ? "Saving…" : "Save Changes"}
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </>
    );
}
