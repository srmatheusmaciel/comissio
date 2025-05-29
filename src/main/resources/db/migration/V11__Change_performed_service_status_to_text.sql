ALTER TABLE performed_service ALTER COLUMN status DROP DEFAULT;

ALTER TABLE performed_service
ALTER COLUMN status TYPE TEXT USING status::TEXT;

ALTER TABLE performed_service
ALTER COLUMN status SET DEFAULT 'COMMISSION_PENDING';

ALTER TABLE performed_service
ADD CONSTRAINT check_performed_service_status
CHECK (status IN ('COMMISSION_PENDING', 'COMMISSION_PAID', 'CANCELLED'));