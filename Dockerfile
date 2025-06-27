# Use official Tomcat with JDK
FROM tomcat:9.0-jdk17

# Clean default webapps
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy WAR from dist folder and rename to ROOT.war
COPY dist/SchoolActivityManagementSystem.war /usr/local/tomcat/webapps/ROOT.war

# Expose port 8080
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]
