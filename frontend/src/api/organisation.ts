import api from "./client";

export const getAllOrganisations = async () => {
    return await api.get("/organisation/get/all");
};