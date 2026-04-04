import api from "./client.ts";

export const getAllCellars = async (organisationId: string) => {
    const res = await api.post("/cellar/get/all", { organisationId: organisationId });
    return res.data;
};

export const newCellar = async (
    cellarName: string | null,
    organisationId: string | null
) => {
    const res = await api.post("/cellar/new", {
        cellarName,
        organisationId
    });
    return res.data;
};
