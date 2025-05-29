ALTER TABLE performed_service
ADD COLUMN service_date DATE;

UPDATE performed_service
SET service_date = COALESCE(CAST(created_at AS DATE), CURRENT_DATE)
WHERE service_date IS NULL;

ALTER TABLE performed_service
ALTER COLUMN service_date SET NOT NULL;