-- Create users table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    birth_year INT,
    email VARCHAR(255) UNIQUE,
    phone VARCHAR(20),
    address TEXT,
    role ENUM('USER', 'ADMIN') DEFAULT 'USER',
    status ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE',
    failed_login_attempts INT DEFAULT 0,
    lockout_time DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50)
);

-- Create genres table
CREATE TABLE genres (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50)
);

-- Create authors table
CREATE TABLE authors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    birth_year INT,
    death_year INT,
    biography TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50)
);

-- Create bookshelves table
CREATE TABLE bookshelves (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    floor INT,
    section VARCHAR(50),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50)
);

-- Create books table
CREATE TABLE books (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(500) NOT NULL,
    description TEXT,
    publication_year INT,
    edition INT,
    `condition` ENUM('NEW', 'OLD', 'DAMAGED') DEFAULT 'NEW',
    can_borrow BOOLEAN DEFAULT TRUE,
    bookshelf_id BIGINT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    FOREIGN KEY (bookshelf_id) REFERENCES bookshelves(id)
);

-- Create borrow_records table
CREATE TABLE borrow_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    borrow_code VARCHAR(50) UNIQUE NOT NULL,
    borrower_id BIGINT NOT NULL,
    borrow_date DATE,
    deposit DECIMAL(10,2),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    FOREIGN KEY (borrower_id) REFERENCES users(id)
);

-- Create borrow_record_items table
CREATE TABLE borrow_record_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    borrow_record_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    expected_return_date DATE,
    return_date DATE,
    return_status ENUM('NOT_RETURNED', 'RETURNED_ON_TIME', 'RETURNED_LATE', 'DAMAGED') DEFAULT 'NOT_RETURNED',
    FOREIGN KEY (borrow_record_id) REFERENCES borrow_records(id),
    FOREIGN KEY (book_id) REFERENCES books(id)
);

-- Create extension_requests table
CREATE TABLE extension_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    request_code VARCHAR(50) UNIQUE NOT NULL,
    borrow_record_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    reason TEXT,
    status ENUM('CREATED', 'PROCESSING', 'CANCELLED', 'APPROVED', 'REJECTED') DEFAULT 'CREATED',
    processed_at DATETIME,
    processed_by VARCHAR(50),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by VARCHAR(50),
    FOREIGN KEY (borrow_record_id) REFERENCES borrow_records(id),
    FOREIGN KEY (book_id) REFERENCES books(id)
);

-- Create junction tables
CREATE TABLE book_authors (
    book_id BIGINT,
    author_id BIGINT,
    PRIMARY KEY (book_id, author_id),
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    FOREIGN KEY (author_id) REFERENCES authors(id) ON DELETE CASCADE
);

CREATE TABLE book_genres (
    book_id BIGINT,
    genre_id BIGINT,
    PRIMARY KEY (book_id, genre_id),
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    FOREIGN KEY (genre_id) REFERENCES genres(id) ON DELETE CASCADE
);

CREATE TABLE bookshelf_genres (
    bookshelf_id BIGINT,
    genre_id BIGINT,
    PRIMARY KEY (bookshelf_id, genre_id),
    FOREIGN KEY (bookshelf_id) REFERENCES bookshelves(id) ON DELETE CASCADE,
    FOREIGN KEY (genre_id) REFERENCES genres(id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_books_title ON books(title);
CREATE INDEX idx_borrow_records_code ON borrow_records(borrow_code);
CREATE INDEX idx_borrow_records_date ON borrow_records(borrow_date);
CREATE INDEX idx_extension_requests_code ON extension_requests(request_code);
CREATE INDEX idx_extension_requests_status ON extension_requests(status);
