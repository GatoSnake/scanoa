package filters;
import java.io.IOException;
import java.util.regex.*;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
 
@WebFilter(filterName = "AuthFilter", urlPatterns = {"*.xhtml"})
public class AuthFilter implements Filter {
     
    public AuthFilter() {
    }
 
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
         
    }
 
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
 
        	// check whether session variable is set
        	HttpServletRequest req = (HttpServletRequest) request;
        	HttpServletResponse res = (HttpServletResponse) response;
        	HttpSession ses = req.getSession(true);
        	String reqURI = req.getRequestURI();
        	
        	String re1="(\\/)";	// Any Single Character 1
            String re2="((?:[a-z][a-z]+))";	// Word 1
            String re3="(\\/)";	// Any Single Character 2
            
            String c1 = null;
            String c2 = null;
            String word1 = null;

            Pattern p = Pattern.compile(re1+re2+re3,Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
            Matcher m = p.matcher(reqURI);
            if (m.find())
            {
                c1=m.group(1);
                word1=m.group(2);
                c2=m.group(3);
            }
        	//user is logged in. Login again will redirect  him to home page directly at every attempt.
        	if(ses.getAttribute("correo")!=null && ( reqURI.contains("login.xhtml") || reqURI.contains("recoveryAccount.xhtml") || reqURI.equals(c1.toString()+word1.toString()+c2.toString()) ) )
        	{
        		res.sendRedirect(req.getContextPath() + "/home.xhtml");
        	}
        	// allow user to proccede if url is login.xhtml or user logged in or user is accessing any page in //public folder
        	if ( reqURI.indexOf("/login.xhtml") >= 0 ||
        		reqURI.indexOf("/recoveryAccount.xhtml") >=0 ||
        		reqURI.indexOf("/public/") >= 0 ||
        		(ses != null && ses.getAttribute("correo") != null) ||
        		reqURI.contains("javax.faces.resource"))
            {
        		chain.doFilter(request, response);
            }
        	// user didn't log in but asking for a page that is not allowed so take user to login page
        	else{
            	res.sendRedirect(req.getContextPath() + "/login.xhtml");  // Anonymous user. Redirect to login page  
            }
        }catch(Throwable t) {
        	System.out.println( t.getMessage());
        }
    }

 
    @Override
    public void destroy() {
         
    }
}