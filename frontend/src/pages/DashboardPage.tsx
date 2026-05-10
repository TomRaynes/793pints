import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import PageLayout from "../components/PageLayout";
import { getPinnedCellars, type PinnedCellarInfo } from "../api/user";
import { useHandleUnauthorised } from "../Utils";

export default function DashboardPage() {
    const navigate = useNavigate();
    const handleUnauthorised = useHandleUnauthorised();
    const [pinned, setPinned] = useState<PinnedCellarInfo[]>([]);
    const [pinnedLoading, setPinnedLoading] = useState(true);

    useEffect(() => {
        (async () => {
            try {
                const data = await getPinnedCellars();
                setPinned(data);
            } catch (err: any) {
                handleUnauthorised(err);
            } finally {
                setPinnedLoading(false);
            }
        })();
    }, []);

    const goToPinnedCellar = (info: PinnedCellarInfo) => {
        navigate("/cellar", {
            state: {
                organisationId: info.organisationId,
                organisationName: info.organisationName,
                cellar: { id: info.cellarId, name: info.cellarName },
                cachedMembers: null,
                cachedMemberImages: null,
                cachedCellars: null,
                cachedAccessLevel: null,
            },
        });
    };

    return (
        <PageLayout>
            <h1 className="page-title">Dashboard</h1>
            <p className="page-subtitle">Welcome back!</p>

            {!pinnedLoading && pinned.length > 0 && (
                <section className="pinned-section">
                    <h2 className="pinned-section-title">📌 Pinned Cellars</h2>
                    <div className="pinned-grid">
                        {pinned.map((p) => (
                            <button
                                key={p.cellarId}
                                className="pinned-card"
                                type="button"
                                onClick={() => goToPinnedCellar(p)}
                            >
                                <div className="pinned-card-icon">🛢️</div>
                                <div className="pinned-card-content">
                                    <span className="pinned-card-name">{p.cellarName}</span>
                                    <span className="pinned-card-org">{p.organisationName}</span>
                                </div>
                                <span className="pinned-card-chevron">›</span>
                            </button>
                        ))}
                    </div>
                </section>
            )}

            <div className="dashboard-grid">
                <button className="dashboard-card" type="button" onClick={() => navigate("/profile")}>
                    <div className="dashboard-card-icon">👤</div>
                    <span className="dashboard-card-label">Profile</span>
                </button>
                <button className="dashboard-card" type="button">
                    <div className="dashboard-card-icon">⚙️</div>
                    <span className="dashboard-card-label">Account</span>
                </button>
                <button className="dashboard-card" type="button" onClick={() => navigate("/invitations")}>
                    <div className="dashboard-card-icon">✉️</div>
                    <span className="dashboard-card-label">Invitations</span>
                </button>
                <button className="dashboard-card" type="button" onClick={() => navigate("/organisations")}>
                    <div className="dashboard-card-icon">🏢</div>
                    <span className="dashboard-card-label">Organisations</span>
                </button>
            </div>
        </PageLayout>
    );
}

