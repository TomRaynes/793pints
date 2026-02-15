import { useEffect, useState } from "react";
import { getAllCasks } from "../api/cask";
import type {Cask, EntityLabel} from "../types/models";
import StatusGroup from "../components/StatusGroup";
import {useLocation} from "react-router-dom";

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

const normalizeState = (state: string | undefined) => {
    if (!state) return "";
    const key = state.trim().toLowerCase();
    return stateLabelMap[key] ?? state;
};

const normalizeCask = (raw: Record<string, unknown>): Cask => {
    return {
        caskId: String(raw.caskId ?? raw.id ?? ""),
        caskName: String(raw.caskName ?? raw.name ?? ""),
        state: normalizeState(String(raw.state ?? "")) as Cask["state"],
    };
};

type CellarLocationState = {
    organisationId: string;
    cellar: EntityLabel;
};

export default function CellarPage() {
    const [casks, setCasks] = useState<Cask[]>([]);
    const location = useLocation();
    const state = location.state as CellarLocationState;

    const organisationId = state.organisationId;
    const cellarId = state.cellar.id;
    const cellarName = state.cellar.name;

    const load = async () => {
        const data = await getAllCasks(organisationId, cellarId);
        const rawList = Array.isArray(data) ? data : data?.casks ?? [];
        setCasks(rawList.map((c: Record<string, unknown>) => normalizeCask(c)));
    };

    useEffect(() => {
        load();
    }, []);

    const newCask = () => {

    };

    return (
        <div className="container">
            <div style={{ display: 'flex', justifyContent: 'center' }}>
                <h2>{cellarName}</h2>
            </div>

            {casks.length === 0 ? (
                <div>No casks found for this cellar.</div>
            ) : (
                statuses.map((status) => (
                    <StatusGroup
                        key={status}
                        status={status}
                        casks={casks.filter((c) => c.state === status)}
                    />
                ))
            )}

            <br/>
            <br/>

            <div style={{ display: 'flex', justifyContent: 'center' }}>
                <button key="new org" type="button" onClick={() => newCask()}>
                    New Cask
                </button>
            </div>
        </div>
    );
}
