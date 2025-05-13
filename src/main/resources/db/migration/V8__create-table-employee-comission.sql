CREATE TABLE employee_comission (
    id UUID PRIMARY KEY,
    employee_id UUID, FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE NOT NULL,
    service_type_id UUID, FOREIGN KEY (service_type_id) REFERENCES service_type(id) ON DELETE CASCADE NOT NULL,
    custom_percentage DECIMAL NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
)