#!/bin/bash

greet()  {
	echo
	echo "Make-a-widget!"
	echo
	echo "A widget is a bundle of Java, HTML, CSS and Javascript." 
	echo "Configurations of a widget called 'instances' are assembled as dashboards"
	echo "This tool will generate a simple widget for you to edit further"
	echo 
	echo "Enter '?' to get specific examples or more info at any prompt"
	echo
	echo
}

descPkg() {
	echo
	echo "Widgets are only deployed as packages"
	echo "Packages can contain multiple widgets and their shared resources"
	echo 
	echo "Example: shapes.Square and shapes.Triangle are two widgets in 'shapes' package"
	echo
}


descName() {
	echo
	echo "Examples: Weather, Search, RecentTrends"
	echo
}

descWidgetDesc() {
	echo
	echo "Example: Current weather conditions"
	echo
}

descConfigDesc() {
	echo
	echo "Examples: "
	echo "    '[not used]'"
	echo "    'city name or zip code'"
	echo "    'JSON String containing settings documented at www.example.com'"
	echo
}

getPkg() {
	echo
	echo "         Step 1: Package"
	echo
	echo "What is your Widget's package?"
	echo  -n "([a-z][a-z0-9]*) [${1}]: "

	read package
	if [ "$package" == "?" ]
		then
		descPkg
		getPkg "$1"
		return
	fi
	if [ "$package" == "" ]
		then

		package="${1}"

		if [ "${1}" == "" ]
			then
			descPkg
			getPkg "$1"
			return
		fi
	fi
}

getName() {
	echo
	echo "         Step 2: Name"
	echo 
	echo "What is the name of your Widget?"
	echo -n "([A-Z][A-Za-z0-9]*) [${1}]: "

	read name
	
	if [ "$name" == "?" ]
		then
		descName
		getName
		return
	fi
	if [ "$name" == "" ]
		then

		name="${1}"

		if [ "${1}" == "" ]
			then
			descName
			getName "$1"
			return
		fi
	fi
}

getFullName() {
	getPkg "$1"
	getName "$2"
	fullName=$package.$name 
	echo
	echo "Widget full name: $fullName"
	echo
	echo -n "Correct? (y/n): "
	read ans
	case "$ans" in
		y|Y|yes|Yes|YES) return ;;
		*) getFullName "$1" "$2" ;;
	esac
}

getWidgetDesc() {
	echo
	echo "         Step 3: One-line Description of Widget"
	echo
	echo -n "Description (or ?): "
	read widgetDesc
	
	if [ "$widgetDesc" == "?" ]
	then
		descWidgetDesc
		getWidgetDesc
		return
	fi
	
	echo
	echo "${fullName}: "
	echo "    ${widgetDesc}"
	echo
	echo -n "Correct? (y/n): "
	read ans
	case "$ans" in
		y|Y|yes|Yes|YES) return ;;
		*) getWidgetDesc ;;
	esac
	
}

getConfigDesc() {
    default="[not used]"
	echo
	echo "         Step 4: One-line Description of config string's meaning"
	echo
	echo -n "Config description [\"${default}\"]: "
	read configDesc
	
	if [ "$configDesc" == "?" ]
	then
		descConfigDesc
		getConfigDesc
		return
	fi
	
	if [ "$configDesc" == "" ]
	then
		configDesc="$default"
	fi
	
	echo "${fullName} Config:"
	echo "    ${configDesc}"
	echo
	echo -n "Correct? (y/n): "
	read ans
	case "$ans" in
		y|Y|yes|Yes|YES) return ;;
		*) getConfigDesc ;;
	esac	
}

pickStylesAndBehavior() {
	echo
	echo "         Step 5: Styles or Behavior?"
	echo
	echo -n "Would you like some example CSS? (y/n): "
	
	read styles
	case "$styles" in
		y|Y|yes|Yes|YES) styles="y" ;;
		*) styles="n" ;;
	esac
	
	echo
	echo -n "Would you like some example JS? (y/n): "
	
	read scripts
	case "$scripts" in
		y|Y|yes|Yes|YES) scripts="y" ;;
		*) scripts="n" ;;
	esac
}

greet
getFullName $1 $2
getWidgetDesc
getConfigDesc
pickStylesAndBehavior

echo
echo "Making widget files..."
mkdir -p "$package"
cd "$package"

echo "    ${package}/${name}.java"

echo "package widgets.$package;" > "$name.java"
echo "public class $name extends widgets.StaticWidget {" >> "$name.java"
echo "    " >> "$name.java"
echo "    @Override" >> "$name.java"
echo "    public String describeWidget() {" >> "$name.java"
echo "         return \"${widgetDesc}\";" >> "$name.java"
echo "    }" >> "$name.java"
echo "    " >> "$name.java"
echo "    @Override" >> "$name.java"
echo "    public String describeConfig() {" >> "$name.java"
echo "         return \"${configDesc}\";" >> "$name.java"
echo "    }" >> "$name.java"
echo "}" >> "$name.java"

echo "    ${package}/${name}.html"

echo "<div id=\"${name}{%WIDGET_ID%}\" class="${name}">" > "$name.html"
echo "<h1>$name</h1>" >> "$name.html"
echo "<p>{%WIDGET_CONFIG%}</p>" >> "$name.html"
echo "</div>"  >> "$name.html"

if [ "$scripts" == "y" ]
then
	echo "<script>Dashboard.register(new ${name}Widget(\"#${name}{%WIDGET_ID%}\"))</script>"  >> "$name.html"

	echo "    ${package}/behavior.js"

	echo "function ${name}Widget( id ) {" > "behavior.js"
	echo "  this.id = id; " >> "behavior.js"
	echo "  this.start = function() { " >> "behavior.js"
	echo "    var text = \$(this.id).find('p').text()" >> "behavior.js"
	echo "    \$(this.id).find('p').text('config: '+text)" >> "behavior.js"
	echo "  }" >> "behavior.js"
	echo "}" >> "behavior.js"
fi

if [ "$styles" == "y" ]
then
echo "    ${package}/styles.css"

echo ".${name} {" > "styles.css"
echo "  color: red;" >> "styles.css"
echo "}" >> "styles.css"
fi


echo 
echo "Now '${package}/' to an widget bundle"
echo
echo "Then browse to http://<dashboards>/widgets?rescan=all"
echo