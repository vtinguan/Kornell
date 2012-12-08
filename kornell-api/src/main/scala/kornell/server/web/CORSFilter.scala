package com.craftware.kornell.server.web

import javax.servlet.annotation.WebFilter
import javax.servlet.Filter
import javax.servlet.FilterConfig
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletResponse

/**
 * This filter enables requests from other origins, such as the GWT app.
 * 
 * @see http://en.wikipedia.org/wiki/Cross-origin_resource_sharing
 * @see http://caniuse.com/#feat=cors (Does IE suck or what)
 */
class CORSFilter extends Filter {
  var allowedOrigins = ""
  
  override def init(cfg:FilterConfig){
    allowedOrigins = cfg.getInitParameter("allowedOrigins")
  }
  

  override def doFilter(req:ServletRequest, res:ServletResponse, chain:FilterChain){
    val resp = res.asInstanceOf[HttpServletResponse]
    if(allowedOrigins != "") 
      resp.addHeader("Access-Control-Allow-Origin", allowedOrigins)    
    chain.doFilter(req,res)
  }
  
  override def destroy(){}
	 
}