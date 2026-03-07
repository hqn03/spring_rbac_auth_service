package github.hqn03.auth_service.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PermissionConstant {
    /** USER PERMISSION **/
    public static final String USER_CREATE = "USER:CREATE";
    public static final String USER_READ = "USER:READ";
    public static final String USER_UPDATE  = "USER:UPDATE";
    public static final String USER_DELETE = "USER:DELETE";

    /** ROLE PERMISSION **/
    public static final String ROLE_CREATE = "ROLE:CREATE";
    public static final String ROLE_READ = "ROLE:READ";
    public static final String ROLE_UPDATE  = "ROLE:UPDATE";
    public static final String ROLE_DELETE = "ROLE:DELETE";
    public static final String ROLE_ASSIGN_PERMISSION = "ROLE:ASSIGN_PERMISSION";
}
