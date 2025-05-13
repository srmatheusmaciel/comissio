CREATE TABLE comission_configuration (
    id UUID PRIMARY KEY,
    service_type_id UUID, FOREIGN KEY (service_type_id) REFERENCES service_type(id) ON DELETE CASCADE,
    default_percentage DECIMAL NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
)