# Use official Tomcat base image with JDK 17
FROM tomcat:9.0-jdk17

# Clean default deployed apps
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy WAR from dist and rename it to ROOT.war so Tomcat auto-deploys it at root

COPY dist/SchoolActivityManagementSystem.war /usr/local/tomcat/webapps/ROOT.war

# Expose Tomcat's default port
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]
