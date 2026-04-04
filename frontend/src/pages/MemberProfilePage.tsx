import { useEffect, useState } from "react";
import { getUserProfile } from "../api/user";
import type { UserProfile } from "../types/models";
import { useHandleUnauthorised } from "../Utils";
import { useLocation, useNavigate } from "react-router-dom";
import PageLayout from "../components/PageLayout";

export default function MemberProfilePage() {
    const [profile, setProfile] = useState<UserProfile | null>(null);
    const [loading, setLoading] = useState(true);
    const [showFullPicture, setShowFullPicture] = useState(false);
    const handleUnauthorised = useHandleUnauthorised();
    const navigate = useNavigate();
    const location = useLocation();
    const userId = location.state?.userId as string | undefined;

    useEffect(() => {
        if (!userId) {
            navigate("/dashboard");
            return;
        }
        (async () => {
            try {
                const data = await getUserProfile(userId);
                setProfile(data);
            } catch (err: any) {
                handleUnauthorised(err);
            } finally {
                setLoading(false);
            }
        })();
    }, [userId]);

    if (loading) {
        return (
            <PageLayout onBack={() => navigate(-1)} backLabel="Back">
                <div className="empty-state">
                    <p className="empty-state-text">Loading…</p>
                </div>
            </PageLayout>
        );
    }

    return (
        <PageLayout onBack={() => navigate(-1)} backLabel="Back">
            <h1 className="page-title">{profile?.name || profile?.username || "Profile"}</h1>
            <p className="page-subtitle">Member profile</p>

            {/* Avatar section */}
            <div className="profile-avatar-section">
                <div
                    className={`profile-avatar profile-avatar-static${profile?.profilePicture ? " profile-avatar-clickable" : ""}`}
                    onClick={() => profile?.profilePicture && setShowFullPicture(true)}
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
                </div>
                <div className="profile-avatar-info">
                    <span className="profile-avatar-username">{profile?.username}</span>
                    <span className="profile-avatar-email">{profile?.email}</span>
                </div>
            </div>

            {showFullPicture && profile?.profilePicture && (
                <div className="lightbox-overlay" onClick={() => setShowFullPicture(false)}>
                    <img
                        src={profile.profilePicture}
                        alt="Profile"
                        className="lightbox-img"
                        onClick={(e) => e.stopPropagation()}
                    />
                </div>
            )}

            {/* Read-only details */}
            <div className="profile-form">
                <div className="profile-readonly-field">
                    <span className="profile-readonly-label">Display Name</span>
                    <span className="profile-readonly-value">
                        {profile?.name || <span className="text-muted">Not set</span>}
                    </span>
                </div>

                <div className="profile-readonly-field">
                    <span className="profile-readonly-label">Bio</span>
                    <span className="profile-readonly-value profile-readonly-bio">
                        {profile?.bio || <span className="text-muted">Not set</span>}
                    </span>
                </div>
            </div>
        </PageLayout>
    );
}
