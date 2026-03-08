import type {Cask} from "../types/models";

export default function CaskCard({ cask }: { cask: Cask }) {
    return (
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
            <div className="cask-card">
                <strong>{cask.caskName}</strong>
                <div>Status: {cask.state}</div>
            </div>
            <div>
                x remaining
            </div>
        </div>
    );
}
