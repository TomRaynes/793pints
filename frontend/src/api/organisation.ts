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

export const inviteToOrganisation = async (
    organisationId: string | null,
    recipientIdentifier: string | null
) => {
    const res = await api.post("/organisation/invite", {
        organisationId,
        recipientIdentifier
    });
    return res.data;
};

export const getUserAccessLevel = async (
    organisationId: string
) => {
    const res = await api.post("/organisation/user/access_level", {
        organisationId
    });
    return res.data;
};

export const getInvitations = async () => {
    return await api.get("/user/invitations");
};

export const acceptInvite = async (
    invitationId: string
) => {
    const res = await api.post("/organisation/invite/accept", {
        invitationId
    });
    return res.data;
};