import type {Cask} from "../types/models";
import CaskCard from "./CaskCard";

interface Props {
    status: string;
    casks: Cask[];
    organisationId: string | null;
    cellarId: string | null;
    onUpdateCask: (updated: Cask) => void;
    onError: (err: unknown) => void;
}

export default function StatusGroup({ status, casks, organisationId, cellarId, onUpdateCask, onError }: Props) {
    if (casks.length === 0) return null;

    return (
        <div className="status-group">
            <h3>{status}</h3>
            {casks.map((c) => (
                <CaskCard
                    key={c.caskId}
                    cask={c}
                    organisationId={organisationId}
                    cellarId={cellarId}
                    onUpdate={onUpdateCask}
                    onError={onError}
                />
            ))}
        </div>
    );
}
