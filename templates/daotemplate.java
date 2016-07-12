#set ($domain = $!domainName.substring(0,1).toLowerCase()+$!domainName.substring(1))package $!{packageName}.dao;
import org.springframework.stereotype.Repository;
import com.iskyshop.core.base.GenericDAO;
import $!{packageName}.domain.$!{domainName};
@Repository("$!{domain}DAO")
public class $!{domainName}DAO extends GenericDAO<$!{domainName}> {

}