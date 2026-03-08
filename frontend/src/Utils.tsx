import { HttpStatusCode } from "axios";
import { useCallback } from "react";
import { useNavigate } from "react-router-dom";
import api from "./api/client.ts";
import type {CaskState} from "./types/models.ts";

export function useHandleUnauthorised() {
    const navigate = useNavigate();

    return useCallback((err: any) => {
        const status = err?.response?.status;

        if (status === HttpStatusCode.Unauthorized || status === HttpStatusCode.BadRequest) {
            navigate("/");
        }
    }, [navigate]);
}

export const isLoggedIn = async() => {
    try {
        const res = await api.post("/user/verify_token");
        return res.status === HttpStatusCode.Ok
    } catch (err: any) {
        return false;
    }
}

export const getNextState = (state: CaskState) : CaskState | null => {
    switch (state) {
        case "Delivered": return "Racked";
        case "Racked": return "Settled";
        case "Settled": return "Vented";
        case "Vented": return "Needs Tap";
        case "Needs Tap": return "Tapped";
        case "Tapped": return "Ready to Serve";
        case "Ready to Serve": return "Pulling";
        case "Pulling": return "Tired";
    }
    return null;
}