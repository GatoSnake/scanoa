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
	public void init(FilterConfig config) throws ServletException {
	    // Nothing to do here!
	}

	@Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
        	HttpServletRequest req = (HttpServletRequest) request;
        	HttpServletResponse res = (HttpServletResponse) response;
        	HttpSession ses = req.getSession(true);
        	String reqURI = req.getRequestURI();
        	
        	String dominio = null;        	
        	String re1="(\\/)";
            String re2="((?:[a-z][a-z]*[0-9]+[a-z0-9]*))";

            Pattern p = Pattern.compile(re1+re2+re1,Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
            Matcher m = p.matcher(reqURI);
            if (m.find())
            {
                dominio = m.group(1).toString()+m.group(2).toString()+m.group(3).toString();
            }
//            System.out.println("doFilter authfilter: "+reqURI+" "+dominio);
            
            if(ses.getAttribute("correo")!=null && 
            		(reqURI.contains("index.xhtml") || 
            		reqURI.contains("login.xhtml") || 
            		reqURI.contains("register.xhtml") || 
            		reqURI.contains("recoveryAccount.xhtml") || 
            		reqURI.equals(dominio))){
            	res.sendRedirect(req.getContextPath() + "/home.xhtml");
            }
            
            if(reqURI.indexOf("/index.xhtml") >= 0 ||
            		reqURI.indexOf("/register.xhtml") >=0 ||
            		reqURI.indexOf("/login.xhtml") >=0 ||
            		reqURI.indexOf("/recoveryAccount.xhtml") >=0 ||
            		(ses != null && ses.getAttribute("correo") != null) ||
            		reqURI.contains("javax.faces.resource"))
            {
            	chain.doFilter(request, response);
            }else{
            	res.sendRedirect(req.getContextPath() + "/index.xhtml");  // Anonymous user. Redirect to login page  
            }
           
        }catch(Throwable t) {
        	System.out.println( t.getMessage());
        }
    }

	@Override
    public void destroy() {
        // Nothing to do here!
    }  
}