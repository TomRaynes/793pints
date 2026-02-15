import {useEffect, useState} from "react";
import type {EntityLabel} from "../types/models.ts";
import {getAllOrganisations} from "../api/organisation.ts";
import { useNavigate } from "react-router-dom";

export default function ChooseOrganisationPage() {
    const [organisations, setOrganisations] = useState<EntityLabel[]>([]);
    const navigate = useNavigate();

    const load = async () => {
        const data = await getAllOrganisations();
        const rawList = Array.isArray(data) ? data : data?.organisations ?? [];
        // setOrganisations(rawList);
        setOrganisations([...rawList].sort((a, b) => a.name.localeCompare(b.name)));
    };

    useEffect(() => {
        load();
    }, []);

    const goToOrganisation = (organisation: EntityLabel) => {
        navigate("/organisation", { state: organisation });
    };

    const newOrganisation = () => {

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
        </div>
    );
}