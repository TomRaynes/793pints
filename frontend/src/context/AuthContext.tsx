import { createContext, useState, type ReactNode } from "react";

interface AuthContextType {
    token: string | null;
    setToken: (t: string | null) => void;
}

export const AuthContext = createContext<AuthContextType>({
    token: null,
    setToken: () => {},
});

export const AuthProvider = ({ children }: { children: ReactNode }) => {
    const [token, setToken] = useState(localStorage.getItem("token"));

    const updateToken = (t: string | null) => {
        if (t) localStorage.setItem("token", t);
        else localStorage.removeItem("token");
        setToken(t);
    };

    return (
        <AuthContext.Provider value={{ token, setToken: updateToken }}>
            {children}
        </AuthContext.Provider>
    );
};

export const hasNoToken = () => {
    return localStorage.getItem("token") == null;
}
