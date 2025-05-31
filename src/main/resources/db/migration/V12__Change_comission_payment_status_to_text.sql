ALTER TABLE comission_payment ALTER COLUMN status DROP DEFAULT;

ALTER TABLE comission_payment
ALTER COLUMN status TYPE TEXT USING status::TEXT;

ALTER TABLE comission_payment
ALTER COLUMN status SET DEFAULT 'PENDING';

ALTER TABLE comission_payment
ADD CONSTRAINT check_comission_payment_status
CHECK (status IN ('PENDING', 'PAID'));