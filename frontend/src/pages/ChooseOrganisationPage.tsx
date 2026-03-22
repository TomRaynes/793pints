import {useEffect, useState} from "react";
import type {EntityLabel} from "../types/models.ts";
import {getAllOrganisations, newOrganisation as createOrganisation} from "../api/organisation.ts";
import { useNavigate } from "react-router-dom";
import {useHandleUnauthorised} from "../Utils.tsx";

export default function ChooseOrganisationPage() {
    const [organisations, setOrganisations] = useState<EntityLabel[]>([]);

    // Add new state variables for popup
    const [isNewOrgOpen, setIsNewOrgOpen] = useState(false);
    const [newOrgName, setNewOrgName] = useState("");
    const [isCreating, setIsCreating] = useState(false);

    const navigate = useNavigate();
    const handleUnauthorised = useHandleUnauthorised();

    const load = async () => {
        try {
            const res = await getAllOrganisations();
            const data = res.data
            const rawList = Array.isArray(data) ? data : data?.organisations ?? [];
            // setOrganisations(rawList);
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

    // @ts-ignore
    return (
        <div className="container">
            <div style={{ display: 'flex', justifyContent: 'center' }}><h2>Organisations</h2></div>


            {organisations.length === 0 ? (
                <div>No organisations found.</div>
            ) : (
                <div className="button-group" style={{ display: "flex", flexDirection: "column", gap: 8 }}>
                    {organisations.map((org) => (
                        <button key={org.id} type="button" onClick={() => goToOrganisation(org)}>
                            {org.name}
                        </button>
                    ))}
                </div>
            )}
            <br/>
            <br/>

            <div style={{ display: 'flex', justifyContent: 'center' }}>
                <button key="new org" type="button" onClick={() => newOrganisation()}>
                    New Organisation
                </button>
            </div>

            {isNewOrgOpen && (
                <div className="modal-overlay" onClick={closeNewOrg}>
                    <div className="modal" onClick={(e) => e.stopPropagation()}>
                        <h3>New Organisation</h3>
                        <input
                            type="text"
                            value={newOrgName}
                            onChange={(e) => setNewOrgName(e.target.value)}
                            placeholder="Enter organisation name"
                            autoFocus
                        />
                        <div className="modal-actions">
                            <button type="button" onClick={closeNewOrg} disabled={isCreating}>
                                Cancel
                            </button>
                            <button type="button" onClick={submitNewOrg} disabled={isCreating || !newOrgName.trim()}>
                                {isCreating ? "Creating..." : "Create"}
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}