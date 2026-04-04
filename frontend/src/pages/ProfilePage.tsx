import { useEffect, useRef, useState } from "react";
import { getProfile, updateProfile, uploadProfilePicture } from "../api/user";
import type { UserProfile } from "../types/models";
import { useHandleUnauthorised } from "../Utils";
import PageLayout from "../components/PageLayout";

export default function ProfilePage() {
    const [profile, setProfile] = useState<UserProfile | null>(null);
    const [loading, setLoading] = useState(true);

    const [editName, setEditName] = useState("");
    const [editBio, setEditBio] = useState("");
    const [isSaving, setIsSaving] = useState(false);
    const [isUploading, setIsUploading] = useState(false);
    const [saveSuccess, setSaveSuccess] = useState(false);

    const fileInputRef = useRef<HTMLInputElement>(null);
    const handleUnauthorised = useHandleUnauthorised();

    const load = async () => {
        try {
            const data = await getProfile();
            setProfile(data);
            setEditName(data.name ?? "");
            setEditBio(data.bio ?? "");
        } catch (err: any) {
            handleUnauthorised(err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        load();
    }, []);

    const handleSave = async () => {
        if (isSaving) return;
        try {
            setIsSaving(true);
            setSaveSuccess(false);
            const data = await updateProfile(editName, editBio);
            setProfile(data);
            setEditName(data.name ?? "");
            setEditBio(data.bio ?? "");
            setSaveSuccess(true);
            setTimeout(() => setSaveSuccess(false), 2000);
        } catch (err: any) {
            handleUnauthorised(err);
        } finally {
            setIsSaving(false);
        }
    };

    const handlePictureClick = () => {
        fileInputRef.current?.click();
    };

    const handleFileChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
        const file = e.target.files?.[0];
        if (!file) return;

        if (!file.type.startsWith("image/")) {
            alert("Please select an image file.");
            return;
        }
        if (file.size > 2 * 1024 * 1024) {
            alert("Image must be under 2 MB.");
            return;
        }

        try {
            setIsUploading(true);
            const data = await uploadProfilePicture(file);
            setProfile(data);
        } catch (err: any) {
            handleUnauthorised(err);
        } finally {
            setIsUploading(false);
            if (fileInputRef.current) fileInputRef.current.value = "";
        }
    };

    const hasChanges =
        profile != null &&
        (editName !== (profile.name ?? "") || editBio !== (profile.bio ?? ""));

    if (loading) {
        return (
            <PageLayout backTo="/dashboard" backLabel="Dashboard">
                <div className="empty-state">
                    <p className="empty-state-text">Loading…</p>
                </div>
            </PageLayout>
        );
    }

    return (
        <PageLayout backTo="/dashboard" backLabel="Dashboard">
            <h1 className="page-title">Profile</h1>
            <p className="page-subtitle">Manage your personal information.</p>

            {/* Avatar section */}
            <div className="profile-avatar-section">
                <button
                    type="button"
                    className="profile-avatar"
                    onClick={handlePictureClick}
                    disabled={isUploading}
                    title="Change profile picture"
                >
                    {profile?.profilePicture ? (
                        <img
                            src={profile.profilePicture}
                            alt="Profile"
                            className="profile-avatar-img"
                        />
                    ) : (
                        <span className="profile-avatar-placeholder">
                            {(profile?.name ?? profile?.username ?? "?").charAt(0).toUpperCase()}
                        </span>
                    )}
                    <span className="profile-avatar-overlay">
                        {isUploading ? "Uploading…" : "Change"}
                    </span>
                </button>
                <input
                    ref={fileInputRef}
                    type="file"
                    accept="image/*"
                    onChange={handleFileChange}
                    style={{ display: "none" }}
                />
                <div className="profile-avatar-info">
                    <span className="profile-avatar-username">{profile?.username}</span>
                    <span className="profile-avatar-email">{profile?.email}</span>
                </div>
            </div>

            {/* Edit form */}
            <div className="profile-form">
                <label className="modal-field">
                    <span>Display Name</span>
                    <input
                        type="text"
                        className="profile-input"
                        value={editName}
                        onChange={(e) => setEditName(e.target.value)}
                        placeholder="Enter your display name"
                        disabled={isSaving}
                    />
                </label>

                <label className="modal-field">
                    <span>Bio</span>
                    <textarea
                        className="profile-textarea"
                        value={editBio}
                        onChange={(e) => setEditBio(e.target.value)}
                        placeholder="Tell others a bit about yourself"
                        rows={4}
                        disabled={isSaving}
                    />
                </label>

                <div className="profile-form-actions">
                    {saveSuccess && (
                        <span className="profile-save-success">✓ Saved</span>
                    )}
                    <button
                        className="btn btn-primary"
                        onClick={handleSave}
                        disabled={isSaving || !hasChanges}
                    >
                        {isSaving ? "Saving…" : "Save Changes"}
                    </button>
                </div>
            </div>
        </PageLayout>
    );
}
