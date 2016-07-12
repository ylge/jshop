package $!{packageName}.command;
import java.util.List;

import com.easyjf.core.support.BaseCommand;

##set ($domain = $!domainName.toLowerCase())
#macro (upperCase $str)
	#set ($upper=$!str.substring(0,1).toUpperCase())
	#set ($l=$!str.substring(1))
$!upper$!l#end
public class $!{domainName}Command extends BaseCommand {
		#foreach($field in $!fields)
			private $!field.getType().getName() $!field.getName();
		#end
		private String parentId="0";// 父级目录
		
		#foreach($field in $!fields)
		public void set#upperCase($!field.getName())($!field.getType().getName() $!field.getName().toLowerCase()){
			this.$!field.getName()=$!field.getName().toLowerCase();
		}
		#end
		
		#foreach($field in $!fields)
		public $!field.getType().getName() get#upperCase($!field.getName())(){
			return this.$!field.getName();
		}
		#end
	
		public List<String> vaild() {
			return super.errors;
		}

}
