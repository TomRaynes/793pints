export interface LoginRequest {
    identifier: string;
    password: string;
}

export interface NewUserRequest {
    username: string;
    email: string;
    password: string;
    confirmPassword: string;
}

export interface Cellar {
    cellarId: string;
    cellarName: string;
}

export type CaskState =
    | "Delivered"
    | "Racked"
    | "Settled"
    | "Vented"
    | "Needs Tap"
    | "Tapped"
    | "Ready to Serve"
    | "Pulling"
    | "Tired";

export interface Cask {
    caskId: string;
    caskName: string;
    state: CaskState;
    stateChangeTimestamp: Date;
    rackCooldownHours: number | null;
    ventCooldownHours: number | null;
    tapCooldownHours: number | null;
    pullingCooldownHours: number | null;
}

export interface EntityLabel {
    id: string;
    name: string;
}
