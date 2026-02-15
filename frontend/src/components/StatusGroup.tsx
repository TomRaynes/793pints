import type {Cask} from "../types/models";
import CaskCard from "./CaskCard";

interface Props {
    status: string;
    casks: Cask[];
}

export default function StatusGroup({ status, casks }: Props) {
    if (casks.length === 0) return null;

    return (
        <div className="status-group">
            <h3>{status}</h3>
            {casks.map((c) => (
                <CaskCard key={c.caskId} cask={c} />
            ))}
        </div>
    );
}
