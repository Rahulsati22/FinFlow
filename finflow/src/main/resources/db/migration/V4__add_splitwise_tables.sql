-- 1. Create the groups table (with the creator tracked!)
CREATE TABLE groups (
                        id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                        name VARCHAR(255) NOT NULL,
                        description VARCHAR(255),
                        created_by UUID NOT NULL,
                        created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                        CONSTRAINT fk_group_creator FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE
);

-- 2. Create the group_members join table
CREATE TABLE group_members (
                               group_id UUID NOT NULL,
                               user_id UUID NOT NULL,
                               PRIMARY KEY (group_id, user_id),
                               CONSTRAINT fk_group_members_group FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE CASCADE,
                               CONSTRAINT fk_group_members_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 3. Create the debt_ledgers table
CREATE TABLE debt_ledgers (
                              id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                              lender_id UUID NOT NULL, -- Who paid
                              borrower_id UUID NOT NULL, -- Who owes
                              group_id UUID NOT NULL,
                              amount DECIMAL(12, 2) NOT NULL,
                              created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

                              CONSTRAINT fk_debt_lender FOREIGN KEY (lender_id) REFERENCES users(id) ON DELETE CASCADE,
                              CONSTRAINT fk_debt_borrower FOREIGN KEY (borrower_id) REFERENCES users(id) ON DELETE CASCADE,
                              CONSTRAINT fk_debt_group FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE CASCADE
);

-- 4. Alter the existing expenses table
ALTER TABLE expenses
    ADD COLUMN split_type VARCHAR(50);

ALTER TABLE expenses
    ADD CONSTRAINT fk_expense_group FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE SET NULL;