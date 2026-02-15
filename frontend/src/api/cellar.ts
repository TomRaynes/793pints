import api from "./client.ts";

export const getAllCellars = async (organisationId: string) => {
    const res = await api.post("/cellar/get/all", { organisationId: organisationId });
    return res.data;
};