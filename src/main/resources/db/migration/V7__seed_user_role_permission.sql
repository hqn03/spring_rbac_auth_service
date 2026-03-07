INSERT INTO permissions (name, description) VALUES
('USER:CREATE', 'Create new user'),
('USER:READ', 'View user'),
('USER:UPDATE', 'Update user'),
('USER:DELETE', 'Delete user'),
('ROLE:CREATE', 'Create new role'),
('ROLE:READ', 'View role'),
('ROLE:UPDATE', 'Update role'),
('ROLE:DELETE', 'Delete role'),
('ROLE:ASSIGN_PERMISSION', 'Assign permission to role');

INSERT INTO roles (name, description) VALUES
('SUPER_ADMIN', 'System super administrator'),
('ADMIN', 'Administrator'),
('USER', 'Normal user');

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
    CROSS JOIN permissions p
WHERE r.name = 'SUPER_ADMIN';

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
    JOIN permissions p
    ON p.name IN (
      'USER:CREATE',
      'USER:READ',
      'USER:UPDATE',
      'USER:DELETE',
      'ROLE:READ'
    )
WHERE r.name = 'ADMIN';

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
    JOIN permissions p ON p.name = 'USER:READ'
WHERE r.name = 'USER';

INSERT INTO users (username, email, password, enabled, blocked)
VALUES ('superadmin', 'superadmin@gmail.com', '$2a$10$Uf8hesYwFaWzZz11AzX1nOmqV5lHn/hb2o4F6tkzl1XTttJm3.nCq', true, false),
       ('admin', 'admin@gmail.com', '$2a$10$Uf8hesYwFaWzZz11AzX1nOmqV5lHn/hb2o4F6tkzl1XTttJm3.nCq', true, false),
       ('qclabburn1', 'tsharple1@1und1.de', 'rC8&W''+.FIF"nWP', false, false),
       ('nreef2', 'etinman2@google.co.uk', 'xF5,gi+hWA_LF', false, true),
       ('ekamen0', 'swanless0@umich.edu', 'nA0!wjY`z', true, false),
       ('agotfrey3', 'nmcmonies3@discuz.net', 'oJ2''.Mb*UO*', true, true),
       ('lblinkhorn4', 'mdumphries4@ebay.co.uk', 'aU3}2)KB', true, true),
       ('ycrilley5', 'sblackborough5@ezinearticles.com', 'rY0''>?XVq)yT', true, true),
       ('dpresho6', 'glinneman6@cornell.edu', 'eJ5}iQ9gv\l+!vmU', false, false),
       ('sdanford7', 'svaldes7@jimdo.com', 'xF1#_1MYsfn+,''x', true, false),
       ('mpinchen8', 'gwellman8@examiner.com', 'aB0|*z$u*(rl.eL', true, false),
       ('psemonin9', 'lmccolgan9@chron.com', 'pP4!g3OU0', false, true);

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
    JOIN roles r ON r.name = 'SUPER_ADMIN'
WHERE u.username = 'superadmin';

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
    JOIN roles r ON r.name = 'ADMIN'
WHERE u.username = 'admin';

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
         JOIN roles r ON r.name = 'USER'
WHERE u.username NOT IN ('admin', 'superadmin');