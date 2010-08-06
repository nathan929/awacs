
function FeedWidget(name, url, target, params) {
	this.name = name;
	this.url = url;
	this.target = target;
	this.params = params;
	this.freq = 30*1000;
	
	
	this.start = function() {
		this.update();
	}
	
	this.load = function() {
		var self = this
		$.ajax({
			type : 'GET',
			url : this.url + this.params,
			success : function(a,b,c){ self.fillFeed(a,b,c) },
			error : function(a,b,c){ self.handleError(a,b,c) },
			dataType : 'json'}
		);
	}
	
	this.update = function() {
		if(!stopped) {
			this.load();
			var self = this;

			//add up to 33% of freq to this timeout, so that all the requests don't hit at once
			var wobble = Math.floor( Math.random()*(this.freq/3) );
			
			setTimeout(function() { self.update(); }, this.freq+wobble);
		}
	}
	
	this.handleError = function(xhr, status, err) {
		Dashboard.warn("Could not load "+this.name+" feed<br /><pre>"+status+"</pre>");
	}
	
	this.fillFeed = function(resp, status, xhr) {
		var items = $(this.target).find(".feedwidget-items");
		var recent = items.children(".item").first().attr("id");
		if(resp && recent != resp.items[0].id) {
			if(resp.ordered) {
				$(items).addClass("ordered");
			} else {
				$(items).removeClass("ordered");
			}
			items.children(".item").remove();
			for(var i in resp.items) {
				var k = resp.items[i];

				var item = $("<div></div>");
				item.addClass("item");
				item.attr("id", k.id);
				var content = $("<div></div>");
				content.addClass("body");
				var title = $("<div></div>");
				title.addClass("title");
				
				if(k.title)
					title.html(unescape(k.title));
				if(k.content)
					content.html(unescape(k.content));
				if(k.link) {
					item.addClass("linked");
					var link = $("<a href="+k.link+"></a>");
					link.addClass("interact");
					if(k.title) {
						link.text(k.title)
						link.addClass("title")
						title.addClass("nointeract");
					} else if(k.content) {
						link.text(k.content)
						link.addClass("body")
						content.addClass("nointeract");				
					}
					
					link.appendTo(item);
				}
				title.appendTo(item);
				content.appendTo(item);
				var clear =  $("<div></div>");
				clear.addClass("clear");
				clear.appendTo(item);
				item.appendTo(items);
			}
		}
		$(this.target).find(".feedwidget-last-updated").text(resp.updated)
	}
}



