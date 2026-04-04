import {useLocation, useNavigate} from "react-router-dom";
import type {EntityLabel} from "../types/models.ts";
import {useEffect, useState} from "react";
import {getAllCellars, newCellar as createCellar} from "../api/cellar.ts";
import {useHandleUnauthorised} from "../Utils.tsx";
import {getUserAccessLevel, inviteToOrganisation} from "../api/organisation.ts";

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
            console.log(data);
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
        <div className="container">
            <div style={{ display: 'flex', justifyContent: 'center' }}>
                <h2>{organisationName}</h2>
            </div>
            <div style={{ display: 'flex', justifyContent: 'center' }}>
                <h3>Cellars</h3>
            </div>


            {cellars.length === 0 ? (
                <div>You have no cellars</div>
            ) : (
                <div className="button-group" style={{ display: "flex", flexDirection: "column", gap: 8 }}>
                    {cellars.map((cellar) => (
                        <button key={cellar.id} type="button" onClick={() => goToCellar(cellar)}>
                            {cellar.name}
                        </button>
                    ))}
                </div>
            )}

            <br/>
            <br/>

            <div style={{ display: 'flex', justifyContent: 'center' }}>
                <button key="new org" type="button" onClick={() => newCellar()}>
                    New Cellar
                </button>
                {accessLevel === "Owner" && (
                    <button key="add member" type="button" onClick={openInviteModal} style={{ marginLeft: "10px" }}>
                        Add Member
                    </button>
                )}
            </div>

            {isInviteOpen && (
                <div className="modal-overlay" onClick={closeInviteModal}>
                    <div className="modal" onClick={(e) => e.stopPropagation()}>
                        <h3>Invite Member</h3>
                        <input
                            type="text"
                            value={inviteIdentifier}
                            onChange={(e) => setInviteIdentifier(e.target.value)}
                            placeholder="Enter username or email"
                            autoFocus
                        />
                        <div className="modal-actions">
                            <button type="button" onClick={closeInviteModal} disabled={isInviting}>
                                Cancel
                            </button>
                            <button type="button" onClick={submitInvite} disabled={isInviting || !inviteIdentifier.trim()}>
                                {isInviting ? "Inviting..." : "Invite"}
                            </button>
                        </div>
                    </div>
                </div>
            )}

            {isNewCellarOpen && (
                <div className="modal-overlay" onClick={closeNewCellarModal}>
                    <div className="modal" onClick={(e) => e.stopPropagation()}>
                        <h3>Cellar Name</h3>
                        <input
                            type="text"
                            value={newCellarName}
                            onChange={(e) => setNewCellarName(e.target.value)}
                            placeholder="Enter cellar name"
                            autoFocus
                        />
                        <div className="modal-actions">
                            <button type="button" onClick={closeNewCellarModal} disabled={isCreatingCellar}>
                                Cancel
                            </button>
                            <button type="button" onClick={submitNewCellar} disabled={isCreatingCellar || !newCellarName.trim()}>
                                {isCreatingCellar ? "Creating..." : "Create"}
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );

}