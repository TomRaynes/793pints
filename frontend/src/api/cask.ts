import api from "./client";

export const getAllCasks = async (
    organisationId: string | null,
    cellarId: string | null
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

export const updateCask = async (
    organisationId: string,
    cellarId: string,
    caskId: string,
    caskName: string,
    state: string,
    rackCooldownHours: string,
    ventCooldownHours: string,
    tapCooldownHours: string,
    pullingPeriodHours: string
) => {
    const res = await api.post("/cask/update", {
        organisationId,
        cellarId,
        caskId,
        caskName,
        state,
        rackCooldownHours,
        ventCooldownHours,
        tapCooldownHours,
        pullingPeriodHours
    });
    return res.data;
};
