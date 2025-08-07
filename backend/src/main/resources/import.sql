-- Insert default config (UUID version)
INSERT INTO config (id, code, value)
VALUES ('99999999-9999-9999-9999-999999999999', 'SIGNATURE_THRESHOLD', '0.8');

-- Insert mock user
INSERT INTO app_user (id, full_name, email, username, password, role)
VALUES ('11111111-1111-1111-1111-111111111111', 'Nguyen Van A', 'a@example.com', 'user_a', 'password123', 'USER');

-- Insert mock application linked to user
INSERT INTO application (id, user_id, signature_image, status)
VALUES ('22222222-2222-2222-2222-222222222222', '11111111-1111-1111-1111-111111111111', 'test-img-1', 'CREATED');