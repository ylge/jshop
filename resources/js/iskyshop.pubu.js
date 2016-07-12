// JavaScript Document
$(function(){  
    var $container = $('#container');  
  
    $container.imagesLoaded( function(){  
            $container.masonry({  
                itemSelector : '.item',
				isAnimated: true
        });  
    }); 
	
	var pre_size;
    //滚动
    $(window).scroll(function(){
      // 当滚动到最底部以上100像素时， 加载新内容
      if ($(document).height() - $(this).scrollTop() - $(this).height()==0) {
        ajax_load_data();
      }
    });

    function ajax_load_data(){
      var href = $('#page-nav').find('.nextprev').attr('href');
	   var size = $('#page-nav').find('.nextprev').attr('rel');
	  var button = true;
      if(href && size != pre_size&&button){
        pre_size = size;
        $.ajax({
          url:href,
          data:{'size':size},
          dataType:'text',
          type:'post',
		  beforeSend:function(){
            button=false;
          },
          success:function(data){
            if(data){
                var $boxes = $( data );
                $container.append( $boxes ).masonry( 'appended', $boxes);
				button=true;
				var rel = size-0+1;
				$('#page-nav').find('.nextprev').attr('rel',rel);
            }
          }
        });
      }
    }
});  
