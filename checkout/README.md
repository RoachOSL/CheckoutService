# Checkout Service

## Features

- **Promotion Prioritization:**  
  Promotions are prioritized based on product quantity over bundle promotions.
    - For example:
        - 6 x A will be calculated as `2 x 90 = 180`.
        - 5 x A will be calculated as `90 + 2 x 40 = 170`.

- **Promotion Stacking:**  
  Promotions stack across multiple quantities. We first evaluate the highest quantity of a single product, then consider additional quantities for promotion.

## Running Instructions

The service uses Docker for containerization. By default:
- Docker runs on port `8080`.
- If you wish to run the service directly from an IDE, it will default to port `8081` to avoid conflicts.

### How to Run It?

1. **Create a `.env` by copying `.env.example`:**

   First, use the command below in the terminal to copy `.env.example` and create a `.env` file in your project directory.  
   This file will hold environment variables for your PostgreSQL setup.  
   It should be located in the same directory as `docker-compose.yaml`.  
   After creating the file, replace the placeholder values with your actual PostgreSQL username, password, and any other necessary configuration details.

    ```bash
    cp .env.example .env
    ```

   Alternatively, you can manually insert data in `.env.example` and rename file to `.env`.

2. **Checkout Service Setup:**
    - Open a terminal in the root directory of the `checkout` service.
    - Build the project using Maven:
      ```bash
      ./mvnw clean package -DskipTests
      ```
    - Build the Docker image:
      ```bash
      docker build -t checkout-service:0.0.1-SNAPSHOT -f checkout/Dockerfile checkout
      ```

3. **Running the Docker Container:**
    - Start the service using Docker Compose:
      ```bash
      docker compose up --build
      ```
    - Stop the service:
      ```bash
      docker compose down
      ```
---
