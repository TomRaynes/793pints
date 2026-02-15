import api from "./client";

export const getAllOrganisations = async () => {
    const res = await api.get("/organisation/get/all");
    return res.data;
};