import { useNavigate } from "react-router-dom";
import { useContext } from "react";
import { AuthContext } from "../context/AuthContext";

interface Props {
    children: React.ReactNode;
    backTo?: string;
    backLabel?: string;
}

export default function PageLayout({ children, backTo, backLabel }: Props) {
    const navigate = useNavigate();
    const { setToken } = useContext(AuthContext);

    const handleLogout = () => {
        setToken(null);
        navigate("/");
    };

    return (
        <div className="page-shell">
            <header className="page-header">
                <span className="brand" onClick={() => navigate("/dashboard")}>
                    793 Pints
                </span>
                <div className="nav-actions">
                    {backTo && (
                        <button className="btn btn-nav" onClick={() => navigate(backTo)}>
                            ← {backLabel || "Back"}
                        </button>
                    )}
                    <button className="btn btn-nav" onClick={handleLogout}>
                        Sign Out
                    </button>
                </div>
            </header>
            <main className="page-content">
                {children}
            </main>
        </div>
    );
}
