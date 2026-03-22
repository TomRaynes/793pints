import api from "./client";

export const getAllOrganisations = async () => {
    return await api.get("/organisation/get/all");
};

export const newOrganisation = async (name: string | null) => {
    const res = await api.post("/organisation/new", {
        name,
    });
    return res.data;
};