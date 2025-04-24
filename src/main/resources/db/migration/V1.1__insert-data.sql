-- Add some users
INSERT INTO member (role, email, username, password)
VALUES
    ('READER', 'john.doe@example.com', 'john_doe', 'password123'),
    ('READER', 'jane.smith@example.com', 'jane_smith', 'securepass'),
    ('READER', 'mike.johnson@example.com', 'mike_johnson', 'qwerty321'),
    ('WRITER', 'alex.elliot@example.com', 'alex_elliot', 'writerpass'),
    ('WRITER', 'lisa.white@example.com', 'lisa_white', 'author123');

-- Add genres
INSERT INTO genre (name, description)
VALUES
    ('Science Fiction', 'Books about future technologies and space'),
    ('Detective', 'Crime and mystery novels'),
    ('Fantasy', 'Magic, mythical creatures, and magical worlds');

-- Add books by writers (with status)
INSERT INTO book (title, description, characters, price, genre_id, author_id, status)
VALUES
    ('Shadows of Mars', 'Story about the first colonists on Mars.', 120000, 300, 1, 4, 'APPROVED'),
    ('Under the Killer', 'A detective story about a serial killer mystery.', 290000, 250, 2, 4, 'APPROVED'),
    ('Kingdom of Shadows', 'Fantasy epic about the battle between magicians and kings.', 150000, 400, 3, 4, 'APPROVED'),
    ('The Code of Destiny', 'A futuristic thriller about AI.', 510000, 280, 1, 4, 'REJECTED'),
    ('Bastion', 'A futuristic thriller.', 310000, 280, 3, 4, 'PENDING');

-- Add books to readers libraries
INSERT INTO library (reader_id, book_id)
VALUES
    (1, 1), (1, 3), (1, 5),
    (2, 2), (2, 4), (2, 5),
    (3, 1), (3, 2), (3, 3) , (3, 5);