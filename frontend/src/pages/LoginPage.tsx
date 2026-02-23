import { useState, useContext } from "react";
import { login } from "../api/auth";
import { AuthContext } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";
import {HttpStatusCode} from "axios";

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

    return (
        <div className="container" style={{
            position: 'absolute',
            top: '30%',
            left: '50%',
            transform: 'translate(-50%, -50%)'
        }}>
            <div style={{ display: 'flex', justifyContent: 'center' }}>
                <h1>793 Pints Login</h1>
            </div>

            <div style={{ display: 'flex', justifyContent: 'center' }}>
                <input
                    placeholder="Email or username"
                    value={identifier}
                    onChange={(e) => setIdentifier(e.target.value)}
                    className="inputElement"
                />
                <input
                    type="password"
                    placeholder="Password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    className="inputElement"
                />
                <button onClick={handleLogin} className="inputElement">Login</button>
            </div>

            {incorrectAttempt && (
                <div style={{ display: 'flex', justifyContent: 'center', marginTop: 12 }}>
                    <span style={{ color: '#b00020' }}>Incorrect username or password.</span>
                </div>
            )}
        </div>
    );
}
