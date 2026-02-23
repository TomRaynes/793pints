import { HttpStatusCode } from "axios";
import { useCallback } from "react";
import { useNavigate } from "react-router-dom";
import api from "./api/client.ts";

export function useHandleUnauthorised() {
    const navigate = useNavigate();

    return useCallback((err: any) => {
        const status = err?.response?.status;
        console.log(err);


        if (status === HttpStatusCode.Unauthorized || status === HttpStatusCode.BadRequest) {
            console.log("unauthorised");
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