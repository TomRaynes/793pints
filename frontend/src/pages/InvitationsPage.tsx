import { useState, useEffect } from "react";
import { getInvitations, acceptInvite } from "../api/organisation";
import { useHandleUnauthorised } from "../Utils";
import { useNavigate } from "react-router-dom";

interface Invitation {
    id: string;
    senderUsername: string;
    organisationName: string;
    organisationId: string;
}

export default function InvitationsPage() {
    const [invitations, setInvitations] = useState<Invitation[]>([]);
    const [loading, setLoading] = useState(false);
    const handleUnauthorised = useHandleUnauthorised();
    const navigate = useNavigate();

    const load = async () => {
        setLoading(true);
        try {
            const res = await getInvitations();
            setInvitations(res.data || []);
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
        <div className="container">
            <div style={{ display: 'flex', justifyContent: 'center' }}>
                <h2>Invitations</h2>
            </div>

            {loading ? (
                <div>Loading...</div>
            ) : invitations.length === 0 ? (
                <div>No invitations found.</div>
            ) : (
                <div style={{ display: "flex", flexDirection: "column", gap: 8 }}>
                    {invitations.map(invitation => (
                        <div key={invitation.id} className="cask-card-row">
                            <div className="cask-card-button" style={{ cursor: "default" }}>
                                <div className="cask-card">
                                    <span style={{ marginRight: "10px" }}>{invitation.senderUsername} has invited you to join organisation <strong>{invitation.organisationName}</strong></span>
                                </div>
                            </div>
                            <button
                                className="cask-delete-button"
                                style={{ borderColor: "#28a745", color: "#28a745", backgroundColor: "#f0fff4" }}
                                onClick={() => handleAccept(invitation)}
                            >
                                Accept
                            </button>
                        </div>
                    ))}
                </div>
            )}

            <br/>
             <div style={{ display: 'flex', justifyContent: 'center' }}>
                <button onClick={() => navigate("/dashboard")}>Back to Dashboard</button>
            </div>
        </div>
    );
}
