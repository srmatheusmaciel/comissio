CREATE TYPE service_status AS ENUM ('REGISTERED', 'CANCELLED');

CREATE TABLE performed_service (
    id UUID PRIMARY KEY,
    employee_id UUID, FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE,
    service_type_id UUID, FOREIGN KEY (service_type_id) REFERENCES service_type(id) ON DELETE CASCADE,
    price DECIMAL NOT NULL,
    comission_amount DECIMAL NOT NULL,
    status service_status DEFAULT 'REGISTERED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
)