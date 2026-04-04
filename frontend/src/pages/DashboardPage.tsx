import { useNavigate } from "react-router-dom";
import PageLayout from "../components/PageLayout";

export default function DashboardPage() {
    const navigate = useNavigate();

    return (
        <PageLayout>
            <h1 className="page-title">Dashboard</h1>
            <p className="page-subtitle">Welcome back!</p>

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