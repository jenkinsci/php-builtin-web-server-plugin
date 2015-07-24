[Jenkins](https://jenkins-ci.org/] plugin to run a [PHP built-in web server](http://php.net/manual/en/features.commandline.webserver.php) for each build.

TODO screenshot here

#### Installation
 * Compile
  * `git clone https://github.com/Fengtan/php-builtin-web-server-plugin`
  * `cd php-builtin-web-server-plugin`
  * `mvn clean install -DskipTests=true`
 * Install (assuming Jenkins runs on http://localhost:8080/)
  * `wget http://localhost:8080/jnlpJars/jenkins-cli.jar`
  * `java -jar jenkins-cli.jar -s http://localhost:8080/ install-plugin ./target/php-builtin-web-server.hpi -restart`
  * Alternatively go to http://localhost:8080/pluginManager/advanced, upload `./target/php-builtin-web-server.hpi` and restart

#### Usage
 * By default, the plugin will use the default location of PHP (`php`). If you want to use an alternate location (e.g. `/usr/bin/php`), go to 'Manage Jenkins > Configure System > PHP > PHP installations'
 * Make sure you use PHP 5.4.0+
 * Create a new project (e.g. 'New Item > Freetyle project')
 * Under 'Build Environment', a checkbox titled 'Run a PHP built-in web server' should show up

#### Dependencies
 * PHP 5.4.0+
