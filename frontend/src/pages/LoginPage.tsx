import { useState, useContext } from "react";
import { login } from "../api/auth";
import { AuthContext } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";
import { HttpStatusCode } from "axios";

export default function LoginPage() {
    const [identifier, setIdentifier] = useState("");
    const [password, setPassword] = useState("");
    const [incorrectAttempt, setIncorrectAttempt] = useState(false);
    const { setToken } = useContext(AuthContext);
    const navigate = useNavigate();

    const handleLogin = async () => {
        setIncorrectAttempt(false);

        try {
            const res = await login({ identifier, password });
            setToken(res.data.token);
            navigate("/dashboard");
        } catch (err: any) {
            const status = err?.response?.status;

            if (status === HttpStatusCode.Unauthorized) {
                setIncorrectAttempt(true);
            }
        }
    };

    const handleKeyDown = (e: React.KeyboardEvent) => {
        if (e.key === "Enter") handleLogin();
    };

    return (
        <div className="login-wrapper">
            <div className="login-card">
                <div className="login-brand">
                    793 <span className="login-brand-accent">Pints</span>
                </div>
                <p className="login-tagline">Cellar management, simplified.</p>

                <div className="login-form" onKeyDown={handleKeyDown}>
                    <div className="form-group">
                        <label className="form-label">Email or Username</label>
                        <input
                            className="form-input"
                            type="text"
                            placeholder="Enter your email or username"
                            value={identifier}
                            onChange={(e) => setIdentifier(e.target.value)}
                            autoFocus
                        />
                    </div>
                    <div className="form-group">
                        <label className="form-label">Password</label>
                        <input
                            className="form-input"
                            type="password"
                            placeholder="Enter your password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                        />
                    </div>

                    {incorrectAttempt && (
                        <div className="login-error">
                            Incorrect username or password. Please try again.
                        </div>
                    )}

                    <button
                        className="btn btn-primary login-submit"
                        onClick={handleLogin}
                        style={{ width: "100%" }}
                    >
                        Sign In
                    </button>
                </div>
            </div>
        </div>
    );
}
