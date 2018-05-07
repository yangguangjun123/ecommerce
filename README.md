# Development of E-Commerce Application using MongoDB, Hadoop Big Data and Spring Technologies
This E-Commerce project shows how to use MongoDB, Hadoop Big Data and Spring technologies to develop an E-Commerce
application. The application includes several fundamental E-Commerce components: Product Catalog Component, Inventory
Management Component and Product Category Component. A sharded MongoDB cluster provides the storage for the product
catalog data, product inventory data and other application data. The application domain model is built on MongoDB POJO
classes. A sample database containing more than 100000 products is built to support the E-Commerce application.

Hadoop Big Data technology is used to automatically discover the user insights of the data in the E-Commerce
application. The Hadoop integration is mainly implemented through the development Map/Reduce jobs. The developed
Map/Reduce jobs are run on the MongoDB and Ubuntu platforms.

Spring and Spring Boot technologies are used to provide the system integration platform for the E-Commerce application.
The Spring technologes decouple the different application components and provide flexibility for the application. A demo
of Spring Boot application is also developed.