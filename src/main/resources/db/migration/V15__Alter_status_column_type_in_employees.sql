-- V15__Alter_status_column_type_in_employees.sql

-- Adiciona a nova coluna de texto temporária
ALTER TABLE employees ADD COLUMN status_text TEXT;

-- Popula a nova coluna convertendo o valor numérico (integer/smallint) da coluna 'status' antiga para texto.
-- O CAST explícito do status para integer resolve a ambiguidade do operador.
UPDATE employees
SET status_text =
    CASE
        WHEN CAST(status AS INTEGER) = 0 THEN 'ACTIVE'
        WHEN CAST(status AS INTEGER) = 1 THEN 'INACTIVE'
        WHEN CAST(status AS INTEGER) = 2 THEN 'ON_LEAVE'
        ELSE 'INACTIVE' -- Um valor padrão seguro
    END;

-- Remove a coluna 'status' antiga que era numérica
ALTER TABLE employees DROP COLUMN status;

-- Renomeia a nova coluna de texto para 'status'
ALTER TABLE employees RENAME COLUMN status_text TO status;

-- Adiciona a constraint para garantir a integridade dos dados
ALTER TABLE employees
ADD CONSTRAINT check_employees_status
CHECK (status IN ('ACTIVE', 'INACTIVE', 'ON_LEAVE'));

-- (Opcional, se você quiser definir um default e not null)
-- ALTER TABLE employees ALTER COLUMN status SET NOT NULL;
-- ALTER TABLE employees ALTER COLUMN status SET DEFAULT 'ACTIVE';