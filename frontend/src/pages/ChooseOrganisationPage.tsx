import { useEffect, useState } from "react";
import type { EntityLabel } from "../types/models.ts";
import { getAllOrganisations, newOrganisation as createOrganisation } from "../api/organisation.ts";
import { useNavigate } from "react-router-dom";
import { useHandleUnauthorised } from "../Utils.tsx";
import PageLayout from "../components/PageLayout";

export default function ChooseOrganisationPage() {
    const [organisations, setOrganisations] = useState<EntityLabel[]>([]);
    const [isNewOrgOpen, setIsNewOrgOpen] = useState(false);
    const [newOrgName, setNewOrgName] = useState("");
    const [isCreating, setIsCreating] = useState(false);

    const navigate = useNavigate();
    const handleUnauthorised = useHandleUnauthorised();

    const load = async () => {
        try {
            const res = await getAllOrganisations();
            const data = res.data;
            const rawList = Array.isArray(data) ? data : data?.organisations ?? [];
            setOrganisations([...rawList].sort((a, b) => a.name.localeCompare(b.name)));
        } catch (err: any) {
            handleUnauthorised(err);
        }
    };

    useEffect(() => {
        load();
    }, []);

    const goToOrganisation = (organisation: EntityLabel) => {
        navigate("/organisation", { state: organisation });
    };

    const newOrganisation = () => {
        setNewOrgName("");
        setIsNewOrgOpen(true);
    };

    const closeNewOrg = () => {
        if (isCreating) return;
        setIsNewOrgOpen(false);
    };

    const submitNewOrg = async () => {
        const trimmedName = newOrgName.trim();
        if (!trimmedName) return;

        try {
            setIsCreating(true);
            await createOrganisation(trimmedName);
            await load();
            setIsNewOrgOpen(false);
        } catch (err: any) {
            console.error(err);
            handleUnauthorised(err);
        } finally {
            setIsCreating(false);
        }
    };

    return (
        <PageLayout backTo="/dashboard" backLabel="Dashboard">
            <h1 className="page-title">Organisations</h1>
            <p className="page-subtitle">Select an organisation to manage its cellars and casks.</p>

            {organisations.length === 0 ? (
                <div className="empty-state">
                    <div className="empty-state-icon">🏢</div>
                    <p className="empty-state-text">No organisations found. Create one to get started.</p>
                </div>
            ) : (
                <div className="list-stack">
                    {organisations.map((org) => (
                        <button key={org.id} className="list-item" onClick={() => goToOrganisation(org)}>
                            <div className="list-item-content">
                                <div className="list-item-title">{org.name}</div>
                            </div>
                            <span className="list-item-chevron">›</span>
                        </button>
                    ))}
                </div>
            )}

            <div className="action-bar">
                <button className="btn btn-primary" onClick={newOrganisation}>
                    + New Organisation
                </button>
            </div>

            {isNewOrgOpen && (
                <div className="modal-overlay" onClick={closeNewOrg}>
                    <div className="modal" onClick={(e) => e.stopPropagation()}>
                        <h3 className="modal-title">New Organisation</h3>
                        <label className="modal-field">
                            <span>Organisation Name</span>
                            <input
                                type="text"
                                value={newOrgName}
                                onChange={(e) => setNewOrgName(e.target.value)}
                                placeholder="Enter organisation name"
                                autoFocus
                            />
                        </label>
                        <div className="modal-actions">
                            <button className="btn btn-secondary" onClick={closeNewOrg} disabled={isCreating}>
                                Cancel
                            </button>
                            <button className="btn btn-primary" onClick={submitNewOrg} disabled={isCreating || !newOrgName.trim()}>
                                {isCreating ? "Creating…" : "Create"}
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </PageLayout>
    );
}