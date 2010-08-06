function DebugWidget(id, params) {
	this.id = id
	this.params = params;
	
	this.start = function() {
		var targ = id;
		if(this.params) {
			targ = this.params;
		}
		$(id).find("dd.target").text(targ);

		
		var matches = $(targ).size();
		var container  = $(id).find(".debugger");
		for (var i = 0; i < matches; i++) {
			target = $(targ).get(i);
			var dl = $("<dl></dl>");
			dl.appendTo(container);

			dl.append( $('<lh></lh>').text( "Match "+(i+1)) );
			

 			$('<dt></dt>').text( "Tag" ).appendTo(dl);
			$('<dd></dd>').text( target.nodeName.toLowerCase() ).appendTo(dl);
			
			$('<dt></dt>').text( "ID" ).appendTo(dl);
			$('<dd></dd>').text( $(target).attr("id") ).appendTo(dl);
			
			$('<dt></dt>').text( "Class" ).appendTo(dl);
			$('<dd></dd>').text( $(target).attr("class") ).appendTo(dl);
			
			var attr = [ "type", "name", "value",];
			for( var k in attr ) { 
				if($(target).attr(attr[k])) {
					$('<dt></dt>').text( attr[k] ).appendTo(dl);
					$('<dd></dd>').text( $(target).attr( attr[k] ) ).appendTo(dl);
				}
			}

			$('<dt></dt>').text( "Width" ).appendTo(dl);
			$('<dd></dd>').text( $(target).width()+"px" ).appendTo(dl);

			$('<dt></dt>').text( "Height" ).appendTo(dl);
			//double set the text to get correct height first
			$('<dd></dd>').text("placeholder").appendTo(dl).text( $(target).height()+"px" );
			
		}
	} 
}
