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
- If you wish to run the service directly from an IDE, it might not work due to a conflict with Docker's port 8080.  
  To avoid conflicts, you can go to `application.yaml` and change the server port to `8081`.

### How to Run It?

1. **Create a `.env` by copying `.env.example`:**

   First, use the command below in the terminal (directory should be in checkout folder in my case it is: `"C:\Users\os199\IdeaProjects\checkout\checkout>
")` to copy `.env.example` and create a `.env` file in your project directory.  
   This file will hold environment variables for your PostgreSQL setup.  
   It should be located in the same directory as `docker-compose.yaml`.  
   After creating the file, replace the placeholder values with your actual PostgreSQL username, password, and any other necessary configuration details.

    ```bash
    cp .env.example .env
    ```

   Alternatively, you can manually insert data in `.env.example` and rename the file to `.env`.

2. **Checkout Service Setup:**
    - Open a terminal in the root directory of the `checkout` service.
    - Build the project using Maven:
      ```bash
      ./mvnw clean package -DskipTests
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

In case of any problems, you can also build the image manually:
- Build the Docker image:
    ```bash
    docker build -t checkout-service:0.0.1-SNAPSHOT -f Dockerfile .
    ```

---

## Endpoints

The Checkout Service exposes a set of RESTful APIs to manage items, promotions, and checkout processes. Below is a list of available endpoints and their functionality:

### 1. **Scanning and Checkout**

- **Scan an Item**  
  **POST** `/checkout/scan?itemId={itemId}`  
  Scans the given item by its ID. Adds the item to the current cart.  
  **Request Parameters:**
    - `itemId` (required): The ID of the item to scan.

- **Get Total Price**  
  **GET** `/checkout/total`  
  Calculates and returns the total price of all scanned items.

- **Finalize Purchase**  
  **POST** `/checkout/finalize`  
  Finalizes the purchase, generates a receipt, and clears the cart.

### 2. **Item Management**

- **Create a New Item**  
  **POST** `/checkout/item`  
  Creates a new item in the database.  
  **Request Body:**
  ```json
  {
    "name": "string",
    "price": "decimal",
  }

### 3. **Promotion Management**

- **Create a Bundle Promotion**  
  **POST** `/checkout/bundle_promotions`  
  Creates a new bundle promotion in the database.

  **Request Body:**
  ```json
  {
    "firstBundleItemId": 1,
    "firstItemRequiredQuantity": 2,
    "secondBundleItemId": 3,
    "secondItemRequiredQuantity": 1,
    "bundlePrice": 20.50
  }

- **Create a Quantity Promotion**  
  **POST** `/checkout/quantity_promotions`  
  Creates a new quantity promotion in the database.

  **Request Body:**
  ```json
  {
  "itemId": 1,
  "requiredQuantity": 3,
  "quantityPromotionPrice": 15.75
  }

### How to Use the API

1. **Start by creating items** using the **Create a New Item** endpoint.
2. **Define promotions** (bundle or quantity) using their respective endpoints.
3. **Scan items** for a checkout session using the **Scan an Item** endpoint.
4. **Use Get Total Price** to calculate the total.
5. **Finalize the purchase** using the **Finalize Purchase** endpoint to generate a receipt.
---
