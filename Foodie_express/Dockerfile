FROM tomcat:10.1-jdk21

# Expose port 8080 to access the application
EXPOSE 8080

# Copy the WAR file from the target directory of your Maven project to the Tomcat webapps directory
COPY target/Foodie_express.war /usr/local/tomcat/webapps/
