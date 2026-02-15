import api from "./client";

export const getAllCasks = async (
    organisationId: string,
    cellarId: string
) => {
    const res = await api.post("/cask/get/all", {
        organisationId,
        cellarId,
    });
    return res.data;
};

export const createCask = async (
    organisationId: string,
    cellarId: string,
    caskName: string,
    state: string
) => {
    const res = await api.post("/cask/new", {
        organisationId,
        cellarId,
        caskName,
        state,
    });
    return res.data;
};
