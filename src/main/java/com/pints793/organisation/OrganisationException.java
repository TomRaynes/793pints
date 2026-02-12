package com.pints793.organisation;

public abstract class OrganisationException extends Exception {
    public static class RemovingOwnerAsAdmin extends OrganisationException { }
    public static class RemovingNonAdmin extends OrganisationException { }
    public static class AddingExistingAdmin extends OrganisationException { }

    public static class RemovingOwnerAsMember extends OrganisationException { }
    public static class RemovingNonMember extends OrganisationException { }
    public static class AddingExistingMember extends OrganisationException { }

}
