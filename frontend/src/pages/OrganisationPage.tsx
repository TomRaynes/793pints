import { useLocation, useNavigate } from "react-router-dom";
import type { EntityLabel } from "../types/models.ts";
import { useEffect, useState } from "react";
import { getAllCellars, newCellar as createCellar } from "../api/cellar.ts";
import { useHandleUnauthorised } from "../Utils.tsx";
import { getUserAccessLevel, inviteToOrganisation } from "../api/organisation.ts";
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
    const [isNewCellarOpen, setIsNewCellarOpen] = useState(false);
    const [newCellarName, setNewCellarName] = useState("");
    const [isCreatingCellar, setIsCreatingCellar] = useState(false);
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
        navigate("/cellar", { state: { organisationId: organisationId, cellar: cellar } });
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
        setInviteIdentifier("");
        setIsInviteOpen(true);
    };

    const closeInviteModal = () => {
        if (isInviting) return;
        setIsInviteOpen(false);
    };

    const submitInvite = async () => {
        const identifier = inviteIdentifier.trim();
        if (!identifier || !organisationId) return;

        try {
            setIsInviting(true);
            await inviteToOrganisation(organisationId, identifier);
            alert("Invitation sent successfully");
            setIsInviteOpen(false);
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

            <h1 className="page-title">{organisationName}</h1>
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
                {accessLevel === "Owner" && (
                    <button className="btn btn-secondary" onClick={openInviteModal}>
                        + Add Member
                    </button>
                )}
            </div>

            {isInviteOpen && (
                <div className="modal-overlay" onClick={closeInviteModal}>
                    <div className="modal" onClick={(e) => e.stopPropagation()}>
                        <h3 className="modal-title">Invite Member</h3>
                        <label className="modal-field">
                            <span>Username or Email</span>
                            <input
                                type="text"
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
        </PageLayout>
    );

}