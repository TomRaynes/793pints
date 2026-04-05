import api from "./client";

export const getProfile = async () => {
    const res = await api.get("/user/profile");
    return res.data;
};

export const getUserProfile = async (userId: string) => {
    const res = await api.get(`/user/profile/${userId}`);
    return res.data;
};

export const updateProfile = async (name: string | null, bio: string | null) => {
    const res = await api.post("/user/profile/update", { name, bio });
    return res.data;
};

export const uploadProfilePicture = async (file: File) => {
    const formData = new FormData();
    formData.append("file", file);
    const res = await api.post("/user/profile/picture", formData, {
        headers: { "Content-Type": "multipart/form-data" },
    });
    return res.data;
};

export const getProfileImage = async (id: string): Promise<string | null> => {
    try {
        const res = await api.get(`/user/profile_image/${id}`);
        return res.data || null;
    } catch {
        return null;
    }
};

export const getProfileImages = async (userIds: string[]): Promise<Record<string, string>> => {
    try {
        const res = await api.post("/user/profile_images", userIds);
        return res.data || {};
    } catch {
        return {};
    }
};

