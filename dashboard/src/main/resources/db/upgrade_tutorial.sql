-- Add page_visits table for tracking tutorial progress
CREATE TABLE IF NOT EXISTS page_visits (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  page_name VARCHAR(50) NOT NULL,
  UNIQUE (user_id, page_name),
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Add index for fast lookups
CREATE INDEX IF NOT EXISTS idx_page_visits_user_id ON page_visits(user_id);
