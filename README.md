# BookStore 1.0

**The BookStore App allows users to post books with authors, review them, and post comments.**

## BookStore 1.0 Functionality:

1. **Users:**
    - Add new users.
    - Retrieve user details with associated books and comments.
    - Delete users.

2. **Authors:**
    - Add new authors.
    - Retrieve author details with associated books.
    - Delete authors.

3. **Books:**
    - Add new books with associated authors.
    - Retrieve book details with associated author and comments.
    - Delete books.

4. **Comments:**
    - Add comments to books.
    - Retrieve comments on books with associated books and users.
    - Delete comments.

## Testing:

### 1. Servlet Tests:
   - Unit tests for servlets are isolated from the running application to ensure proper functionality and behavior.

### 2. DAO Layer Tests:
   - Integration tests are performed on the DAO layers using Testcontainers
to ensure proper interaction with the database.
   - This ensures data access operations function correctly and maintain data integrity.

## Getting Started:

To set up and run the BookStore application locally, follow these steps:

1. **Clone the Repository:**
    ```bash
    git clone https://github.com/darik666/bookstore.git
    ```

2. **Set Up Database:**
    - Configure the database connection settings.
    - Ensure the database schema is created and accessible.

3. **Build and Run the Application:**
    - Build the application using Maven.
    - Start the application server (e.g., Tomcat) and deploy the application.

4. **Access the Application:**
    - Once the application is running, access it through a web browser
using the specified URL:
- http://localhost:8080/bookstore/users
- http://localhost:8080/bookstore/authors
- http://localhost:8080/bookstore/books
- http://localhost:8080/bookstore/comments

