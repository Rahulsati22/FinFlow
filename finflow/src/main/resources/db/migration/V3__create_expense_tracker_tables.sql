-- 1. Categories Table
CREATE TABLE categories (
                            id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                            user_id UUID, -- If NULL, this is a system-default category available to everyone
                            name VARCHAR(100) NOT NULL,
                            icon VARCHAR(50), -- E.g., a lucide-react icon name or an emoji
                            created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

                            CONSTRAINT fk_category_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 2. The Universal Expenses Table
CREATE TABLE expenses (
                          id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                          user_id UUID NOT NULL, -- Who paid/owns this expense
                          category_id UUID,
                          group_id UUID, -- NULL for personal expenses. We will link this to the 'groups' table in Phase 3.
                          amount DECIMAL(12, 2) NOT NULL,
                          description VARCHAR(255) NOT NULL,
                          expense_date DATE NOT NULL,
                          created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

                          CONSTRAINT fk_expense_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                          CONSTRAINT fk_expense_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL
);

-- 3. Monthly Budgets
CREATE TABLE budgets (
                         id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                         user_id UUID NOT NULL,
                         category_id UUID NOT NULL,
                         amount DECIMAL(12, 2) NOT NULL,
                         month_val INT NOT NULL CHECK (month_val >= 1 AND month_val <= 12),
                         year_val INT NOT NULL,
                         created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

                         CONSTRAINT fk_budget_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                         CONSTRAINT fk_budget_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE,
                         CONSTRAINT uq_user_category_month UNIQUE (user_id, category_id, month_val, year_val) -- 1 budget per category per month
);

-- 4. Savings Goals
CREATE TABLE savings_goals (
                               id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                               user_id UUID NOT NULL,
                               name VARCHAR(100) NOT NULL,
                               target_amount DECIMAL(12, 2) NOT NULL,
                               current_amount DECIMAL(12, 2) DEFAULT 0.00,
                               target_date DATE,
                               created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

                               CONSTRAINT fk_goal_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);


CREATE TABLE incomes (
                         id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                         user_id UUID NOT NULL,
                         source VARCHAR(100) NOT NULL, -- e.g., 'TechCorp Salary', 'Freelance Project'
                         amount DECIMAL(12, 2) NOT NULL,
                         income_date DATE NOT NULL,
                         created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                         CONSTRAINT fk_income_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Indexes for performance (since we will query these often for the dashboard)
CREATE INDEX idx_expenses_user_date ON expenses(user_id, expense_date);
CREATE INDEX idx_budgets_user_date ON budgets(user_id, month_val, year_val);
CREATE INDEX idx_incomes_user_date ON incomes(user_id, income_date);