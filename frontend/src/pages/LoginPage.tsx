import { useState, useContext } from "react";
import { login } from "../api/auth";
import { AuthContext } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";

export default function LoginPage() {
    const [identifier, setIdentifier] = useState("");
    const [password, setPassword] = useState("");
    const { setToken } = useContext(AuthContext);
    const navigate = useNavigate();

    const handleLogin = async () => {
        const res = await login({ identifier, password });
        setToken(res.token);
        navigate("/dashboard");
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
        </div>
    );
}
