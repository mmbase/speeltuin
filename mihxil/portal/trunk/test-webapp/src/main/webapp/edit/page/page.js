$(function() {
      var draggableOptions =
	  {
	      containment: 'parent',
	      cursor: 'move',
	      grid: [150, 150],
	      start: function(event, ui) {
		  var el = event.target;
                  $(el).removeClass("x0 x1 x2 x3 y0 y1 y2 y4");
	      },
	      stop: function(event, ui) {
		  $(event.target).closest(".mm_portal_content").find(".block").each(
		      function() {
			  var x = $(this).find("span.x");
			  var y = $(this).find("span.y");
			  var position = $(this).position();
			  x.text("" + parseInt(position.left / 150));
			  y.text("" + parseInt(position.top / 150));
		      }
		  );
	      }
          };
      $(".mm_portal_content .block")
	  .draggable(draggableOptions);
      $("select.width")
	  .live("change",
		function() {
		   $(this).closest(".block").removeClass("width1 width2 width3 width4");
		    $(this).closest(".block").addClass("width" + $(this).val());
		});
      $("select.height")
	  .live("change",
		function() {
		    $(this).closest(".block").removeClass("height1 height2 height3 height4");
		    $(this).closest(".block").addClass("height" + $(this).val());
		});

      $("div.mm_related").
	  live("mmsrRelate",
               function (e, tr, relater) {
		   $.get("block.jspx?block=" + relater.getNumber(tr),
			 function(data){
			     var newBlock = $(data);
			     $(".mm_portal_content").append(newBlock);
			     newBlock.draggable(draggableOptions);
			 }
			);
	       }
	      );
  });