import api from "./client";
import type {LoginRequest, NewUserRequest} from "../types/models";

export const login = async (data: LoginRequest) => {
    const res = await api.post("/user/login", data);
    return res.data;
};

export const register = async (data: NewUserRequest) => {
    const res = await api.post("/user/new", data);
    return res.data;
};
