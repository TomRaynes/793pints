import { useLocation, useNavigate } from "react-router-dom";
import type { EntityLabel } from "../types/models.ts";
import { useEffect, useState } from "react";
import { getAllCellars, newCellar as createCellar } from "../api/cellar.ts";
import { useHandleUnauthorised } from "../Utils.tsx";
import { getUserAccessLevel, inviteToOrganisation, getMembers } from "../api/organisation.ts";
import PageLayout from "../components/PageLayout";

export default function OrganisationPage() {
    const location = useLocation();
    const state = location.state as EntityLabel;
    const organisationName = state?.name;
    const organisationId = state?.id;
    const handleUnauthorised = useHandleUnauthorised();

    const [cellars, setCellars] = useState<EntityLabel[]>([]);
    const [accessLevel, setAccessLevel] = useState<string | null>(null);
    const [isInviteOpen, setIsInviteOpen] = useState(false);
    const [inviteIdentifier, setInviteIdentifier] = useState("");
    const [isInviting, setIsInviting] = useState(false);
    const [inviteSuccess, setInviteSuccess] = useState<string | null>(null);
    const [isNewCellarOpen, setIsNewCellarOpen] = useState(false);
    const [newCellarName, setNewCellarName] = useState("");
    const [isCreatingCellar, setIsCreatingCellar] = useState(false);
    const [isMembersOpen, setIsMembersOpen] = useState(false);
    const [membersData, setMembersData] = useState<{ admins: Record<string, string>; members: Record<string, string> } | null>(null);
    const [membersLoading, setMembersLoading] = useState(false);
    const navigate = useNavigate();

    const load = async () => {
        try {
            const data = await getAllCellars(organisationId);
            const rawList = Array.isArray(data) ? data : data?.cellars ?? [];
            setCellars([...rawList].sort((a, b) => a.name.localeCompare(b.name)));

            const accessData = await getUserAccessLevel(organisationId);
            setAccessLevel(accessData.accessLevel);
        } catch (err: any) {
            handleUnauthorised(err);
        }
    };

    useEffect(() => {
        load();
    }, []);

    const goToCellar = (cellar: EntityLabel) => {
        navigate("/cellar", { state: { organisationId: organisationId, organisationName: organisationName, cellar: cellar } });
    };

    const newCellar = () => {
        setNewCellarName("");
        setIsNewCellarOpen(true);
    };

    const closeNewCellarModal = () => {
        if (isCreatingCellar) return;
        setIsNewCellarOpen(false);
    };

    const submitNewCellar = async () => {
        const name = newCellarName.trim();
        if (!name || !organisationId) return;

        try {
            setIsCreatingCellar(true);
            await createCellar(name, organisationId);
            setIsNewCellarOpen(false);
            await load();
        } catch (err: any) {
            console.error(err);
            handleUnauthorised(err);
        } finally {
            setIsCreatingCellar(false);
        }
    };

    const openInviteModal = () => {
        setIsMembersOpen(false);
        setInviteIdentifier("");
        setIsInviteOpen(true);
    };

    const openMembersModal = async () => {
        if (!organisationId) return;
        setIsMembersOpen(true);
        setMembersLoading(true);
        try {
            const data = await getMembers(organisationId);
            setMembersData(data);
        } catch (err: any) {
            handleUnauthorised(err);
        } finally {
            setMembersLoading(false);
        }
    };

    const closeInviteModal = () => {
        if (isInviting) return;
        setIsInviteOpen(false);
        setIsMembersOpen(true);
    };

    const submitInvite = async () => {
        const identifier = inviteIdentifier.trim();
        if (!identifier || !organisationId) return;

        try {
            setIsInviting(true);
            await inviteToOrganisation(organisationId, identifier);
            setIsInviteOpen(false);
            setIsMembersOpen(true);
            setInviteSuccess(`Invitation sent to ${identifier}`);
            setTimeout(() => setInviteSuccess(null), 3000);
        } catch (err: any) {
            console.error(err);
            handleUnauthorised(err);
        } finally {
            setIsInviting(false);
        }
    };

    return (
        <PageLayout backTo="/organisations" backLabel="Organisations">
            <div className="breadcrumb">
                <button className="breadcrumb-link" onClick={() => navigate("/organisations")}>Organisations</button>
                <span className="breadcrumb-sep">/</span>
                <span className="breadcrumb-current">{organisationName}</span>
            </div>

            <div className="page-title-row page-title-row-spread">
                <h1 className="page-title">{organisationName}</h1>
                <button className="btn btn-secondary btn-sm" onClick={openMembersModal}>
                    Members
                </button>
            </div>
            <p className="page-subtitle">Manage cellars within this organisation.</p>

            {cellars.length === 0 ? (
                <div className="empty-state">
                    <div className="empty-state-icon">🏗️</div>
                    <p className="empty-state-text">No cellars yet. Create one to start tracking casks.</p>
                </div>
            ) : (
                <div className="list-stack">
                    {cellars.map((cellar) => (
                        <button key={cellar.id} className="list-item" onClick={() => goToCellar(cellar)}>
                            <div className="list-item-content">
                                <div className="list-item-title">{cellar.name}</div>
                            </div>
                            <span className="list-item-chevron">›</span>
                        </button>
                    ))}
                </div>
            )}

            <div className="action-bar">
                <button className="btn btn-primary" onClick={newCellar}>
                    + New Cellar
                </button>
            </div>

            {isInviteOpen && (
                <div className="modal-overlay" onClick={closeInviteModal}>
                    <div className="modal" onClick={(e) => e.stopPropagation()}>
                        <h3 className="modal-title">Invite Member</h3>
                        <label className="modal-field">
                            <span>Username or Email</span>
                            <input
                                type="text"
                                id="invite-identifier"
                                value={inviteIdentifier}
                                onChange={(e) => setInviteIdentifier(e.target.value)}
                                placeholder="Enter username or email"
                                autoFocus
                            />
                        </label>
                        <div className="modal-actions">
                            <button className="btn btn-secondary" onClick={closeInviteModal} disabled={isInviting}>
                                Cancel
                            </button>
                            <button className="btn btn-primary" onClick={submitInvite} disabled={isInviting || !inviteIdentifier.trim()}>
                                {isInviting ? "Inviting…" : "Send Invite"}
                            </button>
                        </div>
                    </div>
                </div>
            )}

            {isNewCellarOpen && (
                <div className="modal-overlay" onClick={closeNewCellarModal}>
                    <div className="modal" onClick={(e) => e.stopPropagation()}>
                        <h3 className="modal-title">New Cellar</h3>
                        <label className="modal-field">
                            <span>Cellar Name</span>
                            <input
                                type="text"
                                value={newCellarName}
                                onChange={(e) => setNewCellarName(e.target.value)}
                                placeholder="Enter cellar name"
                                autoFocus
                            />
                        </label>
                        <div className="modal-actions">
                            <button className="btn btn-secondary" onClick={closeNewCellarModal} disabled={isCreatingCellar}>
                                Cancel
                            </button>
                            <button className="btn btn-primary" onClick={submitNewCellar} disabled={isCreatingCellar || !newCellarName.trim()}>
                                {isCreatingCellar ? "Creating…" : "Create"}
                            </button>
                        </div>
                    </div>
                </div>
            )}
            {isMembersOpen && (
                <div className="modal-overlay" onClick={() => setIsMembersOpen(false)}>
                    <div className="modal" onClick={(e) => e.stopPropagation()}>
                        <div className="modal-title-row">
                            <h3 className="modal-title">Members</h3>
                            {accessLevel === "Owner" && (
                                <button className="btn btn-primary btn-sm" onClick={openInviteModal}>
                                    Invite Member
                                </button>
                            )}
                        </div>
                        {inviteSuccess && (
                            <div className="toast toast-success">
                                <span>✓ {inviteSuccess}</span>
                                <button className="toast-dismiss" onClick={() => setInviteSuccess(null)}>✕</button>
                            </div>
                        )}
                        {membersLoading ? (
                            <p className="text-muted">Loading…</p>
                        ) : membersData ? (
                            <div className="members-list">
                                {Object.entries(membersData.admins).map(([id, username]) => (
                                    <div key={id} className="member-row">
                                        <span className="member-name">{username}</span>
                                        <span className="member-badge member-badge-admin">Admin</span>
                                    </div>
                                ))}
                                {Object.entries(membersData.members).map(([id, username]) => (
                                    <div key={id} className="member-row">
                                        <span className="member-name">{username}</span>
                                    </div>
                                ))}
                            </div>
                        ) : (
                            <p className="text-muted">Could not load members.</p>
                        )}
                        <div className="modal-actions">
                            <button className="btn btn-secondary" onClick={() => setIsMembersOpen(false)}>
                                Close
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </PageLayout>
    );

}