import { useNavigate } from "react-router-dom";
export default function DashboardPage() {
    const navigate = useNavigate();

    return (
        <div className="container">
            <div style={{ display: 'flex', justifyContent: 'center' }}>
                <h2>Dashboard</h2>
            </div>

            <div className="button-group" style={{ display: "flex", flexDirection: "column", gap: 8 }}>
                <button type="button">
                    Profile
                </button>
                <button type="button">
                    Account
                </button>
                <button type="button" onClick={() => navigate("/invitations")}>
                    Invitations
                </button>
                <button type="button" onClick={() => navigate("/organisations")}>
                    Organisations
                </button>
            </div>
        </div>
    );
}