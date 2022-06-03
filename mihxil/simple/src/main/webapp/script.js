$(document).ready(
    function() {
	$("#menu li").mouseenter(function() { $(this).addClass("hover"); });
	$("#menu li").mouseleave(function() { $(this).removeClass("hover"); });
	$("#menu li").click(
	    function(ev) {
		var li = this;
		document.location.href = $(li).find("a").attr("href");
		ev.preventDefault();
	    });

	var open = function(u, title) {
	    var a = function() {
		window.open(u, title,'toolbar=0,status=0,resizable=1,width=626,height=436');
	    };
	    if (/Firefox/.test(navigator.userAgent)){
		setTimeout(a, 0);
	    } else{
		a();
	    }
	};
	$("div.icons a.facebook").click(
	    function() {
		var u = 'http://www.facebook.com/share.php?src=bm&v=4&i=1273642353&u=' +
		    encodeURIComponent(document.location.href)+'&t='+encodeURIComponent(document.title);
		open(u, 'share on facebook');
		return false;
	    }
	);
	$("div.icons a.twitter").click(
	    function() {
		var u = 'http://twitter.com/home?status=' + encodeURIComponent(document.location.href);
		open(u, 'share on twitter');
		return false;
	    }
	);
	$("div.icons a.mailto").click(
	    function() {
		var u = "mailto:?subject=" + encodeURIComponent(document.title) + "&body=" + encodeURIComponent(document.location.href);
		open(u, 'mail');
		return false;
	    }
	);
	console.log(google.wave);
	var waveid = "w+HrsFv_PGA";
	google.setOnLoadCallback(function() { new google.wave.WavePanel({target: document.getElementById("waveframe")}).loadWave("googlewave.com!" + waveid);});


    });
$(window).load(
    function() {
	var resize =
	    function() {
		var height = $(window).height() - 9;
		var minHeight = $(".intro").height() + $(".footer").height();
		if (height < minHeight) {
		    height = minHeight;
		}
		$(".container").height(height);
		var maxWidth = $(".container").width();
		var neededMenuHeight = $("#menu ul li:last-child").position().top +$("#menu ul li:last-child").height();
		$("#menu").height(neededMenuHeight);
		$(".content").css("top", neededMenuHeight + "px");
		$(".content").height(height - $("#menu").height() - $(".footer").height());
		var width = maxWidth - 200;
		if (width < 430) width = 430;
		$(".content,#menu").width(width);
		$(".footer").width($(".container").width());
		$(".footer").css("top",  ($(".content").height() +  $("#menu").height()) + "px");

	    };

	$(window).resize(resize);
	resize();
	$(".intro").bg(20);
	$("#menu").bg([20,20,0,0]);

	var urchin = $("head meta[name=com.google.urchin]").attr("content");
	try {
	    var pageTracker = _gat._getTracker(urchin);
	    pageTracker._trackPageview();
	} catch(err) {



	}
    }


);