-- REVIEWS TABLE MIGRATION SCRIPT
-- Run this script to add/update the reviews table in your database

CREATE TABLE IF NOT EXISTS reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    storybook_id BIGINT NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    review_text LONGTEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_reviews_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_reviews_storybook_id FOREIGN KEY (storybook_id) REFERENCES storybooks(id) ON DELETE CASCADE,
    
    INDEX idx_storybook_id (storybook_id),
    INDEX idx_user_id (user_id)
);
