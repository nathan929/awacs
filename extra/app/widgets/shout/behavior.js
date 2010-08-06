function Shoutbox(channel, url, feed_target, form) {
	this.feed = new FeedWidget(channel, url, feed_target, "");
	this.url = url;
	this.target = form;
	
	this.start = function() {
		this.feed.freq =  10*1000;
		this.feed.start();
		var self = this;
		$("#postshout").click(function() {
			text = $("#shout-text").val();
			author = $("#shout-author").val();
			$.post(self.url, { 'create': true, 'author': author, 'text': text })
			$("#shout-text").val("");
			self.feed.load();
			return false;
		});
		
	}
}