-- Insert default admin user (password: admin123)
INSERT INTO users (username, password, first_name, last_name, role, email, created_by) 
VALUES ('admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'System', 'Administrator', 'ADMIN', 'admin@library.com', 'system');

-- Insert sample user (password: user123)
INSERT INTO users (username, password, first_name, last_name, email, phone, address, created_by) 
VALUES ('user1', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Nguyễn', 'Văn A', 'user1@example.com', '0123456789', '123 Đường ABC, Quận 1, TP.HCM', 'admin');

-- Insert sample genres
INSERT INTO genres (name, description, created_by) VALUES 
('Văn học', 'Sách văn học trong và ngoài nước', 'admin'),
('Khoa học', 'Sách khoa học tự nhiên và công nghệ', 'admin'),
('Lịch sử', 'Sách về lịch sử Việt Nam và thế giới', 'admin'),
('Triết học', 'Sách triết học và tư tưởng', 'admin'),
('Kinh tế', 'Sách về kinh tế và quản lý', 'admin'),
('Giáo dục', 'Sách giáo khoa và tham khảo', 'admin'),
('Y học', 'Sách về y học và sức khỏe', 'admin'),
('Công nghệ thông tin', 'Sách về lập trình và công nghệ', 'admin');

-- Insert sample authors
INSERT INTO authors (full_name, birth_year, death_year, biography, created_by) VALUES 
('Nguyễn Du', 1765, 1820, 'Đại thi hào của dân tộc Việt Nam, tác giả của Truyện Kiều', 'admin'),
('Tô Hoài', 1920, 2014, 'Nhà văn nổi tiếng với tác phẩm Dế Mèn phiêu lưu ký', 'admin'),
('Nam Cao', 1915, 1951, 'Nhà văn hiện thực phê phán, tác giả của Chí Phèo', 'admin'),
('Ngô Tất Tố', 1894, 1954, 'Nhà văn với tác phẩm Tắt đèn nổi tiếng', 'admin'),
('Vũ Trọng Phung', 1912, 1939, 'Nhà văn hiện thực với Số đỏ', 'admin');

-- Insert sample bookshelves
INSERT INTO bookshelves (code, floor, section, created_by) VALUES 
('A01', 1, 'A', 'admin'),
('A02', 1, 'A', 'admin'),
('B01', 1, 'B', 'admin'),
('B02', 1, 'B', 'admin'),
('C01', 2, 'C', 'admin'),
('C02', 2, 'C', 'admin'),
('D01', 2, 'D', 'admin'),
('D02', 2, 'D', 'admin');

-- Insert sample books
INSERT INTO books (title, description, publication_year, edition, `condition`, bookshelf_id, created_by) VALUES
('Truyện Kiều', 'Tác phẩm bất hủ của Nguyễn Du về câu chuyện tình yêu bi thương của Thúy Kiều', 1820, 1, 'OLD', 1, 'admin'),
('Dế Mèn phiêu lưu ký', 'Tác phẩm thiếu nhi nổi tiếng của Tô Hoài về cuộc phiêu lưu của chú dế mèn', 1941, 2, 'NEW', 1, 'admin'),
('Chí Phèo', 'Truyện ngắn nổi tiếng của Nam Cao về số phận con người', 1941, 1, 'OLD', 2, 'admin'),
('Tắt đèn', 'Tiểu thuyết của Ngô Tất Tố về đời sống nông thôn', 1939, 1, 'OLD', 2, 'admin'),
('Số đỏ', 'Tiểu thuyết châm biếm của Vũ Trọng Phung', 1936, 1, 'OLD', 3, 'admin'),
('Lão Hạc', 'Truyện ngắn của Nam Cao về tình cha con', 1943, 1, 'NEW', 3, 'admin'),
('Vợ nhặt', 'Truyện ngắn của Kim Lân', 1962, 1, 'NEW', 4, 'admin'),
('Những ngày thơ ấu', 'Hồi ký của Nguyên Hồng', 1938, 1, 'OLD', 4, 'admin');

-- Link books with authors
INSERT INTO book_authors (book_id, author_id) VALUES 
(1, 1), (2, 2), (3, 3), (4, 4), (5, 5), (6, 3);

-- Link books with genres
INSERT INTO book_genres (book_id, genre_id) VALUES 
(1, 1), (2, 1), (3, 1), (4, 1), (5, 1), (6, 1), (7, 1), (8, 1);

-- Link bookshelves with genres
INSERT INTO bookshelf_genres (bookshelf_id, genre_id) VALUES 
(1, 1), (2, 1), (3, 1), (4, 1), (5, 2), (6, 3), (7, 4), (8, 5);

-- Insert sample borrow records
INSERT INTO borrow_records (borrow_code, borrower_id, borrow_date, deposit, created_by) VALUES 
('BR-12345678', 2, '2024-01-15', 50000.00, 'admin'),
('BR-87654321', 2, '2024-01-20', 30000.00, 'admin');

-- Insert sample borrow record items
INSERT INTO borrow_record_items (borrow_record_id, book_id, expected_return_date, return_date, return_status) VALUES 
(1, 1, '2024-02-15', '2024-02-10', 'RETURNED_ON_TIME'),
(1, 2, '2024-02-15', NULL, 'NOT_RETURNED'),
(2, 3, '2024-02-20', NULL, 'NOT_RETURNED');

-- Insert sample extension requests
-- INSERT INTO extension_requests (request_code, borrow_record_id, book_id, reason, status, created_by) VALUES
-- ('ER-12345678', 1, 2, 'Cần thêm thời gian để đọc', 'CREATED', 'user1'),
-- ('ER-87654321', 2, 3, 'Bận việc gia đình', 'PROCESSING', 'user1');

INSERT INTO extension_requests (request_code, borrow_record_id, book_id, reason, status) VALUES
('ER-12345678', 1, 2, 'Cần thêm thời gian để đọc', 'CREATED'),
('ER-87654321', 2, 3, 'Bận việc gia đình', 'PROCESSING');