import type { Cask } from "../types/models";
import CaskCard from "./CaskCard";

interface Props {
    status: string;
    casks: Cask[];
    organisationId: string | null;
    cellarId: string | null;
    onUpdateCask: (updated: Cask) => void;
    onRemoveCask: (caskId: string) => void;
    onError: (err: unknown) => void;
}

const statusDotClass: Record<string, string> = {
    "Delivered": "status-dot-delivered",
    "Racked": "status-dot-racked",
    "Settled": "status-dot-settled",
    "Vented": "status-dot-vented",
    "Needs Tap": "status-dot-needs-tap",
    "Tapped": "status-dot-tapped",
    "Ready to Serve": "status-dot-ready",
    "Pulling": "status-dot-pulling",
    "Tired": "status-dot-tired",
};

export default function StatusGroup({ status, casks, organisationId, cellarId, onUpdateCask, onRemoveCask, onError }: Props) {
    if (casks.length === 0) return null;

    return (
        <div className="status-group">
            <div className="status-group-header">
                <span className={`status-dot ${statusDotClass[status] ?? ""}`} />
                <span className="status-group-title">{status}</span>
                <span className="status-group-count">{casks.length}</span>
            </div>
            {casks.map((c) => (
                <CaskCard
                    key={c.caskId}
                    cask={c}
                    organisationId={organisationId}
                    cellarId={cellarId}
                    onUpdate={onUpdateCask}
                    onRemove={onRemoveCask}
                    onError={onError}
                />
            ))}
        </div>
    );
}
