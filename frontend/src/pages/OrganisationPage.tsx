import {useLocation, useNavigate} from "react-router-dom";
import type {EntityLabel} from "../types/models.ts";
import {useEffect, useState} from "react";
import {getAllCellars} from "../api/cellar.ts";

export default function OrganisationPage() {
    const location = useLocation();
    const state = location.state as EntityLabel;
    const organisationName = state?.name;
    const organisationId = state?.id;

    const [cellars, setCellars] = useState<EntityLabel[]>([]);
    const navigate = useNavigate();

    const load = async () => {
        const data = await getAllCellars(organisationId);
        console.log(data);
        const rawList = Array.isArray(data) ? data : data?.cellars ?? [];
        setCellars([...rawList].sort((a, b) => a.name.localeCompare(b.name)));
    };

    useEffect(() => {
        load();
    }, []);

    const goToCellar = (cellar: EntityLabel) => {
        navigate("/cellar", { state: { organisationId: organisationId, cellar: cellar } });
    };

    const newCellar = () => {

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
            </div>
        </div>
    );

}