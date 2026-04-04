import api from "./client";

export const getProfile = async () => {
    const res = await api.get("/user/profile");
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
