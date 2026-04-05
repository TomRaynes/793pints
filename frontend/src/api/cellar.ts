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

export interface CellarConfig {
    rackCooldownDefault: number;
    ventCooldownDefault: number;
    tapCooldownDefault: number;
    pullingPeriodDefault: number;
}

export interface ConfigField {
    value: number;
    applyToAll: boolean;
}

export interface UpdateCellarConfigRequest {
    rackCooldownDefault: ConfigField;
    ventCooldownDefault: ConfigField;
    tapCooldownDefault: ConfigField;
    pullingPeriodDefault: ConfigField;
}

export const getCellarConfig = async (cellarId: string): Promise<CellarConfig> => {
    const res = await api.get(`/cellar/${cellarId}/config`);
    return res.data;
};

export const updateCellarConfig = async (cellarId: string, request: UpdateCellarConfigRequest) => {
    const res = await api.post(`/cellar/${cellarId}/update_config`, request);
    return res.data;
};

