# AWACS Dashboard platform

AWACS is written in Java, using the Play! framework. A few notes on Play! based applications:
* Java files are compiled on the fly -- you can edit and change files without starting/stopping or recompiling the application.
* The "app" directory of the application or its modules contains executed code.
* Modules are basically miniature Play! applications which can be transparently grafted into a another application: they can have their own 'app', 'lib', etc and those paths are simply searched after the main application's paths when loading files.

A few notes on the dashboard application:
* The app uses its database to store _WidgetInstances_ which are a specific configurations of a _Widget_
* A _Dashboard_ is a collection of WidgetInstances, arranged in columns, optionally with parameters specific to that dashboard (stored as an _Assignment)_.
* Bundles of widgets are packaged as Play! modules so that company-specific widgets are kept separate from distributed widgets.
* The main app has a few "raw" widgets builtin for rendering HTML or Images.
* A _Widget_ is Java class which implements the Widget interface, contained within a package in the _widgets_ package of the application or an added module.
* A Widget can include CSS and JS files in its package directory, to be linked in the <head> of any pages which include an instance of it.
* A Widget controls rendering of its HTML and can setup AJAX/JSON end-points to do server-side processing.

## Example Download / Install Play!
Use your package manager or see Play! documentation for more up-to-date examples,
but as of writing, this was one option:
	curl -O "http://download.playframework.org/releases/play-1.0.3.1.zip"
	unzip play-1.0.3.1.zip
	sudo cp -r play-1.0.3.1 /usr/local/play
	rm play-1.0.3.1.zip
	echo "export PATH=\$PATH:/usr/local/play" >> ~/.profile
	source ~/.profile
	play

## Browse Installed Widgets

http://localhost:9000/widgets displays a gallery of installed widgets as well configured instances, along with links to the sandbox to play with them.

appending "?repair=all" to gallery will delete orphaned instances who's providing widget code is no longer available.

appending ?rescan=all" to the gallery will freshen the widget cache.

## Adding Widget Bundles

Checkout widget module(s) and add to the application. For example:
	cd ~/dashboard
	git clone <path-to-awacs-widgets-local>
	echo "module.awacs-widgets-local=`pwd`/awacs-widgets-local" >> awacs/conf/application.conf

# Making a Simple Widget
	mkdir sandbox && cd sandbox
	/path/to/awacs/examples/widget.sh
	cp -r <package> <widget bundle>/app/widgets/<package>

# Testing a Widget

When developing a widget, a simple action is available to render a specific widget for testing without needing to create a WidgetInstance or add it to a Dashboard:
	http://localhost:9000/sandbox/{className}/{configString}

# About Widgets

* Rendering HTML
When rendering a widget, the dashboard will call the _render_ method of the widget to generate HTML.&nbsp; _BaseWidget_'s implementation will search first for a .html file with the same name as the widget class, then for _widget.html_, in the widget's package.
In the example widget _mywidget.MyClass_ and _mywidget.SomeClass_ will both render from _mywidget/widget.html_ while _mywidget.OtherClass_ with render from _mywidget/OtherClass.html_

* JS and CSS resources
The dashboard will check every widget on a given dashboard for _behavior.js_ and _styles.css_ before rendering a dashboard. If found, these are linked in the <head> tag, and are served using _/styles/<WidgetPackageName>.css_ and _/scripts/<WidgetPackageName>.js_
In the example widget _/styles/mywidget.css_ and _/scripts/mywidget.js_.

* AJAX callbacks
The method _endpoint(endpointName, params)_ of a widget is invoked by the dashboard to reply to requests made to _/endpoint/<widgetPackage>.<WidgetClass>/<endpointName>_. The _Object_ it returns is rendered to the client as JSON using _Gson().toJson()_.
In the example, an a request to _/endpoint/mywidget.MyClass/getDataPoints_ which would result in a call to _mywidget.MyClass("getDataPoints", params)_. See the Play! docs for details on the _params_ argument, but it exposes request parameters.

* Available Javascript Methods:
** Dashboard.register(_somethingToRun_): Drop this in a widget's HTML, and _somethingToRun_'s start() method will be called after everything has loaded. The Dashboard will start widgets gradually to maintain performance.
** Dashboard.warn(_message_): displays _message_ to the user briefly
(i.e. an ajax call timed out)
** Dashboard.fatal(_message_): displays _message_ and reloads the dashboard.
_Use this only when recovery from an error is not likely_ (i.e. malformed JSON indicates potential version change)


### Example Widget Bundle
	app/
	  widgets/
	    mywidget/
	      behavior.js
	      MyClass.java
	      OtherClass.html
	      OtherClass.java
	      SomeClass.java
	      styles.css
	      widget.html
	   myOtherWidget/
	      OtherWidget.java
	      widget.html
