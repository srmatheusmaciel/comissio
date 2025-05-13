CREATE TYPE payment_status AS ENUM ('PENDING', 'PAID');

CREATE TABLE comission_payment (
    id UUID PRIMARY KEY,
    employee_id UUID, FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE,
    performed_service_id UUID, FOREIGN KEY (performed_service_id) REFERENCES performed_service(id) ON DELETE CASCADE,
    amount_paid DECIMAL NOT NULL,
    status payment_status DEFAULT 'PENDING',
    payment_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
)