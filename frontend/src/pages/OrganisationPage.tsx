import { useLocation, useNavigate } from "react-router-dom";
import type { EntityLabel } from "../types/models.ts";
import type { UserProfile } from "../types/models.ts";
import { useEffect, useState } from "react";
import { getAllCellars, newCellar as createCellar } from "../api/cellar.ts";
import { useHandleUnauthorised } from "../Utils.tsx";
import { getUserAccessLevel, inviteToOrganisation, getMembers } from "../api/organisation.ts";
import { getProfileImages, getUserProfile } from "../api/user.ts";
import PageLayout from "../components/PageLayout";

export default function OrganisationPage() {
    const location = useLocation();
    const state = location.state as {
        id: string;
        name: string;
        cachedCellars?: EntityLabel[];
        cachedAccessLevel?: string;
        cachedMembers?: { admins: Record<string, string>; members: Record<string, string> };
        cachedMemberImages?: Record<string, string | null>;
    };
    const organisationName = state?.name;
    const organisationId = state?.id;
    const handleUnauthorised = useHandleUnauthorised();

    const [cellars, setCellars] = useState<EntityLabel[]>(state?.cachedCellars ?? []);
    const [accessLevel, setAccessLevel] = useState<string | null>(state?.cachedAccessLevel ?? null);
    const [isInviteOpen, setIsInviteOpen] = useState(false);
    const [inviteIdentifier, setInviteIdentifier] = useState("");
    const [isInviting, setIsInviting] = useState(false);
    const [inviteSuccess, setInviteSuccess] = useState<string | null>(null);
    const [isNewCellarOpen, setIsNewCellarOpen] = useState(false);
    const [newCellarName, setNewCellarName] = useState("");
    const [isCreatingCellar, setIsCreatingCellar] = useState(false);
    const [isMembersOpen, setIsMembersOpen] = useState(false);
    const [membersData, setMembersData] = useState<{ admins: Record<string, string>; members: Record<string, string> } | null>(state?.cachedMembers ?? null);
    const [memberImages, setMemberImages] = useState<Record<string, string | null>>(state?.cachedMemberImages ?? {});
    const [viewProfileUserId, setViewProfileUserId] = useState<string | null>(null);
    const [viewProfile, setViewProfile] = useState<UserProfile | null>(null);
    const [viewProfileLoading, setViewProfileLoading] = useState(false);
    const [viewProfileFullPicture, setViewProfileFullPicture] = useState(false);
    // Treat the cache as "real" only when an access level is present. CellarPage may
    // forward an empty cellars array / empty image map when the user landed on it via a
    // pinned-cellar shortcut from the dashboard, in which case we still need to load.
    const hasCachedData = Boolean(state?.cachedAccessLevel);
    const [pageLoading, setPageLoading] = useState(!hasCachedData);
    const navigate = useNavigate();

    const load = async () => {
        try {
            // Fetch cellars, access level, members, and images all together
            const [cellarsRes, accessRes, membersRes] = await Promise.all([
                getAllCellars(organisationId),
                getUserAccessLevel(organisationId),
                organisationId ? getMembers(organisationId) : Promise.resolve(null),
            ]);

            const rawList: EntityLabel[] = Array.isArray(cellarsRes) ? cellarsRes : cellarsRes?.cellars ?? [];
            const sorted = [...rawList].sort((a, b) => a.name.localeCompare(b.name));
            setCellars(sorted);
            setAccessLevel(accessRes.accessLevel);

            if (membersRes) {
                setMembersData(membersRes);

                const allUserIds = [
                    ...Object.keys(membersRes.admins ?? {}),
                    ...Object.keys(membersRes.members ?? {}),
                ];
                if (allUserIds.length > 0) {
                    const images = await getProfileImages(allUserIds);
                    setMemberImages(images);
                }
            }
        } catch (err: any) {
            handleUnauthorised(err);
        } finally {
            setPageLoading(false);
        }
    };

    useEffect(() => {
        if (!hasCachedData) {
            load();
        }
    }, []);

    const goToCellar = (cellar: EntityLabel) => {
        navigate("/cellar", {
            state: {
                organisationId,
                organisationName,
                cellar,
                cachedMembers: membersData,
                cachedMemberImages: memberImages,
                cachedCellars: cellars,
                cachedAccessLevel: accessLevel,
            }
        });
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

    const openMembersModal = () => {
        setIsMembersOpen(true);
    };

    const openMemberProfile = async (userId: string) => {
        setViewProfileUserId(userId);
        setViewProfile(null);
        setViewProfileLoading(true);
        setViewProfileFullPicture(false);
        try {
            const data = await getUserProfile(userId);
            setViewProfile(data);
        } catch (err: any) {
            handleUnauthorised(err);
        } finally {
            setViewProfileLoading(false);
        }
    };

    const closeMemberProfile = () => {
        setViewProfileUserId(null);
        setViewProfile(null);
        setViewProfileFullPicture(false);
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

    if (pageLoading) {
        return (
            <PageLayout backTo="/organisations" backLabel="Organisations">
                <div className="empty-state">
                    <p className="empty-state-text">Loading…</p>
                </div>
            </PageLayout>
        );
    }

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
                        {!membersData ? (
                            <p className="text-muted">Loading…</p>
                        ) : (
                            <div className="members-list">
                                {Object.entries(membersData.admins).map(([id, username]) => (
                                    <div key={id} className="member-row member-row-clickable" onClick={() => openMemberProfile(id)}>
                                        <div className="member-avatar">
                                            {memberImages[id] ? (
                                                <img src={memberImages[id]!} alt={username} className="member-avatar-img" />
                                            ) : (
                                                <span className="member-avatar-placeholder">{username.charAt(0).toUpperCase()}</span>
                                            )}
                                        </div>
                                        <span className="member-name">{username}</span>
                                        <span className="member-badge member-badge-admin">Admin</span>
                                    </div>
                                ))}
                                {Object.entries(membersData.members).map(([id, username]) => (
                                    <div key={id} className="member-row member-row-clickable" onClick={() => openMemberProfile(id)}>
                                        <div className="member-avatar">
                                            {memberImages[id] ? (
                                                <img src={memberImages[id]!} alt={username} className="member-avatar-img" />
                                            ) : (
                                                <span className="member-avatar-placeholder">{username.charAt(0).toUpperCase()}</span>
                                            )}
                                        </div>
                                        <span className="member-name">{username}</span>
                                    </div>
                                ))}
                            </div>
                        )}
                        <div className="modal-actions">
                            <button className="btn btn-secondary" onClick={() => setIsMembersOpen(false)}>
                                Close
                            </button>
                        </div>
                    </div>
                </div>
            )}
            {viewProfileUserId && (
                <div className="modal-overlay modal-overlay-top" onClick={closeMemberProfile}>
                    <div className="modal modal-profile" onClick={(e) => e.stopPropagation()}>
                        {viewProfileLoading ? (
                            <p className="text-muted">Loading…</p>
                        ) : viewProfile ? (
                            <>
                                <h3 className="modal-title">{viewProfile.name || viewProfile.username || "Profile"}</h3>

                                <div className="profile-avatar-section">
                                    <div
                                        className={`profile-avatar profile-avatar-static${viewProfile.profilePicture ? " profile-avatar-clickable" : ""}`}
                                        onClick={() => viewProfile.profilePicture && setViewProfileFullPicture(true)}
                                    >
                                        {viewProfile.profilePicture ? (
                                            <img src={viewProfile.profilePicture} alt="Profile" className="profile-avatar-img" />
                                        ) : (
                                            <span className="profile-avatar-placeholder">
                                                {(viewProfile.name ?? viewProfile.username ?? "?").charAt(0).toUpperCase()}
                                            </span>
                                        )}
                                    </div>
                                    <div className="profile-avatar-info">
                                        <span className="profile-avatar-username">{viewProfile.username}</span>
                                        <span className="profile-avatar-email">{viewProfile.email}</span>
                                    </div>
                                </div>

                                <div className="profile-form">
                                    <div className="profile-readonly-field">
                                        <span className="profile-readonly-label">Display Name</span>
                                        <span className="profile-readonly-value">
                                            {viewProfile.name || <span className="text-muted">Not set</span>}
                                        </span>
                                    </div>
                                    <div className="profile-readonly-field">
                                        <span className="profile-readonly-label">Bio</span>
                                        <span className="profile-readonly-value profile-readonly-bio">
                                            {viewProfile.bio || <span className="text-muted">Not set</span>}
                                        </span>
                                    </div>
                                </div>

                                <div className="modal-actions">
                                    <button className="btn btn-secondary" onClick={closeMemberProfile}>
                                        Close
                                    </button>
                                </div>
                            </>
                        ) : (
                            <>
                                <p className="text-muted">Could not load profile.</p>
                                <div className="modal-actions">
                                    <button className="btn btn-secondary" onClick={closeMemberProfile}>
                                        Close
                                    </button>
                                </div>
                            </>
                        )}
                    </div>
                </div>
            )}
            {viewProfileFullPicture && viewProfile?.profilePicture && (
                <div className="lightbox-overlay" onClick={() => setViewProfileFullPicture(false)}>
                    <img
                        src={viewProfile.profilePicture}
                        alt="Profile"
                        className="lightbox-img"
                        onClick={(e) => e.stopPropagation()}
                    />
                </div>
            )}
        </PageLayout>
    );

}