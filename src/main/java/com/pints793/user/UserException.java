package com.pints793.user;

public abstract class UserException extends RuntimeException {
    public static class AlreadyInOrganisation extends UserException { }
    public static class NotInOrganisation extends UserException { }
    public static class CellarNotPinned extends UserException { }
}
