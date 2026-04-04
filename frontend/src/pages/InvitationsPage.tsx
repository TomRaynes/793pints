import { useState, useEffect } from "react";
import { getInvitations, acceptInvite } from "../api/organisation";
import { getProfileImage } from "../api/user";
import { useHandleUnauthorised } from "../Utils";
import PageLayout from "../components/PageLayout";

interface Invitation {
    id: string;
    senderUsername: string;
    organisationName: string;
    organisationId: string;
}

export default function InvitationsPage() {
    const [invitations, setInvitations] = useState<Invitation[]>([]);
    const [senderImages, setSenderImages] = useState<Record<string, string | null>>({});
    const [loading, setLoading] = useState(false);
    const handleUnauthorised = useHandleUnauthorised();

    const load = async () => {
        setLoading(true);
        try {
            const res = await getInvitations();
            const data: Invitation[] = res.data || [];
            setInvitations(data);

            const imageMap: Record<string, string | null> = {};
            await Promise.all(
                data.map(async (inv) => {
                    imageMap[inv.id] = await getProfileImage(inv.id);
                })
            );
            setSenderImages(imageMap);
        } catch (err: any) {
            handleUnauthorised(err);
        } finally {
            setLoading(false);
        }
    };

    const handleAccept = async (invitation: Invitation) => {
        try {
            await acceptInvite(invitation.id);
            setInvitations(prev => prev.filter(inv => inv.id !== invitation.id));
        } catch (err: any) {
            console.error(err);
            handleUnauthorised(err);
        }
    };

    useEffect(() => {
        load();
    }, []);

    return (
        <PageLayout backTo="/dashboard" backLabel="Dashboard">
            <h1 className="page-title">Invitations</h1>
            <p className="page-subtitle">Review and accept invitations to join organisations.</p>

            {loading ? (
                <div className="empty-state">
                    <p className="empty-state-text">Loading…</p>
                </div>
            ) : invitations.length === 0 ? (
                <div className="empty-state">
                    <div className="empty-state-icon">✉️</div>
                    <p className="empty-state-text">No pending invitations.</p>
                </div>
            ) : (
                <div className="list-stack">
                    {invitations.map(invitation => {
                        const image = senderImages[invitation.id];
                        return (
                            <div key={invitation.id} className="invitation-card">
                                <div className="invitation-avatar">
                                    {image ? (
                                        <img src={image} alt={invitation.senderUsername} className="invitation-avatar-img" />
                                    ) : (
                                        <span className="invitation-avatar-placeholder">
                                            {invitation.senderUsername.charAt(0).toUpperCase()}
                                        </span>
                                    )}
                                </div>
                                <div className="invitation-content">
                                    <span className="invitation-sender">{invitation.senderUsername}</span>
                                    {" has invited you to join "}
                                    <span className="invitation-org">{invitation.organisationName}</span>
                                </div>
                                <button
                                    className="btn btn-success btn-sm"
                                    onClick={() => handleAccept(invitation)}
                                >
                                    Accept
                                </button>
                            </div>
                        );
                    })}
                </div>
            )}
        </PageLayout>
    );
}
