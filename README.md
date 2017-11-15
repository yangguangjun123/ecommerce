# ecommerce
E-Commerce Project
This project shows how to use MongoDB, Spring and Spring Boot to develop an E-Commerce application. The application includes several fundamental
E-Commerce components: Product Catalog Component and Inventory Management Component. MongoDB 3.4 is used to implement backend data
domain model. The domain model is annotation based POJO classes. A sharded MongoDB cluster is used to provide the storage for the 
product catelog data and inventory data. The product catalog contains more than 100000 products and the products are distributed on the two
MongoDB shards. The data is accessed through MongoDB Java driver through MongoDB route server from the application.
